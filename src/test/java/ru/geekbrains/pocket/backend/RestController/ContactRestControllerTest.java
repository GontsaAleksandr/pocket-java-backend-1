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
import ru.geekbrains.pocket.backend.controller.rest.ContactRestController;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;
import ru.geekbrains.pocket.backend.domain.pub.UserContactCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserContactPub;
import ru.geekbrains.pocket.backend.service.UserContactService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@Log4j2
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ContactRestControllerTest {

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    private final String fullname = "Testing Backend";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private UserContactService userContactService;
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

    //=============== contact get ===============

    @Test
    public void getContacts() throws Exception {
        String offset = "0";
        User user = getUser();
        assertNotNull(user);
        UserContact userContact = userContactService.createUserContact(user, userService.getUserByEmail(email), username);
        assertNotNull(userContact);
        userContact = userContactService.createUserContact(user, userService.getUserByEmail(email), username);
        assertNotNull(userContact);

        ResultActions result = mockMvc.perform(get("/account/contacts")
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
        UserContactCollection userContactCollection = gson.fromJson(r, UserContactCollection.class);
        assertNotNull(userContactCollection);
    }

    @Test
    public void getContact() throws Exception {
        User user = getUser();
        assertNotNull(user);
        UserContact userContact = userContactService.createUserContact(user, userService.getUserByEmail(email), username);
        assertNotNull(userContact);
        String idContact = userContact.getContact().getId().toString();

        ResultActions result = mockMvc.perform(get("/account/contacts/" + idContact)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.contact.username", equalTo(username)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        UserContactPub userContactPub = gson.fromJson(r, UserContactPub.class);
        assertNotNull(userContactPub);
        assertEquals(username, userContactPub.getContact().getUsername());
    }

    //=============== contact post ===============

    @Test
    public void addContact() throws Exception {
        User user = getUser();
        assertNotNull(user); //список контактов пуст
        String idContact = user.getId().toString();

        ContactRestController.AddContactRequest addContactRequest =
            new ContactRestController.AddContactRequest(idContact, username);
        assertNotNull(addContactRequest);
        ResultActions result = mockMvc.perform(post("/account/contacts")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(addContactRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.byname", equalTo(username)))
                .andExpect(jsonPath("$.contact.username", equalTo(username)))
        ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        UserContactPub userContactPub = gson.fromJson(r, UserContactPub.class);
        assertNotNull(userContactPub);
        assertEquals(username, userContactPub.getContact().getUsername());
    }

    //=============== contact put ===============

    @Test
    public void editContact() throws Exception {
        String newByname = "newByname";
        User user = getUser();
        assertNotNull(user);
        UserContact userContact = userContactService.createUserContact(user, userService.getUserByEmail(email), username);
        assertNotNull(userContact);
        String idContact = userContact.getContact().getId().toString();

        ContactRestController.EditContactRequest editContactRequest =
                new ContactRestController.EditContactRequest(newByname);
        assertNotNull(editContactRequest);
        ResultActions result = mockMvc.perform(put("/account/contacts/" + idContact)
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(editContactRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.byname", equalTo(newByname)))
                .andExpect(jsonPath("$.contact.username", equalTo(username)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        UserContactPub userContactPub = gson.fromJson(r, UserContactPub.class);
        assertNotNull(userContactPub);
        assertEquals(newByname, userContactPub.getByname());
        assertEquals(username, userContactPub.getContact().getUsername());
    }

    //=============== contact delete ===============

    @Test
    public void deleteContact() throws Exception {
        User user = getUser();
        assertNotNull(user);
        UserContact userContact = userContactService.createUserContact(user, userService.getUserByEmail(email), username);
        assertNotNull(userContact);
        String idContact = userContact.getContact().getId().toString();

        ResultActions result = mockMvc.perform(delete("/account/contacts/" + idContact)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
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