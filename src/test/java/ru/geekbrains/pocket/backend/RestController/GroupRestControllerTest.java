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
import ru.geekbrains.pocket.backend.controller.rest.ContactRestController;
import ru.geekbrains.pocket.backend.controller.rest.GroupRestController;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;
import ru.geekbrains.pocket.backend.domain.pub.GroupPub;
import ru.geekbrains.pocket.backend.domain.pub.UserContactCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserContactPub;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.*;

import java.net.URI;
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
public class GroupRestControllerTest {

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

    //=============== group get ===============

    @Test
    public void getGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);
        String idGroup = group.getId().toString();
        String invitationCode = group.getInvitation_code();//если юзер не в группе

        GroupRestController.InvitationCodeRequest invitationCodeRequest =
                new GroupRestController.InvitationCodeRequest(invitationCode);
        assertNotNull(invitationCodeRequest);
        ResultActions result = mockMvc.perform(get("/groups/" + idGroup)
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(invitationCodeRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.creator", equalTo(user.getId().toString())))
                .andExpect(jsonPath("$.invitation_code", equalTo(invitationCode)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupPub groupPub = gson.fromJson(r, GroupPub.class);
        assertNotNull(groupPub);
    }

    //=============== group post ===============

    @Test
    public void createGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        String nameGroup = "TestGroup";

        GroupRestController.NewGroupRequest newGroupRequest =
                new GroupRestController.NewGroupRequest(nameGroup);
        assertNotNull(newGroupRequest);
        ResultActions result = mockMvc.perform(post("/groups")
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(newGroupRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.creator", equalTo(user.getId().toString())))
                .andExpect(jsonPath("$.name", equalTo(nameGroup)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupPub groupPub = gson.fromJson(r, GroupPub.class);
        assertNotNull(groupPub);
    }

    //=============== group put ===============

    @Test
    public void editGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);

//        GroupMember groupMember = groupMemberService.getGroupMember(group, user);
//        assertNotNull(groupMember);
//        groupMember.setRole(RoleGroupMember.administrator);
//        groupMember = groupMemberService.updateGroupMember(groupMember);
//        assertNotNull(groupMember);

        String idGroup = group.getId().toString();
        String nameGroup = group.getName();
        String newNameGroup = "new" + group.getName();

        GroupRestController.NewGroupRequest newGroupRequest =
                new GroupRestController.NewGroupRequest(newNameGroup);
        assertNotNull(newGroupRequest);
        ResultActions result = mockMvc.perform(put("/groups/" + idGroup)
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(newGroupRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.creator", equalTo(user.getId().toString())))
                .andExpect(jsonPath("$.name", equalTo(newNameGroup)))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupPub groupPub = gson.fromJson(r, GroupPub.class);
        assertNotNull(groupPub);
        assertNotEquals(nameGroup,groupPub.getName());
    }

    //=============== group link ===============

    @Test
    public void linkGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);

        GroupMember groupMember = groupMemberService.getGroupMember(group, user);
        assertNotNull(groupMember);
        groupMemberService.deleteGroupMember(groupMember);

        String idGroup = group.getId().toString();
        String invitationCode = group.getInvitation_code();

        GroupRestController.InvitationCodeRequest invitationCodeRequest =
                new GroupRestController.InvitationCodeRequest(invitationCode);
        assertNotNull(invitationCodeRequest);
        URI uri = new URI("/groups/" + idGroup);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.request("link", uri)
                .header("Authorization", "Bearer " + token)
                .content(gson.toJson(invitationCodeRequest))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.creator", equalTo(user.getId().toString())))
                ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        GroupPub groupPub = gson.fromJson(r, GroupPub.class);
        assertNotNull(groupPub);
        assertEquals(idGroup, groupPub.getId());

        groupMember = groupMemberService.getGroupMember(group, user);
        assertNotNull(groupMember);

    }

    //=============== group unlink ===============

    @Test
    public void unlinkGroup() throws Exception {
        User user = getUser();
        assertNotNull(user);
        Group group = groupService.createGroup(user);
        assertNotNull(group);

        GroupMember groupMember = groupMemberService.getGroupMember(group, user);
        assertNotNull(groupMember);

        String idGroup = group.getId().toString();

        URI uri = new URI("/groups/" + idGroup);
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.request("unlink", uri)
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