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
import ru.geekbrains.pocket.backend.controller.rest.GroupMemberRestController;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberCollection;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberPub;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;
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
public class GroupInviteRestControllerTest {

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    private final String fullname = "Testing Backend";
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
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

    //=============== group invite get ===============

    @Test
    public void getInviteCode() throws Exception {
        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();
        String invitationCode = group.getInvitation_code();

        mockMvc.perform(get("/groups/" + idGroup + "/invites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.active", equalTo(true)))
                .andExpect(jsonPath("$.invitation_code", equalTo(invitationCode)))
                ;
    }

    //=============== group invite post ===============

    @Test
    public void createInviteCode() throws Exception {
        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();
        String invitationCode = group.getInvitation_code();

        mockMvc.perform(post("/groups/" + idGroup + "/invites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.active", equalTo(true)))
                .andExpect(jsonPath("$.invitation_code", notNullValue()))
                .andExpect(jsonPath("$.invitation_code", not(invitationCode)))
                ;
    }

    //=============== group invite delete ===============

    @Test
    public void deleteGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();

        mockMvc.perform(delete("/groups/" + idGroup + "/invites")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
        ;

        group = groupService.getGroup(group.getId());
        assertNotNull(group);
        assertNull(group.getInvitation_code());

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