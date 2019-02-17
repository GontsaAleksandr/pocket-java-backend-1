package ru.geekbrains.pocket.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.geekbrains.pocket.backend.controller.websocket.MyChannelInterceptor;

import java.util.List;

//https://o7planning.org/ru/10719/create-a-simple-chat-application-with-spring-boot-and-websocket
//https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server

@Configuration
//@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    //private static final StringMessageConverter MESSAGE_CONVERTER;
//    static {
////        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
////        resolver.setDefaultMimeType(MimeTypeUtils.TEXT_PLAIN);
////
////        MESSAGE_CONVERTER = new StringMessageConverter();
////        MESSAGE_CONVERTER.setContentTypeResolver(resolver);
////    }

    private static final MappingJackson2MessageConverter MESSAGE_CONVERTER =
            new MappingJackson2MessageConverter();

    @Autowired
    private HttpHandshakeInterceptor handshakeInterceptor;

    //https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-server
    //4.2.4. Server Configuration
//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//        return container;
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/", "/queue/");
        registry.setApplicationDestinationPrefixes("/v1");
        //registry.setPreservePublishOrder(true); //публикация сообщения по очереди
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        DefaultHandshakeHandler handler =
                new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy());

        //registry.addEndpoint("/ws");
        registry.addEndpoint("/socket")
                .setHandshakeHandler(handler)
                //.setAllowedOrigins("*")
                .withSockJS()
                .setInterceptors(handshakeInterceptor)
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new MyChannelInterceptor()); //перехват всех сообщений
        registration.taskExecutor().corePoolSize(50);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(MESSAGE_CONVERTER);
        return false;
    }

//    @Override
//    public WebSocketMessageBrokerStats webSocketMessageBrokerStats() {
//        WebSocketMessageBrokerStats stats = super.webSocketMessageBrokerStats();
//        stats.setLoggingPeriod(5 * 1000);
//        return stats;
//    }

//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
//        registration.setSendTimeLimit(15 * 1000).setSendBufferSizeLimit(512 * 1024);
//        registration.setMessageSizeLimit(128 * 1024);
//    }

}
