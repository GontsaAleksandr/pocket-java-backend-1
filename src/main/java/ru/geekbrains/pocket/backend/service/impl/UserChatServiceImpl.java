package ru.geekbrains.pocket.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;
import ru.geekbrains.pocket.backend.repository.UserChatRepository;
import ru.geekbrains.pocket.backend.service.UserChatService;

import java.util.List;

@Service
public class UserChatServiceImpl implements UserChatService {
    @Autowired
    private UserChatRepository userChatRepository;


    @Override
    public List<UserChat> getUserChats(Group group) {
        return userChatRepository.findByGroup(group);
    }

    @Override
    public List<UserChat> getUserChats(User user) {
        return userChatRepository.findByUser(user);
    }

    @Override
    public UserChat insert(UserChat userChat) {
        return userChatRepository.insert(userChat);
    }

    @Override
    public void deleteAllUserChats() {
        userChatRepository.deleteAll();
    }
}
