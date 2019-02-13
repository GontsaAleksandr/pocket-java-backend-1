package ru.geekbrains.pocket.backend.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.UserProfilePub;
import ru.geekbrains.pocket.backend.service.UserService;

import javax.validation.constraints.Size;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@RequestMapping("/v1") config in application.properties
public class UserRestController {
    @Autowired
    private UserService userService;

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}") //Получить информацию о пользователе
    public ResponseEntity<?> getUserProfileById(@PathVariable String id) {
        //TODO validate
        User user = userService.getUserById(new ObjectId(id));
        if (user != null)
            return new ResponseEntity<>(new UserProfilePub(user), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/users") //Поиск пользователя
    public ResponseEntity<?> findUser(@RequestParam("email") @Size(min = 6, max = 32) String email) {
        //TODO validate
        User user = userService.getUserByEmail(email);
        if (user != null)
            return new ResponseEntity<>(new UserProfilePub(user), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
