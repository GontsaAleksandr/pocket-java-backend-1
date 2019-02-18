package ru.geekbrains.pocket.backend.service;

import com.mongodb.MongoWriteException;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.exception.InvalidOldPasswordException;
import ru.geekbrains.pocket.backend.exception.UserAlreadyExistException;
import ru.geekbrains.pocket.backend.resource.UserResource;

import java.util.List;

public interface UserService {

    User changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    User createUserAccount(String email, String password, String name)
            throws UserAlreadyExistException, DuplicateKeyException, MongoWriteException;

    void delete(ObjectId id);

    void delete(String email);

    void deleteAll();

    List<User> getAllUsers();

    List<UserResource> getAllUserResources();

    User getUserById(ObjectId id);

    User getUserByEmail(String email);

    User getUserByUsername(String userName);

    List<Role> getRolesByUsername(String userName);

    User insert(User user);

    User update(User user) throws DuplicateKeyException, MongoWriteException;

    User updateNameAndPassword(User user, String name, String oldPassword, String newPassword)
            throws InvalidOldPasswordException, DuplicateKeyException, MongoWriteException;

    User validateUser(ObjectId id);

    User validateUser(String username);

}
