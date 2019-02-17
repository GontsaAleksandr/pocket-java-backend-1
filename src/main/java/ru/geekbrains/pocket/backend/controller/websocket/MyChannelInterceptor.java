package ru.geekbrains.pocket.backend.controller.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

//for Websocket

public class MyChannelInterceptor implements ChannelInterceptor {

//    @Autowired
//    AuthenticationManager authenticationManager;

    //https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server
    //4.4.13. Token Authentication
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompHeaderAccessor accessor =
//                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();// .getStompCommand();
        if (StompCommand.CONNECT.equals(command)) {
            //Authentication user =  ... ; // access authentication header(s)
            //accessor.setUser(user);
        }


        return message;
    }
}
