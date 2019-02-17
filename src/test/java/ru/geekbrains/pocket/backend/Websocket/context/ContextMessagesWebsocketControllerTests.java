/*
 * Copyright 2002-2014 the original author or authors.
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

package ru.geekbrains.pocket.backend.Websocket.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.geekbrains.pocket.backend.Websocket.support.TestPrincipal;
import ru.geekbrains.pocket.backend.controller.websocket.MessagesWebsocketController;
import ru.geekbrains.pocket.backend.service.UserService;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тесте MessagesWebsocketController, который использует Spring TestContext Framework
 * для загрузки актуальной конфигурации Spring.
 * Тест вручную создаёт сообщения, представляющие кадры STOMP, и отправляет их
 * для clientInboundChannel, имитирующего клиентов путем установки идентификатора сеанса
 * и пользовательские заголовки сообщения соответственно.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = {
        ContextMessagesWebsocketControllerTests.TestWebSocketConfig.class,
        ContextMessagesWebsocketControllerTests.TestConfig.class
})
public class ContextMessagesWebsocketControllerTests {

    @Autowired
    private UserService userService;

    @Autowired
    private AbstractSubscribableChannel clientInboundChannel;

    @Autowired
    private AbstractSubscribableChannel clientOutboundChannel;

    @Autowired
    private AbstractSubscribableChannel brokerChannel;

    private TestChannelInterceptor clientOutboundChannelInterceptor;

    private TestChannelInterceptor brokerChannelInterceptor;

    @Before
    public void setup() throws Exception {

        this.brokerChannelInterceptor = new TestChannelInterceptor();
        this.clientOutboundChannelInterceptor = new TestChannelInterceptor();

        this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
        this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
    }


    @Test
    public void getMessageSUBSCRIBE() throws Exception {
        MessagesWebsocketController.TestMessage testMessage =
                new MessagesWebsocketController.TestMessage("Bob!!!", "b@b.com!!!", "message from Bob!!!");
        byte[] payload = new ObjectMapper().writeValueAsBytes(testMessage);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/app/testsubscribe");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Bob")); //Имя авторизованного юзера
        headers.setSessionAttributes(new HashMap<>());
        //Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();

        this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/testsubscribe");
        //отправка сообщения
        this.clientInboundChannel.send(message);

        //получаем ответ от подписки в течении определенного времени
        Message<?> reply = this.clientOutboundChannelInterceptor.awaitMessage(5);
        //проверяем пришло ли сообщение на clientOutboundChannel
        assertNotNull(reply);

        //сверяем заголовок полученного сообщения с отправленным
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/app/testsubscribe", replyHeaders.getDestination());

        //сверяем тело полученного сообщения с отправленным
        //либо с тем что должен отдать контроллер
        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("username").assertValue(json, "Bob");
        new JsonPathExpectationsHelper("email").assertValue(json, "b@b.com");
        new JsonPathExpectationsHelper("text").assertValue(json, "message from bob");
    }

    @Test
    public void executeClientSendMessage() throws Exception {
        final String textMessage = "test message";
        //тело отправляемого сообщения от клиента
        MessagesWebsocketController.ClientSendMessage clientSendMessage = new MessagesWebsocketController.ClientSendMessage();
        clientSendMessage.setText(textMessage);
        //processMessageFromClient.setGroup("");
        clientSendMessage.setRecipient(userService.getUserByUsername("Alex").getId().toString()); //"5c4459823c56f7063c1bc034"); //

        byte[] payload = new ObjectMapper().writeValueAsBytes(clientSendMessage);

        //заголовок отправляемого сообщения от клиента
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/send");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Bob"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());

        //установка endpoint (подписки) для сообщений всем юзерам ???
        this.brokerChannelInterceptor.setIncludedDestinations("/user/**");
        //Отправить новое сообщение
        this.clientInboundChannel.send(message);

        //получаем ответ от подписки в течении определенного времени
        Message<?> messageUpdate = this.brokerChannelInterceptor.awaitMessage(5);
        assertNotNull(messageUpdate);        //проверяем пришло ли сообщение

        //сверяем заголовок полученного сообщения
        StompHeaderAccessor messageUpdateHeaders = StompHeaderAccessor.wrap(messageUpdate);
        assertEquals("/user/Bob/queue/new", messageUpdateHeaders.getDestination());

        //сверяем тело полученного сообщения
        String json = new String((byte[]) messageUpdate.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("").assertValue(json, "Error");
    }

    @Configuration
    @EnableScheduling
//	@ComponentScan(
//			basePackages="ru.geekbrains.pocket.backend",
//			excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
//	)
    @ComponentScan(basePackages = "ru.geekbrains.pocket.backend")
    @EnableWebSocketMessageBroker
    static class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Autowired
        Environment env;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/queue/", "/topic/");
//			registry.enableStompBrokerRelay("/queue/", "/topic/");
            registry.setApplicationDestinationPrefixes("/app");
        }
    }

    /**
     * Класс конфигурации, который отменяет регистрацию MessageHandler, который он находит в
     * ApplicationContext из каналов сообщений, на которые они подписаны ...
     * за исключением обработчика сообщений, используемого для вызова аннотированных методов обработки сообщений.
     * Цель состоит в том, чтобы уменьшить дополнительную обработку и дополнительные сообщения не
     * связанные с тестом.
     */
    @Configuration
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    static class TestConfig implements ApplicationListener<ContextRefreshedEvent> {

        @Autowired
        private List<SubscribableChannel> channels;

        @Autowired
        private List<MessageHandler> handlers;


        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            for (MessageHandler handler : handlers) {
                if (handler instanceof SimpAnnotationMethodMessageHandler) {
                    continue;
                }
                for (SubscribableChannel channel : channels) {
                    channel.unsubscribe(handler);
                }
            }
        }
    }
}