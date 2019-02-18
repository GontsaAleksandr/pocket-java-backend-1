package ru.geekbrains.pocket.backend.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.exception.GroupMessageNotFoundException;
import ru.geekbrains.pocket.backend.repository.GroupMessageRepository;
import ru.geekbrains.pocket.backend.service.GroupMessageService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    @Autowired
    private GroupMessageRepository repository;

    @Override
    public GroupMessage createMessage(GroupMessage groupMessage) {
        groupMessage.setSent_at(new Date());
        return repository.insert(groupMessage);
    }

    @Override
    public GroupMessage createMessage(User sender, Group group, String text) {
        GroupMessage messageNew = new GroupMessage(sender, group, text);
        messageNew.setSent_at(new Date());
        return repository.insert(messageNew);    }

    @Override
    public void deleteMessage(GroupMessage groupMessage) {
        repository.delete(groupMessage);
    }

    @Override
    public void deleteAllMessages() {
        repository.deleteAll();
    }

    @Override
    public GroupMessage getMessage(ObjectId id) {
        Optional<GroupMessage> groupMessage = Optional.of(repository.findById(id).orElseThrow(
                () -> new GroupMessageNotFoundException("Group message with id = " + id + " not found")));
        return groupMessage.get();
    }

    @Override
    public GroupMessage getMessage(User sender, Group group, String text) {
        return repository.findFirstBySenderAndGroupAndText(sender, group, text);
    }

    @Override
    public List<GroupMessage> getMessages(User sender) {
        return repository.findBySender(sender);
    }

    @Override
    public List<GroupMessage> getMessages(Group group) {
        return repository.findByGroup(group);
    }

    @Override
    public List<GroupMessage> getMessages(ObjectId id) {
        return repository.findByGroupId(id);
    }

    @Override
    public GroupMessage updateMessage(GroupMessage groupMessage) {
        return repository.save(groupMessage);
    }

}
