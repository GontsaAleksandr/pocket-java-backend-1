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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.geekbrains.pocket.backend.controller.rest.AccountRestController;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class AccountRestControllerTest {

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    private final String fullname = "Testing Backend";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
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

    //TODO проверка на дубликат username
    //=============== account get ===============

    @Test
    public void account() throws Exception {
        assertNotNull(getUser());

        mockMvc.perform(get("/account")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.email",  equalTo(email)))
                .andExpect(jsonPath("$.profile.username",  equalTo(username)))
        ;
    }

    //=============== account put ===============

    @Test
    public void accountEdit() throws Exception {
        final String newUsername = "NewTesting";
        final String newPassword = "New12345";
        User user = getUser();
        assertNotNull(user);
        String passwordHash = user.getPassword();

        AccountRestController.EditAccountRequest editAccountRequest =
                new AccountRestController.EditAccountRequest(newUsername, password, newPassword);
        assertNotNull(editAccountRequest);
        mockMvc.perform(put("/account")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(editAccountRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.email", equalTo(email)))
                .andExpect(jsonPath("$.profile.username", equalTo(newUsername)))
        ;

        user = userService.getUserByEmail(email);
        assertNotNull(user);
        assertEquals(newUsername, user.getProfile().getUsername()); //имя изменилось
        assertNotEquals(passwordHash, user.getPassword()); //пароль изменился

        //возвращаем данные как были до теста
//        user.getProfile().setUsername(username);
//        user.setPassword(passwordHash);
//        user = userService.update(user);
//        assertNotNull(user);
    }

    @Test
    public void accountEditBadRequestUserName() throws Exception {
        final String newUsername = "T";
        final String newPassword = "New12345";
        User user = getUser();
        assertNotNull(user);
        String passwordHash = user.getPassword();

        AccountRestController.EditAccountRequest editAccountRequest =
                new AccountRestController.EditAccountRequest(newUsername, password, newPassword);
        assertNotNull(editAccountRequest);
        mockMvc.perform(put("/account")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(editAccountRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        user = userService.getUserByEmail(email);
        assertNotNull(user);
        assertNotEquals(newUsername, user.getProfile().getUsername());
        assertEquals(passwordHash, user.getPassword());
    }

    @Test
    public void accountEditBadRequestPassword() throws Exception {
        final String newUsername = "NewTesting";
        final String newPassword = "123";
        User user = getUser();
        assertNotNull(user);
        String passwordHash = user.getPassword();

        AccountRestController.EditAccountRequest editAccountRequest =
                new AccountRestController.EditAccountRequest(newUsername, password, newPassword);
        assertNotNull(editAccountRequest);
        mockMvc.perform(put("/account")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(editAccountRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        user = userService.getUserByEmail(email);
        assertNotNull(user);
        assertNotEquals(newUsername, user.getProfile().getUsername());
        assertEquals(passwordHash, user.getPassword());
    }

    @Test
    public void accountEditBadRequest() throws Exception {
        User user = getUser();
        assertNotNull(user);
        String passwordHash = user.getPassword();

        mockMvc.perform(put("/account")
                .header("Authorization", "Bearer " + token)
                .content("{}")
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        user = userService.getUserByEmail(email);
        assertNotNull(user);
        assertEquals(passwordHash, user.getPassword());
    }

    @Test
    public void accountEditUnauthorized() throws Exception {
        final String newUsername = "NewTesting";
        final String newPassword = "New12345";
        assertNotNull(getUser());

        AccountRestController.EditAccountRequest editAccountRequest =
                new AccountRestController.EditAccountRequest(newUsername, password, newPassword);
        assertNotNull(editAccountRequest);
        mockMvc.perform(put("/account")
                .header("Authorization", "Bearer bad" + token)
                .content(gson.toJson(editAccountRequest))
                .contentType(contentType))
                .andExpect(status().isUnauthorized());

        User user = userService.getUserByEmail(email);
        assertNotNull(user);
        assertNotEquals(newUsername, user.getProfile().getUsername());
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