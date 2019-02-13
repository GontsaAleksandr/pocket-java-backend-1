package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.PasswordResetToken;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;
import ru.geekbrains.pocket.backend.enumeration.TokenStatus;
import ru.geekbrains.pocket.backend.exception.TokenNotFoundException;

public interface UserTokenService {

    PasswordResetToken createPasswordResetTokenForUser(User user, String token);

    UserToken createTokenForUser(User user);

    User getUserByToken(String token);

    UserToken getNewToken(User user);

    UserToken getToken(User user);

    UserToken getToken(String token);

    UserToken getValidToken(User user);

    UserToken updateToken(UserToken userToken);

    UserToken updateToken(String token) throws TokenNotFoundException;

    void deleteUserToken(UserToken tokenOnDelete);

    void deleteAllUserToken();

    String validatePasswordResetToken(ObjectId id, String token);

    TokenStatus validateToken(String token);

}
