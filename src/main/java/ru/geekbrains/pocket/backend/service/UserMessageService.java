package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;

import java.util.List;

public interface UserMessageService {

    UserMessage createMessage(UserMessage userMessage);

    UserMessage createMessage(User sender, User recipient, String text);

    void deleteMessage(UserMessage userMessage);

    void deleteAllMessages();

    UserMessage getMessage(ObjectId id);

    UserMessage getMessage(User sender, User recipient, String text);

    List<UserMessage> getAllMessagesUser(User user);

    List<UserMessage> getMessagesBySender(User sender);

    List<UserMessage> getMessagesByRecipient(User recipient);

    List<UserMessage> getUnreadMessagesFromUser(User sender);

    List<UserMessage> getUnreadMessagesToUser(User recipient);

    UserMessage sendMessageFromTo(User sender, User recipient, String message);

    UserMessage updateMessage(UserMessage userMessage);

}
