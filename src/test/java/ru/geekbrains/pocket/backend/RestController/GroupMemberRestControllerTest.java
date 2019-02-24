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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.geekbrains.pocket.backend.controller.rest.GroupMemberRestController;
import ru.geekbrains.pocket.backend.controller.rest.GroupRestController;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberCollection;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberPub;
import ru.geekbrains.pocket.backend.domain.pub.GroupPub;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import java.net.URI;
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
public class GroupMemberRestControllerTest {

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
    private GroupMemberService groupMemberService;
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

    //=============== group members get ===============

    @Test
    public void getGroupMembers() throws Exception {
        String offset = "0";

        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();

        ResultActions result = mockMvc.perform(get("/groups/" + idGroup + "/members")
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
        GroupMemberCollection groupMemberCollection = gson.fromJson(r, GroupMemberCollection.class);
        assertNotNull(groupMemberCollection);
    }

    //=============== group member post ===============

    @Test
    public void createGroupMember() throws Exception {
        User user = getUser();
        assertNotNull(user);

        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();

        GroupMemberRestController.AddGroupMemberRequest addGroupMemberRequest =
                new GroupMemberRestController.AddGroupMemberRequest(user.getId().toString());
        assertNotNull(addGroupMemberRequest);
        ResultActions result = mockMvc.perform(post("/groups/" + idGroup + "/members")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(addGroupMemberRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.user", notNullValue()))
                .andExpect(jsonPath("$.role", equalTo(RoleGroupMember.speacker.toString())))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupMemberPub groupMemberPub = gson.fromJson(r, GroupMemberPub.class);
        assertNotNull(groupMemberPub);
    }

    //=============== group member put ===============

    @Test
    public void editGroupMember() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);

        String idUser = user.getId().toString();
        String idGroup = group.getId().toString();

        ResultActions result = mockMvc.perform(put("/groups/" + idGroup + "/members/" + idUser)
                .header("Authorization", "Bearer " + token)
                .param("role", RoleGroupMember.speacker.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.user", notNullValue()))
                .andExpect(jsonPath("$.role", equalTo(RoleGroupMember.speacker.toString())))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupMemberPub groupMemberPub = gson.fromJson(r, GroupMemberPub.class);
        assertNotNull(groupMemberPub);
        assertEquals(RoleGroupMember.speacker, groupMemberPub.getRole());
    }

    //=============== group member delete ===============

    @Test
    public void deleteGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);

        GroupMember groupMember = groupMemberService.getGroupMember(group, user);
        assertNotNull(groupMember);

        String idUser = user.getId().toString();
        String idGroup = group.getId().toString();

        mockMvc.perform(delete("/groups/" + idGroup + "/members/" + idUser)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                ;

        groupMember = groupMemberService.getGroupMember(group, user);
        assertNull(groupMember);

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