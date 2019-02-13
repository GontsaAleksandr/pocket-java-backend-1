package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.List;

public interface GroupMessageService {

    GroupMessage createMessage(GroupMessage groupMessage);

    GroupMessage createMessage(User sender, Group group, String text);

    void deleteMessage(GroupMessage groupMessage);

    void deleteAllMessages();

    GroupMessage getMessage(ObjectId id);

    List<GroupMessage> getMessages(User sender);

    List<GroupMessage> getMessages(Group group);

    List<GroupMessage> getMessages(ObjectId id);

    GroupMessage updateMessage(GroupMessage groupMessage);

}
