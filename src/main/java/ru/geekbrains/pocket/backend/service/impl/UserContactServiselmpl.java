package ru.geekbrains.pocket.backend.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;
import ru.geekbrains.pocket.backend.repository.UserContactRepository;
import ru.geekbrains.pocket.backend.service.UserContactService;

import java.util.List;

@Service
public class UserContactServiselmpl implements UserContactService {
    @Autowired
    private UserContactRepository repository;

    @Override
    public UserContact createUserContact(UserContact userContact) {
        return repository.insert(userContact);
    }

    @Override
    public UserContact createUserContact(User user, User contact) {
        UserContact userContact = repository.findFirstByUserAndContact(user, contact);
        if (userContact == null) {
            userContact = new UserContact(user, contact);
            return repository.insert(userContact);
        } else
            return userContact;
    }

    @Override
    public UserContact createUserContact(User user, User contact, String byname) {
        UserContact userContact = repository.findFirstByUserAndContact(user, contact);
        if (userContact == null) {
            userContact = new UserContact(user, contact, byname);
            return repository.insert(userContact);
        } else
            return userContact;
    }

    @Override
    public void deleteUsersContact(UserContact userContact) {
        repository.delete(userContact);
    }

    @Override
    public UserContact getUserContact(User user, User contact) {
        return repository.findFirstByUserAndContact(user, contact);
    }

    @Override
    public UserContact getUserContact(User user, ObjectId idContact) {
        return repository.findFirstByUserAndContactId(user, idContact);
    }

    @Override
    public List<UserContact> getUserContacts(User user) {
        return repository.findByUser(user);
    }

    @Override
    public UserContact updateUserContact(UserContact userContact) {
        return repository.save(userContact);
    }
}
