package ru.geekbrains.pocket.backend.controller.websocket;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import ru.geekbrains.pocket.backend.service.UserMessageService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

//https://o7planning.org/ru/10719/create-a-simple-chat-application-with-spring-boot-and-websocket
//https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server

@Log4j2
@Controller
public class TestWebsocketController implements ApplicationListener<BrokerAvailabilityEvent> {

    private AtomicBoolean brokerAvailable = new AtomicBoolean(); //доступность брокера

    private SimpMessagingTemplate simpMessagingTemplate;
    private UserMessageService userMessageService;
    private UserService userService;

    @Autowired
    public TestWebsocketController(SimpMessagingTemplate simpMessagingTemplate,
                                   UserMessageService userMessageService,
                                   UserService userService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userMessageService = userMessageService;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        this.brokerAvailable.set(event.isBrokerAvailable());
    }

    @MessageMapping("/test")
    @SendTo("/topic/test")
    public TestMessage testMessage(@Payload TestMessage testMessage) {
        log.debug("testMessage: " + testMessage.getUsername());// principal.getName());
        System.out.println("testMessage: " + testMessage.getUsername() + ", " + testMessage);
        return new TestMessage(testMessage.getUsername(),
                testMessage.getEmail(), testMessage.getText());
    }

    @SubscribeMapping("/testsubscribe")
    //@SendTo("/topic/testsubscribe")
    public TestMessage testSubscribe(Principal principal, @Payload TestMessage testMessage) {
        log.debug("testSubscribe: " + testMessage.getUsername());
        System.out.println("testSubscribe: " + testMessage.getUsername() + ", " + testMessage + ", " + principal);
//        return new TestMessage(testMessage.getUsername(),
//                testMessage.getEmail(), "testSubscribe: " + testMessage.getText());
        return new TestMessage("Bob", "b@b.com", "message from bob");
    }

    //@Scheduled(fixedDelay=7000)
    public void sendTest() {
        TestMessage user = new TestMessage("TestMessage", "email3", "sendTest");
        if (log.isTraceEnabled()) {
            log.trace("sendTest: " + user);
        }
        if (this.brokerAvailable.get()) {
            log.debug("sendTest: " + user);
            System.out.println("sendTest: " + user);

            this.simpMessagingTemplate.convertAndSend("/topic/test." + "sendTest", user);
        }
    }

    //@Scheduled(fixedDelay=10000)
    public void sendToUserTest() {
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        TestMessage user = new TestMessage("TestMessage", "email4", "sendToUserTest");
        if (this.brokerAvailable.get()) {
            log.debug("sendToUserTest: " + user);
            System.out.println("sendToUserTest: " + user);

            String payload = "sendToUserTest: " + user.getUsername();
            this.simpMessagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/sendToUserTest", payload, map);
        }
    }

    public void sendToUserTestError() {
        TestMessage user = new TestMessage("TestMessage", "email5", "sendToUserTestError");
        if (this.brokerAvailable.get()) {
            log.debug("sendToUserTestError: " + user);
            System.out.println("sendToUserTestError: " + user);

            String payload = "sendToUserTestError: " + user;
            this.simpMessagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/errors", payload);
        }
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

}
