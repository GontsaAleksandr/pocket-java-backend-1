package ru.geekbrains.pocket.backend.service;

import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;

public interface UserChatService {

    List<UserChat> getUserChats(Group group);

    List<UserChat> getUserChats(User user);

    UserChat insert(UserChat userChat);

    void deleteAllUserChats();
}
