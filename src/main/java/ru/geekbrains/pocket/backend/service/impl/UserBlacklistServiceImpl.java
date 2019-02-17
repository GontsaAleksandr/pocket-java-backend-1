package ru.geekbrains.pocket.backend.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;
import ru.geekbrains.pocket.backend.repository.UserBlacklistRepository;
import ru.geekbrains.pocket.backend.service.UserBlacklistService;

import java.util.List;

@Service
public class UserBlacklistServiceImpl implements UserBlacklistService {
    @Autowired
    private UserBlacklistRepository repository;

    @Override
    public UserBlacklist createUserBlacklist(UserBlacklist userBlacklist) {
        return repository.insert(userBlacklist);
    }

    @Override
    public UserBlacklist createUserBlacklist(User user, User banned) {
        UserBlacklist userBlacklist = repository.findFirstByUserAndBanned(user, banned);
        if (userBlacklist == null) {
            userBlacklist = new UserBlacklist(user, banned);
            return repository.insert(userBlacklist);
        } else
            return userBlacklist;
    }

    @Override
    public void deleteUserBlacklist(UserBlacklist userBlacklist) {
        repository.delete(userBlacklist);
    }

    @Override
    public UserBlacklist getUserBlacklist(User user, User banned) {
        return repository.findFirstByUserAndBanned(user, banned);
    }

    @Override
    public UserBlacklist getUserBlacklist(User user, ObjectId idBanned) {
        return repository.findFirstByUserAndBannedId(user, idBanned);
    }

    @Override
    public List<UserBlacklist> getUserBlacklists(User user) {
        return repository.findByUser(user);
    }

    @Override
    public UserBlacklist updateUserBlacklist(UserBlacklist userBlacklist) {
        return repository.save(userBlacklist);
    }
}
