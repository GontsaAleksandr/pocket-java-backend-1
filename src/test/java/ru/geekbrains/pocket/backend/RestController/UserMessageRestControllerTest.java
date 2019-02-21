package ru.geekbrains.pocket.backend.RestController;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.domain.pub.MessageCollection;
import ru.geekbrains.pocket.backend.domain.pub.MessagePub;
import ru.geekbrains.pocket.backend.domain.pub.UserChatCollection;
import ru.geekbrains.pocket.backend.enumeration.MessageType;
import ru.geekbrains.pocket.backend.service.UserChatService;
import ru.geekbrains.pocket.backend.service.UserMessageService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class UserMessageRestControllerTest {

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private UserMessageService userMessageService;

    private MockMvc mockMvc;
    private Gson gson;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    private String token;

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).dispatchOptions(true).build();

        gson = new Gson();
    }

    //=============== user messages ===============

    @Test
    public void getMessages() throws Exception {
        String offset = "0";
        User user = getUser();
        assertNotNull(user);
        String idUser = user.getId().toString();

        ResultActions result = mockMvc.perform(get("/user/" + idUser + "/messages")
                .header("Authorization", "Bearer " + token)
                .param("offset",offset))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.offset", isA(Number.class)))
                .andExpect(jsonPath("$.offset", equalTo(0)))
                .andExpect(jsonPath("$..[0]",  notNullValue()))
        ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        MessageCollection messageCollection = gson.fromJson(r, MessageCollection.class);
        assertNotNull(messageCollection);
    }

    //=============== user message ===============

    @Test
    public void getMessage() throws Exception {
        User user = getUser();
        assertNotNull(user);
        String idUser = user.getId().toString();
        String testMessage = "Test message";
        //add message
        UserMessage userMessage = userMessageService.createMessage(user, user, testMessage);
        assertNotNull(userMessage);
        String idMessage = userMessage.getId().toString();

        ResultActions result = mockMvc.perform(get("/user/" + idUser + "/messages/" + idMessage)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", equalTo(idMessage)))
                //.andExpect(jsonPath("$.type",  equalTo(MessageType.direct)))
                .andExpect(jsonPath("$.text",  equalTo(testMessage)))
                ;

        MessagePub message = gson.fromJson(result.andReturn().getResponse().getContentAsString(),
                MessagePub.class);
        assertNotNull(message);
    }

    private User getUser() {
        userService.delete(email);
        User user = userService.createUserAccount(email, password, username);
        try {
            token = userTokenService.getValidToken(user, "0.0.0.0").getToken();
            log.debug(token);
        } catch (Exception ex) {
            log.debug(ex.getMessage());
        }
        return user;
    }

}