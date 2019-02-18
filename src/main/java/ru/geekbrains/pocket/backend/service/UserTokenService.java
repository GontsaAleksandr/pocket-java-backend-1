package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;
import ru.geekbrains.pocket.backend.enumeration.TokenStatus;

import java.util.List;

public interface UserTokenService {

//    PasswordResetToken createPasswordResetTokenForUser(User user, String token);

    UserToken createOrUpdateToken(User user, String userIp);

    List<UserToken> createOrUpdateToken(User user);

    TokenStatus getTokenStatus(String token);

    User getUserByToken(String token);

    UserToken getUserToken(String token);

    UserToken getUserToken(User user, String userIp);

    List<UserToken> getUserToken(User user);

    UserToken getValidToken(User user, String userIp);

    List<UserToken> getValidToken(User user);

    UserToken updateUserToken(UserToken userToken);

    UserToken updateUserToken(String token);

    void deleteUserToken(UserToken userToken);

    void deleteAllUserToken();

//    String validatePasswordResetToken(ObjectId id, String token);

}
