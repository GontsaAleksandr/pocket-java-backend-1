package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;

import java.util.List;

public interface UserContactService {

    UserContact createUserContact(UserContact userContact);

    UserContact createUserContact(User user, User contact);

    UserContact createUserContact(User user, User contact, String byname);

    void deleteUsersContact(UserContact contactOnDelete);

    UserContact getUserContact(User user, User contact);

    UserContact getUserContact(User user, ObjectId idContact);

    List<UserContact> getUserContacts(User user);

    UserContact updateUserContact(UserContact userContact);
}
