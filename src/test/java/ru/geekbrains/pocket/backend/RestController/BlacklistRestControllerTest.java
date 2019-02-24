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
import ru.geekbrains.pocket.backend.controller.rest.BlacklistRestController;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;
import ru.geekbrains.pocket.backend.domain.db.UserChat;
import ru.geekbrains.pocket.backend.domain.pub.UserBlacklistCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserBlacklistPub;
import ru.geekbrains.pocket.backend.service.UserBlacklistService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class BlacklistRestControllerTest {

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBlacklistService userBlacklistService;
    @Autowired
    private UserTokenService userTokenService;

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

    //=============== blacklist get ===============

    @Test
    public void getBlacklist() throws Exception {
        String offset = "0";

        User user = getUser();
        assertNotNull(user);

        UserBlacklist userBlacklist = userBlacklistService.createUserBlacklist(user, user);
        assertNotNull(userBlacklist);
        userBlacklist = userBlacklistService.createUserBlacklist(user, user);
        assertNotNull(userBlacklist);

        ResultActions result = mockMvc.perform(get("/account/blacklist")
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
        UserBlacklistCollection userBlacklistCollection = gson.fromJson(r, UserBlacklistCollection.class);
        assertNotNull(userBlacklistCollection);
    }

    //=============== blacklist post ===============

    @Test
    public void addUserToBlacklist() throws Exception {
        User user = getUser();
        assertNotNull(user);
        String idBanned = user.getId().toString();

        UserBlacklist userBlacklist = userBlacklistService.createUserBlacklist(user, user);
        assertNotNull(userBlacklist);

        BlacklistRestController.AddBannedRequest addBannedRequest =
                new BlacklistRestController.AddBannedRequest(idBanned);
        assertNotNull(addBannedRequest);
        ResultActions result = mockMvc.perform(post("/account/blacklist")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(addBannedRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.user", notNullValue()))
                .andExpect(jsonPath("$.user.id", equalTo(idBanned)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        UserBlacklistPub userBlacklistPub = gson.fromJson(r, UserBlacklistPub.class);
        assertNotNull(userBlacklistPub);
    }

    //=============== blacklist delete ===============

    @Test
    public void deleteUserFromBlacklist() throws Exception {
        User user = getUser();
        assertNotNull(user);
        String idBanned = user.getId().toString();

        UserBlacklist userBlacklist = userBlacklistService.createUserBlacklist(user, user);
        assertNotNull(userBlacklist);

        mockMvc.perform(delete("/account/blacklist/" + idBanned)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
        ;

        userBlacklist = userBlacklistService.getUserBlacklist(user, user);
        assertNull(userBlacklist);
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