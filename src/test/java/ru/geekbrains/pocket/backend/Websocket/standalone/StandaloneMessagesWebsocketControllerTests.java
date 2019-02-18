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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.JsonPathExpectationsHelper;
import ru.geekbrains.pocket.backend.Websocket.support.TestPrincipal;
import ru.geekbrains.pocket.backend.controller.websocket.MessagesWebsocketController;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.service.UserService;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

//Тестирование MessagesWebsocketController с минимальной инфраструктурой,
// необходимой для тестирования аннотированных методов контроллера
// не загружая конфигурацию Spring

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class StandaloneMessagesWebsocketControllerTests {

    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    //	private TestMessageChannel clientOutboundChannel1;
    private TestMessageChannel clientOutboundChannel;

    //	private TestAnnotationMethodHandler annotationMethodHandler1;
    private TestAnnotationMethodHandler annotationMethodHandler;

    private TestUserMessageService testUserMessageService;


    @Before
    public void setup() {

        this.testUserMessageService = new TestUserMessageService();

        MessagesWebsocketController controller = new MessagesWebsocketController(simpMessagingTemplate,
                testUserMessageService, userService);

        //канал получатель
//		this.clientOutboundChannel1 = new TestMessageChannel();
        this.clientOutboundChannel = new TestMessageChannel();

        //канал отправитель

//		this.annotationMethodHandler1 = new TestAnnotationMethodHandler(
//				new TestMessageChannel(), clientOutboundChannel1, new SimpMessagingTemplate(new TestMessageChannel()));
//
//		this.annotationMethodHandler1.registerHandler(controller);
//		this.annotationMethodHandler1.setDestinationPrefixes(Arrays.asList("/app"));
//		this.annotationMethodHandler1.setMessageConverter(new MappingJackson2MessageConverter());
//		this.annotationMethodHandler1.setApplicationContext(new StaticApplicationContext());
//		this.annotationMethodHandler1.afterPropertiesSet();


        this.annotationMethodHandler = new TestAnnotationMethodHandler(
                new TestMessageChannel(), clientOutboundChannel, new SimpMessagingTemplate(new TestMessageChannel()));

        this.annotationMethodHandler.registerHandler(controller);
        this.annotationMethodHandler.setDestinationPrefixes(Arrays.asList("/app"));
        this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
        this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
        this.annotationMethodHandler.afterPropertiesSet();
    }

    //SUBSCRIBE
    //проверка отправки сообщения на подписку и получение ответа
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

        //отправка сообщения
        this.annotationMethodHandler.handleMessage(message);

        //проверяем пришло ли одно сообщение на clientOutboundChannel
        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        //сверяем заголовок полученного сообщения с отправленным
        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/app/testsubscribe", replyHeaders.getDestination());

        //сверяем тело полученного сообщения с отправленным
        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("username").assertValue(json, "Bob");
        new JsonPathExpectationsHelper("email").assertValue(json, "b@b.com");
        new JsonPathExpectationsHelper("text").assertValue(json, "message from bob");
    }


    //SEND
    //Отправить новое сообщение
    //Событие "Новое сообщение"
    @Test
    public void executeClientSendMessage() throws Exception {
        final String textMessage = "test message";
        //тело отправляемого сообщения от клиента
        MessagesWebsocketController.ClientSendMessage clientSendMessage = new MessagesWebsocketController.ClientSendMessage();
        clientSendMessage.setText(textMessage);
        //processMessageFromClient.setGroup("");
        clientSendMessage.setRecipient(userService.getUserByUsername("Alex").getId().toString());

        byte[] payload = new ObjectMapper().writeValueAsBytes(clientSendMessage);

/*		//нужно подписаться на ответ по "/topic/send"
		StompHeaderAccessor headers1 = StompHeaderAccessor.createRoleIfNotFound(StompCommand.SUBSCRIBE);
		headers1.setSubscriptionId("0");
		headers1.setDestination("/app/topic/send");
		headers1.setSessionId("0");
		headers1.setUser(new TestPrincipal("Bob")); //Имя авторизованного юзера
		headers1.setSessionAttributes(new HashMap<>());
		Message<byte[]> message1 = MessageBuilder.withPayload(new byte[0]).setHeaders(headers1).build();

		//отправка сообщения
		this.annotationMethodHandler1.handleMessage(message1);

		//assertEquals(1, this.clientOutboundChannel1.getMessages().size());
		//Message<?> reply1 = this.clientOutboundChannel1.getMessages().get(0);*/

        //заголовок отправляемого сообщения от клиента
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SEND);
        headers.setDestination("/app/send");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Bob"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(payload).setHeaders(headers).build();

        //Отправить новое сообщение
        this.annotationMethodHandler.handleMessage(message);

        //проверяем записалось ли сообщение через UserMessageService
        assertEquals(1, this.testUserMessageService.getUserMessages().size());
        UserMessage actual = this.testUserMessageService.getUserMessages().get(0);

        assertEquals("Bob", actual.getSender().getProfile().getUsername());
        assertEquals("Alex", actual.getRecipient().getProfile().getUsername());
        assertEquals(textMessage, actual.getText());

/*		//=====ОТВЕТ СЕРВЕРА===================================
		//проверяем пришло ли одно сообщение (ответ клиенту отправителю) на clientOutboundChannel
		assertEquals(1, this.clientOutboundChannel.getMessages().size());
		Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

		//сверяем заголовок полученного сообщения с отправленным
		StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
		assertEquals("0", replyHeaders.getSessionId());
		//assertEquals("0", replyHeaders.getSubscriptionId());
		//assertEquals(StompCommand.SUBSCRIBE, replyHeaders.getCommand());
		assertEquals("/app/topic/send", replyHeaders.getDestination());
		//assertEquals("/topic/send", replyHeaders.getDestination());


		//сверяем тело полученного сообщения с отправленным
		String textResponse = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
		assertEquals(textResponse, "12345qq12345");*/

    }


    /**
     * An extension of SimpAnnotationMethodMessageHandler that exposes a (public)
     * method for manually registering a controller, rather than having it
     * auto-discovered in the Spring ApplicationContext.
     */
    private static class TestAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler {

        public TestAnnotationMethodHandler(SubscribableChannel inChannel, MessageChannel outChannel,
                                           SimpMessageSendingOperations brokerTemplate) {

            super(inChannel, outChannel, brokerTemplate);
        }

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }

}
