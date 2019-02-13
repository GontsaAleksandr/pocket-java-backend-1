package ru.geekbrains.pocket.backend.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.exception.UserNotFoundException;
import ru.geekbrains.pocket.backend.resource.UserResource;
import ru.geekbrains.pocket.backend.resource.UserResources;
import ru.geekbrains.pocket.backend.response.UsersErrorResponse;
import ru.geekbrains.pocket.backend.service.UserService;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:9000")
public class ExampleUserRestController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/id/{id}")
    public UserResource getUserById(@PathVariable ObjectId id) {
        User user = userService.getUserById(id);
        return new UserResource(user);
    }

    @GetMapping("/userst/{name}")
    public UserResource getUserByName(@PathVariable String name) {
        return new UserResource(userService.getUserByUsername(name));
    }

    @GetMapping("/users/name/{name}")
    public Resource<User> getUserByName2(@PathVariable String name) {
        User user = userService.getUserByUsername(name);
        return new Resource<>(user,
                linkTo(methodOn(ExampleUserRestController.class).getUserByName(name)).withSelfRel(),
                linkTo(methodOn(ExampleUserRestController.class).getAllUsers()).withRel("users"));
    }

    @GetMapping("/usersall")
    public UserResources getAllUsers() {
        return new UserResources(userService.getAllUsers());
    }

    @GetMapping("/usersall1")
    public List<User> getAllUsers1() {
        return userService.getAllUsers();
    }

    @GetMapping("/usersall2")
    public Resources<UserResource> getAllUsers2() {
        return new Resources<>(userService.getAllUserResources());
    }

    @GetMapping("/usersall3")
    public Resources<Resource<User>> getAllUsers3() {
        List<Resource<User>> users = userService.getAllUsers().stream()
                .map(user -> new Resource<>(user,
                        linkTo(methodOn(ExampleUserRestController.class).getUserByName2(user.getEmail())).withSelfRel(),
                        linkTo(methodOn(ExampleUserRestController.class).getAllUsers()).withRel("users")))
                .collect(Collectors.toList());

        return new Resources<>(users,
                linkTo(methodOn(ExampleUserRestController.class).getAllUsers()).withSelfRel());
    }

    @PostMapping("/userst")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        //TODO вывод ошибки если юзер уже есть или не указан
        if (user == null) return null;
        user.setId(null);
        user = userService.insert(user);
        HttpHeaders httpHeaders = new HttpHeaders();

        Link forOneBookmark = new UserResource(user).getLink("self");
        httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        log.info("addUser: " + user);

        return new ResponseEntity<User>(null, httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping("/userst")
    public User updateUser(Principal principal, @RequestBody User user) {
        //обновление доступно только текущего авторизованного юзера
        String username = principal.getName();
        user.setId(userService.validateUser(username).getId());
        log.info("update: " + user);
        return userService.update(user);
    }

    @DeleteMapping("/userst/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable ObjectId id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<UsersErrorResponse> handleException(UserNotFoundException exc) {
        UsersErrorResponse usersErrorResponse = new UsersErrorResponse();
        usersErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        usersErrorResponse.setMessage(exc.getMessage());
        usersErrorResponse.setTimestamp(System.currentTimeMillis());
        return new ResponseEntity<>(usersErrorResponse, HttpStatus.NOT_FOUND);
    }

}
