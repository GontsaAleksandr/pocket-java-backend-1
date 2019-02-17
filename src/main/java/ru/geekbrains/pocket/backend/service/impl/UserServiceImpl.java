package ru.geekbrains.pocket.backend.service.impl;

import org.springframework.dao.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserProfile;
import ru.geekbrains.pocket.backend.exception.InvalidOldPasswordException;
import ru.geekbrains.pocket.backend.exception.RoleNotFoundException;
import ru.geekbrains.pocket.backend.exception.UserAlreadyExistException;
import ru.geekbrains.pocket.backend.exception.UserNotFoundException;
import ru.geekbrains.pocket.backend.repository.RoleRepository;
import ru.geekbrains.pocket.backend.repository.UserRepository;
import ru.geekbrains.pocket.backend.resource.UserResource;
import ru.geekbrains.pocket.backend.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    private final static String ROLE_USER = "ROLE_USER";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public User createUserAccount(String email, String password, String name)
            throws UserAlreadyExistException, DuplicateKeyException, MongoWriteException {
        if (userRepository.findByEmail(email) != null) {
            throw new UserAlreadyExistException("There is an account with that email adress: " + email);
        }

        User user = new User();

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); //получаем хэш пароля
        //user.setUsing2FA(account.isUsing2FA());
        user.setProfile(new UserProfile(name));
        user.setRoles(Arrays.asList(getRoleUser()));
        return userRepository.insert(user);
    }

    @Override
    public void delete(ObjectId id) throws RuntimeException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("User with id = " + id + " not found");
        }
        userRepository.delete(user.get());
    }

    @Override
    public void delete(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserResource> getAllUserResources() {
        return userRepository.findAll()
                .stream()
                .map(UserResource::new)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(ObjectId id) throws RuntimeException {
        //TODO исправить
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id)
                    .orElseThrow(
                            () -> new UserNotFoundException("User with id = " + id + " not found"));
            return user;
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
//        user = Optional.of(user).orElseThrow(
//                () -> new UserNotFoundException("User with email = " + email + " not found"));
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws RuntimeException {
        //User user2 = userRepository.findFirstByUsername(username);
        User user = Optional.of(userRepository.findByProfileUsername(username)).orElseThrow(
                () -> new UserNotFoundException("User with username = '" + username + "' not found"));
        return user;
    }

    @Override
    public List<Role> getRolesByUsername(String username) throws RuntimeException {
        User user = Optional.of(userRepository.findByProfileUsername(username)).orElseThrow(
                () -> new UserNotFoundException("User with username = '" + username + "' not found"));
        return (List<Role>) user.getRoles();
    }

    @Override
    public User insert(User user) throws RuntimeException {
        Role role = Optional.of(roleRepository.findByName("ROLE_USER")).orElseThrow(
                () -> new RoleNotFoundException("Role with name = 'ROLE_USER' not found."));
        user.setRoles(Collections.singletonList(role));
        return userRepository.insert(user);
    }

    private Role getRoleUser() {
        Role roleUser = roleRepository.findByName(ROLE_USER);
        if (roleUser == null)
            roleUser = roleRepository.insert(new Role(ROLE_USER));
        return roleUser;
    }

    @Override
    public User update(User user) throws MongoWriteException {
        try {
            return userRepository.save(user);
        } catch (MongoWriteException ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    @Override
    public User updateNameAndPassword(User user, String name, String oldPassword, String newPassword)
            throws InvalidOldPasswordException {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidOldPasswordException("Old & current password does not match!");
        }
        user.getProfile().setUsername(name);
        user.setPassword(passwordEncoder.encode(newPassword));
        return update(user);
    }

    public User validateUser(ObjectId id) throws UsernameNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id = " + id + " not found"));
    }

    public User validateUser(String username) throws UsernameNotFoundException {
        return Optional.of(userRepository.findByProfileUsername(username)).orElseThrow(
                () -> new UserNotFoundException("User with username = " + username + " not found"));
    }

}
