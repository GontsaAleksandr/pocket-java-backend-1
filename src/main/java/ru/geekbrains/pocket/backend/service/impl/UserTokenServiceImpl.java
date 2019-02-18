package ru.geekbrains.pocket.backend.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;
import ru.geekbrains.pocket.backend.enumeration.TokenStatus;
import ru.geekbrains.pocket.backend.repository.UserRepository;
import ru.geekbrains.pocket.backend.repository.UserTokenRepository;
import ru.geekbrains.pocket.backend.security.token.JwtTokenUtil;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private PasswordResetTokenRepository passwordTokenRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

//    @Override
//    public PasswordResetToken createPasswordResetTokenForUser(User user, String token) {
//        final PasswordResetToken userToken = new PasswordResetToken(token, user);
//        return passwordTokenRepository.save(userToken);
//    }

    @Override
    public UserToken createOrUpdateToken(User user, String userIp) {
        userIp = "0.0.0.0"; //TODO
        if (user == null) return null;
        //final String token = UUID.randomUUID().toString();
        final String newToken = jwtTokenUtil.generateToken(user);
        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(newToken);
        UserToken userToken = userTokenRepository.findFirstByUserAndUserip(user, userIp);
        if (userToken == null) {
            userToken = new UserToken(newToken, user, expiryDate);
            return userTokenRepository.save(userToken);//insert
        } else {
            userToken.setToken(newToken);
            userToken.setExpiryDate(expiryDate);
            return userTokenRepository.save(userToken);
        }
    }

    @Override
    public List<UserToken> createOrUpdateToken(User user) {
//        if (user == null) return null;
//        //final String token = UUID.randomUUID().toString();
//        final String newToken = jwtTokenUtil.generateToken(user);
//        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(newToken);
//        UserToken userToken = userTokenRepository.findByUser(user);
//        if (userToken == null) {
//            userToken = new UserToken(newToken, user, expiryDate);
//            return userTokenRepository.insert(userToken);
//        }
//        else {
//            userToken.setToken(newToken);
//            userToken.setExpiryDate(expiryDate);
//            return userTokenRepository.save(userToken);
//        }
        return null;
    }

    @Override
    public TokenStatus getTokenStatus(String token) {
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
        userRepository.save(user);
        return TokenStatus.VALID;
    }

    @Override
    public User getUserByToken(String token) {
        if (token != null && !token.equals("")) {
            UserToken UserToken = userTokenRepository.findByToken(token);
            if (UserToken != null) {
                return UserToken.getUser();
            }
        }
        return null;
    }

    @Override
    public UserToken getUserToken(String token) {
        return userTokenRepository.findByToken(token);
    }

    @Override
    public UserToken getUserToken(User user, String userIp) {
        return userTokenRepository.findFirstByUserAndUserip(user, userIp);
    }

    @Override
    public List<UserToken> getUserToken(User user) {
        return userTokenRepository.findByUser(user);
    }


    @Override
    public UserToken getValidToken(User user, String userIp) {
        userIp = "0.0.0.0"; //TODO
        //ищем есть ли токен у этого юзера
        UserToken userToken = getUserToken(user, userIp);
        if (userToken != null) {
            //TODO перепроверить
            //проверяем токен
//            if (jwtTokenUtil.getTokenStatus(userToken.getUserToken(), user.getEmail())){
//                //обновляем токен
//                userToken = updateUserToken(userToken);
//            }
//            if (jwtTokenUtil.getExpirationDateFromToken(userToken.getUserToken()).before(new Date())){
//                //обновляем токен
//                userToken = updateUserToken(userToken);
//            }
            if (getTokenStatus(userToken.getToken()).equals(TokenStatus.EXPIRED)) {
                //обновляем токен
                userToken = updateUserToken(userToken);
            }
        } else {
            //токен не найден, создаём новый токен
            userToken = createOrUpdateToken(user, userIp);
        }
        return userToken;
    }

    @Override
    public List<UserToken> getValidToken(User user) {
//        //ищем есть ли токен у этого юзера
//        UserToken userToken = getUserToken(user);
//        if (userToken != null) {
//            //проверяем токен
////            if (jwtTokenUtil.getTokenStatus(userToken.getUserToken(), user.getEmail())){
////                //обновляем токен
////                userToken = updateUserToken(userToken);
////            }
////            if (jwtTokenUtil.getExpirationDateFromToken(userToken.getUserToken()).before(new Date())){
////                //обновляем токен
////                userToken = updateUserToken(userToken);
////            }
//            if (getTokenStatus(userToken.getToken()).equals(TokenStatus.EXPIRED)) {
//                //обновляем токен
//                userToken = updateUserToken(userToken);
//            }
//        } else {
//            //токен не найден, создаём новый токен
//            userToken = createOrUpdateToken(user);
//        }
//        return userToken;
        return null;
    }

    @Override
    public UserToken updateUserToken(UserToken userToken) {
        String newToken = jwtTokenUtil.generateToken(userToken.getUser());
        Date expiryDate = jwtTokenUtil.getExpirationDateFromToken(newToken);
        userToken.updateToken(newToken, expiryDate);
        return userTokenRepository.save(userToken);
    }

    @Override
    public UserToken updateUserToken(String token) {
        UserToken userToken = getUserToken(token);
        if (userToken != null){
            updateUserToken(userToken);
        }
        return null;
    }

    @Override
    public void deleteUserToken(UserToken userToken) {
        userTokenRepository.delete(userToken);
    }

    @Override
    public void deleteAllUserToken() {
        userTokenRepository.deleteAll();
    }

//    @Override
//    public String validatePasswordResetToken(ObjectId id, String token) {
//        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
//        if ((passToken == null) || (passToken.getUser().getId() != id)) {
//            return "invalidToken";
//        }
//
//        final Calendar cal = Calendar.getInstance();
//        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
//            return "expired";
//        }
//
//        final User user = passToken.getUser();
//        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        return null;
//    }

}
