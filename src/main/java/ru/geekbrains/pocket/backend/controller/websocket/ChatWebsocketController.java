package ru.geekbrains.pocket.backend.controller.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.enumeration.StatusTyping;
import ru.geekbrains.pocket.backend.service.GroupMessageService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.service.UserMessageService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
@Controller
public class ChatWebsocketController implements ApplicationListener<BrokerAvailabilityEvent> {
    private AtomicBoolean brokerAvailable = new AtomicBoolean(); //доступность брокера

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMessageService groupMessageService;


    @MessageMapping("/typing") //Клиент начал либо закончил печатать
    @SendToUser("/queue/typing") //Событие "Новое сообщение"
    public void processTyping(@Payload ClientTypingMessage message,
                              @Header("simpUser") Authentication simpUser) {
        String response = "Error";
        if (message == null || message.getStatus() == null
                || (!message.getStatus().equals(StatusTyping.start)
                & !message.getStatus().equals(StatusTyping.stop))) {
            //TODO проверка на ошибки
            response = "Error message";
        } else {
            User sender = userService.getUserByEmail(simpUser.getName());
            if (sender != null) {
                if (message.getGroup() != null && !message.getGroup().equals("")) {
                    Group group = groupService.getGroup(new ObjectId(message.getGroup()));
                    if (group != null) {
                        ServerTypingMessage serverTypingMessage = new ServerTypingMessage(message.getStatus(),
                                group.getId().toString(), null);
                        return;
                    }
                } else if (message.getRecipient() != null && !message.getRecipient().equals("")) {
                    User recipient = userService.getUserById(new ObjectId(message.getRecipient()));
                    if (recipient != null) {
                        ServerTypingMessage serverTypingMessage = new ServerTypingMessage(message.getStatus(),
                                null, sender.getId().toString());
                        return;
                    }
                }
            }
        }

    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        this.brokerAvailable.set(event.isBrokerAvailable());
    }

    //=========Models request & response=========
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientTypingMessage {
        private StatusTyping status;
        private String group;
        private String recipient;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServerTypingMessage {
        private StatusTyping status;
        private String group;
        private String sender;
    }
}
