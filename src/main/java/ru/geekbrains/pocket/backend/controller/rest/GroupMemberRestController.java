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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberCollection;
import ru.geekbrains.pocket.backend.domain.pub.GroupMemberPub;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/groups")
public class GroupMemberRestController {
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/{id}/members") //Получить список участников группы
    public ResponseEntity<?> getGroupMembers(@PathVariable String id,
                                             @RequestParam("offset") Integer offset) {
        List<GroupMember> groupMembers = groupMemberService.getGroupMembers(new ObjectId(id));
        if (groupMembers != null) {
            return new ResponseEntity<>(new GroupMemberCollection(offset, groupMembers), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}/members") //Добавить участника в группу
    //Особенность: не возвращает ошибки, если такой участник в группе уже есть, но и не добавляет его еще раз
    public ResponseEntity<?> addGroupMember(@PathVariable String id,
                                            @Valid @RequestBody AddGroupMemberRequest addGroupMemberRequest,
                                            final BindingResult result,
                                            HttpServletRequest request) {
        if(result.hasErrors()) {
            return getResponseEntity(result);
        }

        Group group = groupService.getGroup(new ObjectId(id));
        User user = httpRequestComponent.getUserFromToken(request);
        //определяем группу и текущего пользователя
        if (group != null && user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
            //определяем является пользователь членом и администратором группы
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                User member = userService.getUserById(new ObjectId(addGroupMemberRequest.getUser()));
                //ищем есть ли позользователь в бд по предъявленному id
                if (member != null) {
                    groupMember = groupMemberService.createGroupMember(group, member, RoleGroupMember.speacker);
                    return new ResponseEntity<>(new GroupMemberPub(groupMember), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{idGroup}/members/{idMember}") //Изменить права пользователя группы
    public ResponseEntity<?> editGroupMember(@PathVariable String idGroup, @PathVariable String idMember,
                                         @RequestParam("role") RoleGroupMember role,
                                         HttpServletRequest request) {
        User user = httpRequestComponent.getUserFromToken(request);
        //определяем группу и текущего пользователя
        if (user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(new ObjectId(idGroup), user);
            //определяем является пользователь членом и администратором группы
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                groupMember = groupMemberService.getGroupMember(new ObjectId(idGroup), new ObjectId(idMember));
                //ищем есть ли пользователь в группе по предъявленному id
                if (groupMember != null) {
                    groupMember.setRole(role);
                    groupMember = groupMemberService.updateGroupMember(groupMember);
                    return new ResponseEntity<>(new GroupMemberPub(groupMember), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{idGroup}/members/{idMember}") //Удалить участника из группы
    //Особенность: не возвращает ошибки, даже если такого участника в группе нет
    public ResponseEntity<?> deleteGroupMember(@PathVariable String idGroup, @PathVariable String idMember,
                                           HttpServletRequest request){
        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(new ObjectId(idGroup), user);
            //ищем есть ли текущий пользователь в группе
            if (groupMember != null) {
                //определяем является пользователь администратором группы
                if (groupMember.getRole().equals(RoleGroupMember.administrator)) {
                    groupMember = groupMemberService.getGroupMember(new ObjectId(idGroup), new ObjectId(idMember));
                    //ищем есть ли пользователь в группе по предъявленному id
                    if (groupMember == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                }
                //не является администратором группы, то может удалить только себя
                groupMemberService.deleteGroupMember(groupMember);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<?> getResponseEntity(BindingResult result) {
        final Map<String, Object> response = new HashMap<>();
        response.put("message", "Your request contains errors");
        response.put("errors", result.getAllErrors()
                .stream()
                .map(x -> String.format("%s : %s", x.getCode(), x.getDefaultMessage()))
                .collect(Collectors.toList()));
        log.debug(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //===== Request & Response =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddGroupMemberRequest {

        @NotNull
        @NotEmpty
        private String user;

    }

}
