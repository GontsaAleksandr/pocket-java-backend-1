package ru.geekbrains.pocket.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import static org.springframework.messaging.simp.SimpMessageType.*;

//https://www.baeldung.com/spring-security-websockets

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpTypeMatchers(CONNECT, UNSUBSCRIBE, DISCONNECT).permitAll()
                .simpDestMatchers("/topic", "/topic/**").hasAnyRole("ADMIN","USER")
                .simpDestMatchers("/queue", "/queue/**").hasAnyRole("ADMIN","USER")
                //.simpDestMatchers("/topic/**").authenticated()
                .simpDestMatchers("/send", "/read", "/typing").hasAnyRole("ADMIN","USER")
                //.simpSubscribeDestMatchers("/topic/**").authenticated()
                .anyMessage().authenticated();

// message types other than MESSAGE and SUBSCRIBE
//            .nullDestMatcher().authenticated()
//      .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
//      catch all
//            .anyMessage().denyAll();

    }

//    @Override
//    public void customizeClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new MyChannelInterceptor()); //перехват всех сообщений
//    }

}
