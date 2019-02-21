package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;

public interface UserChatService {

    UserChat createUserChat(UserChat userChat);

    UserChat createUserChat(User user, User direct);

    UserChat createUserChat(User user, User direct, User sender);

    UserChat getUserChat(ObjectId id);

    UserChat getUserChat(User user, Group group);

    UserChat getUserChat(User user, User direct);

    List<UserChat> getUserChats(Group group);

    List<UserChat> getUserChats(User user);

    void deleteAllUserChats();

    void deleteUserChat(ObjectId id);
}
