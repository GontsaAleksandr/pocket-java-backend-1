package ru.geekbrains.pocket.backend.controller.rest;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;
import ru.geekbrains.pocket.backend.domain.pub.UserContactCollection;
import ru.geekbrains.pocket.backend.domain.pub.UserContactPub;
import ru.geekbrains.pocket.backend.service.UserContactService;
import ru.geekbrains.pocket.backend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
public class ContactRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserContactService userContactService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/contacts") //Получить список контактов
    public ResponseEntity<?> getContacts(@RequestParam("offset") Integer offset,
                                   HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            List<UserContact> userContacts = userContactService.getUserContacts(user);
            return new ResponseEntity<>(
                    new UserContactCollection(user, offset, userContacts), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping ("/contacts/{id}") //Получить контакт по id пользователя (если такой пользователь есть в списке контактов)
    public ResponseEntity<?> getContact(@PathVariable String id,
                                        HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            UserContact userContact = userContactService.getUserContact(user, new ObjectId(id));
            return new ResponseEntity<>(new UserContactPub(userContact), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/contacts") //Добавить новый контакт
    //Особенность: не возвращает ошибки, если такой пользователь в списке контактов уже есть, но и не добавляет его еще раз
    public ResponseEntity<?> addNewContact(@Valid @RequestBody AddContactRequest addContactRequest,
                                           HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        ObjectId id = new ObjectId(addContactRequest.getUser());
        if (user != null) {
            UserContact userContact = userContactService.getUserContact(user, id);
            if (userContact == null) {
                User contact = userService.getUserById(id);
                if (contact != null) {
                    if (addContactRequest.getByname() == null)
                        userContact = userContactService.createUserContact(user, contact);
                    else
                        userContact = userContactService.createUserContact(user, contact, addContactRequest.getByname());
                } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(new UserContactPub(userContact), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PutMapping("/contacts/{id}") //Редактировать контакт
    public ResponseEntity<?> editContact(@PathVariable String id,
                                         @Valid @RequestBody EditContactRequest editContactRequest,
                                         HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        String byname = editContactRequest.getByname();
        if (user != null && byname != null) {
            UserContact userContact = userContactService.getUserContact(user, new ObjectId(id));
            if (userContact != null) {
                userContact.setByName(byname);
                userContact = userContactService.updateUserContact(userContact);
                return new ResponseEntity<>(new UserContactPub(userContact), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/contacts/{id}") //Удалить контакт
    //Особенность: не возвращает ошибки, даже если такого пользователя в контактах нет
    public ResponseEntity<?> deleteContact(@PathVariable String id,
                                           HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            UserContact userContact = userContactService.getUserContact(user, new ObjectId(id));
            if (userContact != null) {
                userContactService.deleteUsersContact(userContact);
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
    private static class AddContactRequest {

        @NotNull
        @NotEmpty
        private String user;

        @Nullable
        private String byname;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EditContactRequest {

        @NotNull
        private String byname;

    }
}
