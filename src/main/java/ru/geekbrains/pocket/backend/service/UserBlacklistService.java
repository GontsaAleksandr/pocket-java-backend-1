package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;

import java.util.List;

public interface UserBlacklistService {

    UserBlacklist createUserBlacklist(UserBlacklist userBlacklist);

    UserBlacklist createUserBlacklist(User user, User banned);

    void deleteUserBlacklist(UserBlacklist userBlacklist);

    UserBlacklist getUserBlacklist(User user, User banned);

    UserBlacklist getUserBlacklist(User user, ObjectId idContact);

    List<UserBlacklist> getUserBlacklists(User user);

    UserBlacklist updateUserBlacklist(UserBlacklist userBlacklist);
}
