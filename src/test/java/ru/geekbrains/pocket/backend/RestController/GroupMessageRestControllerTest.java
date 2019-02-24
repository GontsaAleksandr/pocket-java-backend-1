package ru.geekbrains.pocket.backend.RestController;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
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
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.domain.pub.MessageCollection;
import ru.geekbrains.pocket.backend.domain.pub.MessagePub;
import ru.geekbrains.pocket.backend.service.GroupMessageService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class GroupMessageRestControllerTest {

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
    private GroupService groupService;
    @Autowired
    private GroupMessageService groupMessageService;

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

    //=============== group messages get ===============

    @Test
    public void getMessages() throws Exception {
        String offset = "0";
        String textMessage = "Test text message";

        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();

        GroupMessage groupMessage = groupMessageService.createMessage(user, group, textMessage);
        assertNotNull(groupMessage);
        groupMessage = groupMessageService.createMessage(user, group, textMessage + " 2");
        assertNotNull(groupMessage);

        ResultActions result = mockMvc.perform(get("/groups/" + idGroup + "/messages")
                .header("Authorization", "Bearer " + token)
                .param("offset",offset))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.offset", isA(Number.class)))
                .andExpect(jsonPath("$.offset", equalTo(new Integer(offset))))
                .andExpect(jsonPath("$..[0]",  notNullValue()))
        ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        MessageCollection messageCollection = gson.fromJson(r, MessageCollection.class);
        assertNotNull(messageCollection);
    }

    //=============== group message get ===============

    @Test
    public void getMessage() throws Exception {
        String textMessage = "Test text message";

        User user = getUser();
        assertNotNull(user);
        String idUser = user.getId().toString();

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();

        GroupMessage groupMessage = groupMessageService.createMessage(user, group, textMessage);
        assertNotNull(groupMessage);
        groupMessage = groupMessageService.createMessage(user, group, textMessage + " 2");
        assertNotNull(groupMessage);
        String idMessage = groupMessage.getId().toString();

        ResultActions result = mockMvc.perform(get("/groups/" + idGroup + "/messages/" + idMessage)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", equalTo(idMessage)))
                .andExpect(jsonPath("$.text",  equalTo(textMessage + " 2")))
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