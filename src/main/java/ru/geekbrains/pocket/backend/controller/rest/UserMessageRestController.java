package ru.geekbrains.pocket.backend.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.domain.pub.MessageCollection;
import ru.geekbrains.pocket.backend.domain.pub.MessagePub;
import ru.geekbrains.pocket.backend.service.UserMessageService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserMessageRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMessageService userMessageService;

    @GetMapping("/{idUser}/messages") //Получить историю переписки
    public ResponseEntity<?> getMessages(@PathVariable String idUser, @RequestParam("offset") Integer offset) {
        User user = userService.getUserById(new ObjectId(idUser));
        List<UserMessage> messages;
        if (user != null) {
            messages = userMessageService.getAllMessagesUser(user);

            MessageCollection messageCollection = new MessageCollection();
            messageCollection.setUserMessages(offset, messages);
            return new ResponseEntity<>(messageCollection, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

    @GetMapping("/{idUser}/messages/{idMessage}") //Получить конкретное сообщение
    public ResponseEntity<?> findMessage(@PathVariable String idUser, @PathVariable String idMessage) {
        UserMessage message = userMessageService.getMessage(new ObjectId(idMessage));
        if (message != null) {
            if (idUser.equals(message.getSender().getId().toString())
                    || idUser.equals(message.getRecipient().getId().toString()))
                return new ResponseEntity<>(new MessagePub(message), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
