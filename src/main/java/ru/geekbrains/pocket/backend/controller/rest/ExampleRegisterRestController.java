package ru.geekbrains.pocket.backend.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserProfile;
import ru.geekbrains.pocket.backend.resource.UserResource;
import ru.geekbrains.pocket.backend.service.RoleService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.net.URI;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api")
public class ExampleRegisterRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @CrossOrigin(origins = "http://localhost:9000")
    @GetMapping("/register/{name}")
    public UserResource getUserByName(@PathVariable String name) {
        return new UserResource(userService.getUserByEmail(name));
    }

    @PostMapping("/register/")
    public ResponseEntity<?> processRegistrationForm(@RequestBody String email, String password, String name) {
        log.debug("Processing registration form for: " + email);
        User existing = userService.getUserByEmail(email);
        if (existing != null) {
            log.debug("Email already exists.");
        }

        Role roleUser = roleService.getRole("ROLE_USER");
        if (roleUser == null)
            roleUser = roleService.insert(roleUser);

        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(name);

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("123")); //TODO
        user.setProfile(userProfile);
        user.setRoles(Arrays.asList(roleUser));

        user = userService.insert(user);

        log.debug("Successfully created user: " + user.getEmail());

        HttpHeaders httpHeaders = new HttpHeaders();

        Link forOneBookmark = new UserResource(user).getLink("self");
        httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        log.info("register: " + user);

        return new ResponseEntity<User>(null, httpHeaders, HttpStatus.CREATED);
    }
}
