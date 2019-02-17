package ru.geekbrains.pocket.backend.controller.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Log4j2
@Component
public class WebSocketEventListener {


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        //String username = StompUtils.getHeaderValue("username", headerAccessor);
        //TODO нужен поиск по sessionId с привязкой к юзеру

        if(username != null) {
            log.info("User Disconnected : " + username);
            //TODO ???
            messagingTemplate.convertAndSend("/topic/user", "handleWebSocketDisconnectListener");
        }
    }

}
