/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.geekbrains.pocket.backend.Websocket.standalone;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.service.UserMessageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//симулирует поведение UserMessageService без записи в бд
public class TestUserMessageService implements UserMessageService {

    private final List<UserMessage> userMessages = new ArrayList<>();

    public List<UserMessage> getUserMessages() {
        return userMessages;
    }

    @Override
    public UserMessage createMessage(UserMessage userMessage) {
        ObjectId objectId = new ObjectId();
        userMessage.setId(objectId);
        userMessage.setSent_at(new Date());
        userMessage.setRead(false);
        userMessages.add(userMessage);
        return userMessage;
    }

    @Override
    public UserMessage createMessage(User sender, User recipient, String text) {
        ObjectId objectId = new ObjectId();
        UserMessage userMessage = new UserMessage(sender, recipient, text);
        userMessage.setId(objectId);
        userMessage.setSent_at(new Date());
        userMessage.setRead(false);
        userMessages.add(userMessage);
        return userMessage;
    }

    @Override
    public void deleteMessage(UserMessage userMessage) {

    }

    @Override
    public void deleteAllMessages() {

    }

    @Override
    public UserMessage getMessage(ObjectId id) {
        return null;
    }

    @Override
    public UserMessage getMessage(User sender, User recipient, String text) {
        return null;
    }

    @Override
    public List<UserMessage> getAllMessagesUser(User user) {
        return null;
    }

    @Override
    public List<UserMessage> getMessagesBySender(User sender) {
        return null;
    }

    @Override
    public List<UserMessage> getMessagesByRecipient(User recipient) {
        return null;
    }

    @Override
    public List<UserMessage> getUnreadMessagesFromUser(User sender) {
        return null;
    }

    @Override
    public List<UserMessage> getUnreadMessagesToUser(User recipient) {
        return null;
    }

    @Override
    public UserMessage sendMessageFromTo(User sender, User recipient, String message) {
        return null;
    }

    @Override
    public UserMessage updateMessage(UserMessage userMessage) {
        return null;
    }
}