package ru.geekbrains.pocket.backend.controller.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;
import ru.geekbrains.pocket.backend.util.RandomString;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/groups")
public class GroupInviteRestController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/{id}/invites") //Получить статус инвайта (активен или отсутсвует) и код, если есть
    public ResponseEntity<?> getGroupInvite(@PathVariable String id,
                                            HttpServletRequest request) {
        Group group = groupService.getGroup(new ObjectId(id));
        User user = httpRequestComponent.getUserFromToken(request);
        //определяем группу и текущего пользователя
        if (group != null && user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
            //определяем является пользователь членом и администратором группы
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                if (group.getInvitation_code() != null) {
                    return new ResponseEntity<>(new InviteResponseTrue(group.getInvitation_code()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new InviteResponseFalse(), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{id}/invites") //Создать код-инвайт либо пересоздать его
    public ResponseEntity<?> addGroupInvite(@PathVariable String id,
                                            HttpServletRequest request) {
        Group group = groupService.getGroup(new ObjectId(id));
        User user = httpRequestComponent.getUserFromToken(request);
        //определяем группу и текущего пользователя
        if (group != null && user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
            //определяем является пользователь членом и администратором группы
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                //формируем новый инвайт
                group.setInvitation_code(new RandomString(32).nextString());
                //group.setInvitation_code(RandomStringUtil.randomString(32));
                return new ResponseEntity<>(new InviteResponseTrue(group.getInvitation_code()), HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}/invites") //Удалить код-инвайт
    public ResponseEntity<?> deleteGroupInvite(@PathVariable String id,
                                           HttpServletRequest request){
        Group group = groupService.getGroup(new ObjectId(id));
        User user = httpRequestComponent.getUserFromToken(request);
        //определяем группу и текущего пользователя
        if (group != null && user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
            //определяем является пользователь членом и администратором группы
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                if (group.getInvitation_code() != null) {
                    group.setInvitation_code(null);
                    group = groupService.updateGroup(group);
                    if (group == null)
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //===== Request & Response =====

    @Getter
    @Setter
    @NoArgsConstructor
    private static class InviteResponseTrue {

        private boolean active = true;

        private String invitation_code;

        public InviteResponseTrue(String invitation_code) {
            this.invitation_code = invitation_code;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class InviteResponseFalse {

        private boolean active = false;

    }

}
