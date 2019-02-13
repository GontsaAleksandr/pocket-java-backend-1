package ru.geekbrains.pocket.backend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.PasswordResetToken;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;
import ru.geekbrains.pocket.backend.enumeration.TokenStatus;
import ru.geekbrains.pocket.backend.exception.TokenNotFoundException;
import ru.geekbrains.pocket.backend.repository.PasswordResetTokenRepository;
import ru.geekbrains.pocket.backend.repository.UserRepository;
import ru.geekbrains.pocket.backend.repository.UserTokenRepository;
import ru.geekbrains.pocket.backend.security.token.JwtTokenUtil;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public PasswordResetToken createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken userToken = new PasswordResetToken(token, user);
        return passwordTokenRepository.save(userToken);
    }

    @Override
    public UserToken createTokenForUser(User user) {
        //final String token = UUID.randomUUID().toString();
        final String token = jwtTokenUtil.generateToken(user);
        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(token);
        final UserToken userToken = new UserToken(token, user, expiryDate);
        return userTokenRepository.save(userToken);
    }

    @Override
    public User getUserByToken(String token) {
        final UserToken UserToken = userTokenRepository.findByToken(token);
        if (UserToken != null) {
            return UserToken.getUser();
        }
        return null;
    }

    @Override
    public UserToken getNewToken(User user) {
        //ищем есть ли токен у этого юзера
        UserToken userToken = getToken(user);
        if (userToken != null) {
            //обновляем токен
            userToken = updateToken(userToken);
        } else {
            //токен не найден, создаём новый токен
            userToken = createTokenForUser(user);
        }
        return userToken;
    }

    @Override
    public UserToken getToken(User user) {
        return userTokenRepository.findByUser(user);
    }

    @Override
    public UserToken getToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    @Override
    public UserToken getValidToken(User user) {
        //ищем есть ли токен у этого юзера
        UserToken userToken = getToken(user);
        if (userToken != null) {
            //проверяем токен
//            if (jwtTokenUtil.validateToken(userToken.getToken(), user.getEmail())){
//                //обновляем токен
//                userToken = updateToken(userToken);
//            }
//            if (jwtTokenUtil.getExpirationDateFromToken(userToken.getToken()).before(new Date())){
//                //обновляем токен
//                userToken = updateToken(userToken);
//            }
            if (validateToken(userToken.getToken()).equals(TokenStatus.EXPIRED)) {
                //обновляем токен
                userToken = updateToken(userToken);
            }
        } else {
            //токен не найден, создаём новый токен
            userToken = createTokenForUser(user);
        }
        return userToken;
    }

    @Override
    public UserToken updateToken(UserToken userToken) {
        String newToken = jwtTokenUtil.generateToken(userToken.getUser());
        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(newToken);
        userToken.updateToken(newToken, expiryDate);
        return userTokenRepository.save(userToken);
    }

    @Override
    public UserToken updateToken(final String token) throws TokenNotFoundException {
        UserToken userToken = Optional.of(userTokenRepository.findByToken(token)).orElseThrow(
                () -> new TokenNotFoundException("Token not found : " + token));
        String newToken = jwtTokenUtil.generateToken(userToken.getUser());
        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(newToken);
        userToken.updateToken(newToken, expiryDate);
        return userTokenRepository.save(userToken);
    }

    @Override
    public void deleteUserToken(UserToken tokenOnDelete) {
    }

    @Override
    public void deleteAllUserToken() {
        userTokenRepository.deleteAll();
    }

    @Override
    public String validatePasswordResetToken(ObjectId id, String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            return "invalidToken";
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "expired";
        }

        final User user = passToken.getUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    @Override
    public TokenStatus validateToken(String token) {
        final UserToken UserToken = userTokenRepository.findByToken(token);
        if (UserToken == null) {
            return TokenStatus.INVALID;
        }

        //TODO use JwtTokenUtil.isTokenExpired
        final User user = UserToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((UserToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                .getTime()) <= 0) {
            userTokenRepository.delete(UserToken);
            return TokenStatus.EXPIRED;
        }

        user.setEnabled(true);
        // userTokenRepository.delete(UserToken);
        userRepository.save(user);
        return TokenStatus.VALID;
    }

}
