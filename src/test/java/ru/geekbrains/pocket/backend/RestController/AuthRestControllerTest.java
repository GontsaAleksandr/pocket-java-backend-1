package ru.geekbrains.pocket.backend.RestController;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
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
import ru.geekbrains.pocket.backend.util.ConverterJSON;
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

@Log4j2
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

    private final String email = "testing@mail.ru";
    private final String password = "Abc12345";
    private final String username = "Testing";
    private final String fullname = "Testing Backend";

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
        assertNotNull(getUser());

        AuthRestController.LoginRequest loginRequest =
                new AuthRestController.LoginRequest(email,password);
        assertNotNull(loginRequest);
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
                .andExpect(jsonPath("$.user.email",  equalTo(email)))
                .andExpect(jsonPath("$.user.profile.username",  equalTo(username)))
        ;

        MvcResult mvcResult  = result.andReturn();
        String r = mvcResult.getResponse().getContentAsString();
        AuthRestController.RegistrationResponse registrationResponse =
                gson.fromJson(r, AuthRestController.RegistrationResponse.class);
        String token = registrationResponse.getToken();
        log.debug("token : " + token);
        System.out.println("token : " + token);
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
                new AuthRestController.LoginRequest(email,"BadPass");
        mockMvc.perform(post("/auth/login")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void loginNotFound() throws Exception {
        AuthRestController.LoginRequest loginRequest =
                new AuthRestController.LoginRequest("notfound@mail.ru", password);
        mockMvc.perform(post("/auth/login")
                .content(gson.toJson(loginRequest))
                .contentType(contentType))
                .andExpect(status().isNotFound());

    }

    //=============== registration ===============

    @Test
    public void registrationAndNotDelete() throws Exception {
        userService.delete(email);

        AuthRestController.RegistrationRequest registrationRequest =
                new AuthRestController.RegistrationRequest(email, password, username);
        assertNotNull(registrationRequest);
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(registrationRequest))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email",  equalTo(email)))
                .andExpect(jsonPath("$.user.profile.username",  equalTo(username)))
        ;

        User user = userService.getUserByEmail(email);
        assertNotNull(user);
    }

    @Test
    public void registration() throws Exception {
        userService.delete(email);

        AuthRestController.RegistrationRequest registrationRequest =
                new AuthRestController.RegistrationRequest(email,password,username);
        assertNotNull(registrationRequest);
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(registrationRequest))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.email",  equalTo(email)))
                .andExpect(jsonPath("$.user.profile.username",  equalTo(username)))
        ;

        User user = userService.getUserByEmail(email);
        assertNotNull(user);
    }

    @Test
    public void registrationBadPassword() throws Exception {
        AuthRestController.RegistrationRequest registrationRequest =
                new AuthRestController.RegistrationRequest(email,"BadPass",username);
        assertNotNull(registrationRequest);
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(registrationRequest))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registrationConflictEmail() throws Exception {
        assertNotNull(getUser());

        AuthRestController.RegistrationRequest registrationRequest =
                new AuthRestController.RegistrationRequest(email, password, username);
        assertNotNull(registrationRequest);
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(registrationRequest))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void registrationConflictUserName() throws Exception {
        assertNotNull(getUser());
        userService.delete("any" + email);

        AuthRestController.RegistrationRequest registrationRequest =
                new AuthRestController.RegistrationRequest("any" + email, password ,username);
        assertNotNull(registrationRequest);
        mockMvc.perform(post("/auth/registration")
                .content(gson.toJson(registrationRequest))
                .contentType(contentType))
                .andExpect(status().isConflict());

        userService.delete(email);
        userService.delete("any" + email);
    }

    private User getUser() {
        userService.delete(email);
        return userService.createUserAccount(email, password, username);
    }
}