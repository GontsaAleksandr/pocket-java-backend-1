package ru.geekbrains.pocket.backend.RestController;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
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
import ru.geekbrains.pocket.backend.controller.rest.AccountRestController;
import ru.geekbrains.pocket.backend.controller.rest.AuthRestController;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;
import ru.geekbrains.pocket.backend.domain.pub.UserChatCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserChatPub;
import ru.geekbrains.pocket.backend.service.UserChatService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ChatRestControllerTest {

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
    private UserChatService userChatService;

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

    //=============== chats get ===============

    @Test
    public void chats() throws Exception {
        String offset = "0";
        assertNotNull(getUser()); //юзер от которого выполняется запрос, нужен для получения токена
        UserChat userChat = userChatService.createUserChat(userService.getUserByEmail(email),
                userService.getUserByEmail(email));
        assertNotNull(userChat);

        ResultActions result = mockMvc.perform(get("/account/chats")
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
        UserChatCollection userChatCollection = gson.fromJson(r, UserChatCollection.class);
        assertNotNull(userChatCollection);
    }

    //=============== chat delete ===============

    @Test
    public void deleteChat() throws Exception {
        assertNotNull(getUser());
        UserChat userChat = userChatService.createUserChat(userService.getUserByEmail(email),
                                                            userService.getUserByEmail(email));
        assertNotNull(userChat);
        String id = userChat.getId().toString();

        mockMvc.perform(delete("/account/chats/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
        ;

        userChat = userChatService.getUserChat(new ObjectId(id));
        assertNull(userChat);
    }

    private User getUser() {
        userService.delete(email);
        User user = userService.createUserAccount(email, password, username);
        try {
            token = userTokenService.getValidToken(user, "0.0.0.0").getToken();
        } catch (Exception ex) {
            log.debug(ex.getMessage());
        }
        return user;
    }

}