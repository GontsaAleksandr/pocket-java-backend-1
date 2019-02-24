package ru.geekbrains.pocket.backend.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;
import ru.geekbrains.pocket.backend.domain.pub.UserBlacklistCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserBlacklistPub;
import ru.geekbrains.pocket.backend.service.UserBlacklistService;
import ru.geekbrains.pocket.backend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/account")
public class BlacklistRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserBlacklistService userBlacklistService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/blacklist") //Получить список всех пользователей, добавленных в ЧС этим пользователем
    public ResponseEntity<?> getBlacklist(@RequestParam("offset") Integer offset,
                                   HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            List<UserBlacklist> userBlacklists = userBlacklistService.getUserBlacklists(user);
            return new ResponseEntity<>(
                    new UserBlacklistCollection(user, offset, userBlacklists), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/blacklist") //Добавить пользователя в ЧС
    //Особенность: не возвращает ошибки, если такой пользователь в ЧС уже есть, но и не добавляет его еще раз
    public ResponseEntity<?> addContactToBlacklist(@Valid @RequestBody BlacklistRestController.AddBannedRequest addBannedRequest,
                                           HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        ObjectId id = new ObjectId(addBannedRequest.getUser());
        if (user != null) {
            UserBlacklist userBlacklist = userBlacklistService.getUserBlacklist(user, id);
            if (userBlacklist == null) {
                User banned = userService.getUserById(id);
                if (banned != null) {
                    userBlacklist = userBlacklistService.createUserBlacklist(user, banned);
                } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new UserBlacklistPub(userBlacklist), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/blacklist/{id}") //Удалить пользоватлея из ЧС
    //Особенность: не возвращает ошибки, даже если такого пользователя в ЧС нет
    public ResponseEntity<?> deleteContact(@PathVariable String id,
                                           HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            UserBlacklist userBlacklist = userBlacklistService.getUserBlacklist(user, new ObjectId(id));
            if (userBlacklist != null) {
                userBlacklistService.deleteUserBlacklist(userBlacklist);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    //===== Request & Response =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddBannedRequest {

        @NotNull
        @NotEmpty
        private String user;

    }

}
