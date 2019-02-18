package ru.geekbrains.pocket.backend.controller.websocket;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.domain.pub.MessagePub;
import ru.geekbrains.pocket.backend.service.GroupMessageService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.service.UserMessageService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicBoolean;

//https://o7planning.org/ru/10719/create-a-simple-chat-application-with-spring-boot-and-websocket
//https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server

@Log4j2
@Controller
public class MessagesWebsocketController implements ApplicationListener<BrokerAvailabilityEvent> {

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

    public MessagesWebsocketController(SimpMessagingTemplate simpMessagingTemplate,
                                       UserMessageService userMessageService,
                                       UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userMessageService = userMessageService;
        this.userService = userService;
    }

    @MessageMapping("/send") //Отправить новое сообщение
    @SendToUser("/queue/send") //Событие "Новое сообщение"
    public String processMessageFromClient(@Payload ClientSendMessage message,
                                           //@Headers Map headers,
                                           //@Header("token") String token
                                           @Header("simpUser") Authentication simpUser,
                                           @Header("simpSessionId") String sessionId,
                                           Principal principal) throws Exception {
        String response = "Error";
        //Authentication a = (Authentication) headers.get("simpUser");
        if (message == null || message.getText() == null || message.getText().equals("")) {
            //TODO проверка на ошибки
            response = "Error message";
        } else {
            User sender = userService.getUserByEmail(simpUser.getName());
            if (sender != null) {
                if (message.getGroup() != null && !message.getGroup().equals("")) {
                    Group group = groupService.getGroup(new ObjectId(message.getGroup()));
                    if (group != null) {
                        GroupMessage groupMessage = groupMessageService.createMessage(sender, group, message.getText());
                        response = groupMessage.getId().toString();
                        //TODO отправить сообщение группе
                        //TODO цикл по всем юзерам группы
                        MessagePub messagePub = new MessagePub(groupMessage);
                        this.simpMessagingTemplate.convertAndSend("/queue/new", messagePub);
                    }
                } else if (message.getRecipient() != null && !message.getRecipient().equals("")) {
                    User recipient = userService.getUserById(new ObjectId(message.getRecipient()));
                    if (recipient != null) {
                        UserMessage userMessage = userMessageService.createMessage(sender, recipient, message.getText());
                        response = userMessage.getId().toString();

                        MessagePub messagePub = new MessagePub(userMessage);
                        this.simpMessagingTemplate.convertAndSendToUser(recipient.getEmail(),"/queue/new", messagePub);
                        //this.simpMessagingTemplate.convertAndSendToUser(sessionId,"/queue/new", messagePub);
                    }
                }
            }
        }
        log.warn("processMessageFromClient: " + response);
        return response;
    }

    @MessageMapping("/read") //Прочитал сообщение
    @SendToUser("/queue/read") //Cобытие "Пользователь прочитал сообщение"
    public ServerMessageRead processMessageRead(@Payload ClientMessageRead message,
                                                SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        User sender = userService.getUserById(new ObjectId(message.getSender()));
        if (sender != null) {
            UserMessage userMessage = userMessageService.getMessage(new ObjectId(message.getId()));
            User recipient = userMessage.getRecipient();
            log.warn("processMessageRead: " + message.getSender());
            log.debug("processMessageRead: " + HtmlUtils.htmlEscape(message.getSender()));
            return new ServerMessageRead(message.getId(), recipient.getId().toString());
        }
        return null;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        log.error("handleException: " + exception.getMessage());
        return exception.getMessage();
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
    @ToString
    public static class TestMessage {
        private String username;
        private String email;
        private String text;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientSendMessage {
        private String text;
        private String group;
        private String recipient;
        private String messageid;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ClientMessageRead {
        private String id;
        private String sender;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ServerMessageRead {
        private String id;
        private String recipient;
    }
}

//example
//   @MessageMapping("/myHandler/{username}")
//public void handleTextMessage(@DestinationVariable String username,Message message) {
//        //do something
//        }