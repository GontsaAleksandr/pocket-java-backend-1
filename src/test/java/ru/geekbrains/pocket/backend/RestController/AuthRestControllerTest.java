package ru.geekbrains.pocket.backend.RestController;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.geekbrains.pocket.backend.controller.rest.AuthRestController;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.service.UserService;
import ru.geekbrains.pocket.backend.util.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@SpringBootTest
public class AuthRestControllerTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

//    @Mock
//    UserService mockUserService;
//    @InjectMocks
//    AuthRestController authRestController = new AuthRestController();

    private MockMvc mockMvc;
    private Gson gson;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext)
                .dispatchOptions(true).build();
        //https://habr.com/ru/post/171911/
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .addFilter(springSecurityFilterChain) // добавляем фильтр безопасности
//                .dispatchOptions(true).build();
//       mockMvc = MockMvcBuilders.standaloneSetup(authRestController).build();

        gson = new Gson();
    }

    //=============== login ===============

    @Test
    public void login() throws Exception {
        AuthRestController.LoginRequest loginRequest =
                new AuthRestController.LoginRequest("a@mail.ru","Abc12345");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/auth/login")
                .content(gson.toJson(loginRequest))
                //.param("lastname",'lastname')
                //.with(SecurityRequestPostProcessors.userDetailsService("a@mail.ru")) // добавляем поддержку Security
                .contentType(contentType)
        ;

        ResultActions result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                //.andExpect(content().mimeType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email",  equalTo("a@mail.ru")))
                .andExpect(jsonPath("$.user.profile.username",  equalTo("Alex")))
        ;

        MvcResult mvcResult  = result.andReturn();

    }

    @Test
    public void loginBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                .content("{}")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginBadPassword() throws Exception {
        AuthRestController.LoginRequest loginRequest =
                new AuthRestController.LoginRequest("a@mail.ru","123");
        mockMvc.perform(post("/auth/login")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void loginNotFound() throws Exception {
        AuthRestController.LoginRequest loginRequest =
                new AuthRestController.LoginRequest("notfound@mail.ru","Abc12345");
        mockMvc.perform(post("/auth/login")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isNotFound());

    }

    //=============== registration ===============

    @Test
    public void registration() throws Exception {
        userService.delete("reg_test@mail.ru");

        AuthRestController.RegistrationRequest loginRequest =
                new AuthRestController.RegistrationRequest("reg_test@mail.ru","Abc12345","RegTest");
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email",  equalTo("reg_test@mail.ru")))
                .andExpect(jsonPath("$.user.profile.username",  equalTo("RegTest")))
        ;

        User user = userService.getUserByEmail("reg_test@mail.ru");
        assertNotNull(user);

        userService.delete("reg_test@mail.ru");
    }

    @Test
    public void registrationBadPassword() throws Exception {
        AuthRestController.RegistrationRequest loginRequest =
                new AuthRestController.RegistrationRequest("badpassword@mail.ru","Abc12","badpassword");
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registrationConflictEmail() throws Exception {
        User user = userService.getUserByEmail("reg_test@mail.ru");
        if (user == null)
            user = userService.createUserAccount("reg_test@mail.ru","Abc12345","RegTest");
        assertNotNull(user);

        AuthRestController.RegistrationRequest loginRequest =
                new AuthRestController.RegistrationRequest("reg_test@mail.ru","Abc12345","RegTest");
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isConflict());

        userService.delete("reg_test@mail.ru");
    }

    @Test
    public void registrationConflictUserName() throws Exception {
        userService.delete("reg_test2@mail.ru");
        User user = userService.getUserByEmail("reg_test@mail.ru");
        if (user == null)
            user = userService.createUserAccount("reg_test@mail.ru","Abc12345","RegTest");
        assertNotNull(user);

        AuthRestController.RegistrationRequest loginRequest =
                new AuthRestController.RegistrationRequest("reg_test2@mail.ru","Abc12345","RegTest");
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isConflict());

        userService.delete("reg_test@mail.ru");
        userService.delete("reg_test2@mail.ru");
    }

}