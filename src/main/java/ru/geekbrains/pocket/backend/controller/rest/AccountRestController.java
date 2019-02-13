package ru.geekbrains.pocket.backend.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.UserPub;
import ru.geekbrains.pocket.backend.domain.pub.ValidationErrorCollection;
import ru.geekbrains.pocket.backend.exception.InvalidOldPasswordException;
import ru.geekbrains.pocket.backend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@RequestMapping("/v1") config in application.properties
public class AccountRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/account") //Получить информацию о своем аккаунте
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null)
            return new ResponseEntity<>(new UserPub(user), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(path = "/account", consumes = "application/json") //Изменить данные аккаунта
    public ResponseEntity<?> editAccount(@Valid @RequestBody EditAccountRequest editAccountRequest,
                                         HttpServletRequest request,
                                         BindingResult result, WebRequest webRequest, Errors errors) {
        if (editAccountRequest.getOldPassword().equals(editAccountRequest.getNewPassword())) {
            log.debug("Old & new password is match!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = httpRequestComponent.getUserFromToken(request);
        if (user == null) {
            ValidationErrorCollection validationErrorCollection = new ValidationErrorCollection();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            user = userService.updateNameAndPassword(user, editAccountRequest.getName(),
                    editAccountRequest.getOldPassword(), editAccountRequest.getNewPassword());
        } catch (InvalidOldPasswordException ex) {
            log.debug("Old & current password does not match!");
            log.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new UserPub(user), HttpStatus.OK);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EditAccountRequest {
        @NotNull
        @Size(min = 2, max = 32)
        private String name;
        @NotNull
        @Size(min = 8, max = 32)
        private String oldPassword;
        @NotNull
        @Size(min = 8, max = 32)
        private String newPassword;
    }
}
