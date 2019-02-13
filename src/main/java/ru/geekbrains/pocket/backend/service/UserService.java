package ru.geekbrains.pocket.backend.service;

import com.mongodb.MongoServerException;
import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserProfile;
import ru.geekbrains.pocket.backend.exception.InvalidOldPasswordException;
import ru.geekbrains.pocket.backend.exception.UserAlreadyExistException;
import ru.geekbrains.pocket.backend.resource.UserResource;

import java.util.Date;
import java.util.List;

public interface UserService { //extends UserDetailsService {

    User changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    void delete(ObjectId id);

    void delete(String email);

    List<User> getAllUsers();

    List<UserResource> getAllUserResources();

    User getUserById(ObjectId id);

    User getUserByEmail(String email);

    User getUserByUsername(String userName);

    List<Role> getRolesByUsername(String userName);

    User insert(User user);

    User createUserAccount(String email, String password, String name)
            throws UserAlreadyExistException, MongoServerException;

    User update(User user);

    String updateUserProfile(User user, UserProfile userProfile);

    String updateUserFullName(User user, String firstName);

    String updateUserUsername(User user, String lastName);

    String updateUsersLastSeen(User user, Date date);

    User updateNameAndPassword(User user, String name, String oldPassword, String newPassword)
            throws InvalidOldPasswordException;

    User validateUser(ObjectId id);

    User validateUser(String username);

}
