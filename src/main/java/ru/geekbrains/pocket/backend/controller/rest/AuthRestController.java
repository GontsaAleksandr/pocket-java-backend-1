package ru.geekbrains.pocket.backend.controller.rest;

import com.mongodb.MongoServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.geekbrains.pocket.backend.domain.db.Privilege;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;
import ru.geekbrains.pocket.backend.domain.pub.UserPub;
import ru.geekbrains.pocket.backend.enumeration.TokenStatus;
import ru.geekbrains.pocket.backend.exception.UserAlreadyExistException;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;
import ru.geekbrains.pocket.backend.util.validation.ValidEmail;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    //отправка электронного письма с запросом подтверждения email
//    @Autowired
//    ApplicationEventPublisher eventPublisher;
    @Autowired
    private MessageSource messages;
//    @Autowired
//    private JavaMailSender mailSender;
    @Autowired
    private Environment env;


    @PostMapping(path = "/login", consumes = "application/json")// produces = "application/json;charset=UTF-8")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        //TODO validate
        User user = userService.getUserByEmail(loginRequest.getEmail());
        if (user == null) {
            log.debug("User not exists.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.debug("Password does not match");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //ищем есть ли токен у этого юзера
        UserToken userToken = userTokenService.getValidToken(user);

        try {
            authWithoutPassword(user);
        } catch (AuthenticationException ex){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(new RegistrationResponse(userToken.getToken(), new UserPub(userToken.getUser())), HttpStatus.OK);
    }

    @PostMapping(path = "/registration", consumes = "application/json")
    public ResponseEntity<?> registration(@Valid @RequestBody RegistrationRequest registrationRequest,
                                          final HttpServletRequest request,
                                          BindingResult result, WebRequest webRequest, Errors errors)
            throws AuthenticationException {
        //TODO validate
        //If error, just return a 400 bad request, along with the error message
        if (result.hasErrors()) {
            final Map<String, Object> response = new HashMap<>();
            response.put("message", "Your request contains errors");
            response.put("errors", result.getAllErrors()
                    .stream()
                    .map(x -> String.format("%s : %s", x.getCode(), x.getDefaultMessage()))
                    .collect(Collectors.toList()));
            //return ResponseEntity.badRequest().body(response);
        }

        log.debug("Processing registration form for: " + registrationRequest);

        User user;
        try {
            user = userService.createUserAccount(registrationRequest.getEmail(),
                    registrationRequest.getPassword(),
                    registrationRequest.getName());
        } catch (UserAlreadyExistException e) {
            log.debug("Email already exists.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (MongoServerException e) {// MongoWriteException, DuplicateKeyException
            log.debug("Email write to db user " + registrationRequest);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        UserToken userToken = userTokenService.getNewToken(user);

        try {
            authWithoutPassword(user);
        } catch (AuthenticationException ex){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //отправка электронного письма с запросом подтверждения email
//        try {
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent
//                (registered, request.getLocale(), getAppUrl(request)));
//        } catch (Exception me) {
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
        log.debug("Successfully created user: " + user.getEmail() + " and token " + userToken.getToken());

        return new ResponseEntity<>(new RegistrationResponse(userToken.getToken(), new UserPub(userToken.getUser())), HttpStatus.CREATED);

    }

    //https://www.baeldung.com/registration-verify-user-by-email
    @GetMapping("/registrationConfirm")
    public ResponseEntity<?> confirmRegistration(final HttpServletRequest request, @RequestParam("token") final String token)
            throws UnsupportedEncodingException {
        Locale locale = request.getLocale();
        final TokenStatus result = userTokenService.validateToken(token);
        if (result.equals(TokenStatus.VALID)) {
            final User user = userTokenService.getUserByToken(token);
            // if (user.isUsing2FA()) {
            // model.addAttribute("qr", userService.generateQRUrl(user));
            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            // }
            authWithoutPassword(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        //Пользователь будет перенаправлен на страницу ошибки с соответствующим сообщением, если:
        // - UserToken не существует по какой-либо причине или
        // - Срок действия UserToken истек
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail
            (final String contextPath, final Locale locale, final UserToken newToken, final User user) {
        String confirmationUrl =
                contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
        String message = messages.getMessage("message.resendToken", null, locale);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " rn" + confirmationUrl);
        email.setFrom(env.getProperty("support.email"));
        email.setTo(user.getEmail());
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    //аутентификация в Spring
    public void authWithoutPassword(User user) throws AuthenticationException {
        List<Privilege> privileges = user.getRoles().stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<GrantedAuthority> authorities = privileges.stream().map(
                p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void authWithPassword(User user) throws AuthenticationException {
        List<Privilege> privileges = user.getRoles().stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<GrantedAuthority> authorities = privileges.stream().map(
                p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toList());

        //https://www.baeldung.com/manually-set-user-authentication-spring-security
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), authorities);
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

//        HttpSession session = request.getSession(true);
//        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
    }

    //===== Request & Response =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LoginRequest {

        @NotNull
        @ValidEmail
        @Size(min = 6, max = 32)
        private String email;
        @NotNull
        @Size(min = 8, max = 32)
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class RegistrationRequest {
        @NotNull
        @ValidEmail //(message = "email names must comply with the standard")
        @Size(min = 6, max = 32)
        private String email;
        @NotNull
        @Size(min = 8, max = 32)
        private String password;
        @NotNull
        @Size(min = 2, max = 32)
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RegistrationResponse {
        private String token;
        private UserPub user;
    }

}
