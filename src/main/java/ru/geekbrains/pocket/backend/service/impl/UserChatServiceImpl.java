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
    private UserChatRepository repository;

    @Override
    public UserChat createUserChat(UserChat userChat) {
        return repository.insert(userChat);
    }

    @Override
    public UserChat createUserChat(User user, User direct, User sender) {
        return repository.insert(new UserChat(user, direct, sender));
    }

    @Override
    public UserChat getUserChat(User user, Group group) {
        return repository.findFirstByUserAndGroup(user, group);
    }

    @Override
    public UserChat getUserChat(User user, User direct) {
        return repository.findFirstByUserAndDirect(user, direct);
    }

    @Override
    public List<UserChat> getUserChats(Group group) {
        return repository.findByGroup(group);
    }

    @Override
    public List<UserChat> getUserChats(User user) {
        return repository.findByUser(user);
    }

    @Override
    public void deleteAllUserChats() {
        repository.deleteAll();
    }
}
