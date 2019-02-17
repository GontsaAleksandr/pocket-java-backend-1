package ru.geekbrains.pocket.backend.controller.rest;

import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.pub.MessageCollection;
import ru.geekbrains.pocket.backend.domain.pub.MessagePub;
import ru.geekbrains.pocket.backend.service.GroupMessageService;
import ru.geekbrains.pocket.backend.service.UserService;

import java.util.List;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/groups")
public class GroupMessageRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupMessageService groupMessageService;

    @GetMapping("/{idGroup}/messages") //Получить историю переписки
    public ResponseEntity<?> getMessages(@PathVariable String idGroup, @RequestParam("offset") Integer offset) {
        List<GroupMessage> messages = groupMessageService.getMessages(new ObjectId(idGroup));
        if (messages != null) {
            MessageCollection messageCollection = new MessageCollection();
            messageCollection.setGroupMessages(offset, messages);
            return new ResponseEntity<>(messageCollection, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

    @GetMapping("/{idGroup}/messages/{id}") //Получить конкретное сообщение
    public ResponseEntity<?> findMessage(@PathVariable String idGroup, @PathVariable String idMessage) {
        GroupMessage message = groupMessageService.getMessage(new ObjectId(idMessage));
        if (message != null) {
            if (idGroup.equals(message.getGroup().getId().toString()))
                return new ResponseEntity<>(new MessagePub(message), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
