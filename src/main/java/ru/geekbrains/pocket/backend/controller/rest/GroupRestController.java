package ru.geekbrains.pocket.backend.controller.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.pub.GroupPub;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class GroupRestController {
    //private static final Logger logger = LoggerFactory.getLogger(GroupRestController.class.getName());

    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private HttpRequestComponent httpRequestComponent;

    @GetMapping("/groups/{id}") //Получить информацию о группе
    public ResponseEntity<?> getGroup(@PathVariable String id,
                                      @Valid @RequestBody InvitationCodeRequest invitationCodeRequest,
                                      final BindingResult result) {
        if(result.hasErrors()) {
            return getResponseEntity(result);
        }

        Group group = groupService.getGroup(new ObjectId(id));
        if (group != null) {
            if (group.getInvitation_code() == null ||
                    (group.getInvitation_code() != null
                    && group.getInvitation_code().equals(invitationCodeRequest.getInvitation_code())))
                return new ResponseEntity<>(new GroupPub(group), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/groups") //Создать группу
    public ResponseEntity<?> createGroup(@Valid @RequestBody NewGroupRequest newGroupRequest,
                                         final BindingResult result,
                                         HttpServletRequest request) {
        if(result.hasErrors()) {
            return getResponseEntity(result);
        }

        User user = httpRequestComponent.getUserFromToken(request);
        if (user != null) {
            Group group = new Group();
            group.setCreator(user);
            //group.setProject();
            group.setName(newGroupRequest.getName());
            if (newGroupRequest.getDescription() != null)
                group.setDescription(newGroupRequest.getDescription());
            //group.setInvitation_code("");
            group.setPublic(newGroupRequest.isPublic);
            group = groupService.createGroup(group);
            if (group != null) {
                return new ResponseEntity<>(new GroupPub(group), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/groups/{id}") //Изменить информацию о группе
    public ResponseEntity<?> editGroup(@PathVariable String id,
                                       @Valid @RequestBody EditGroupRequest groupRequest,
                                       final BindingResult result,
                                       HttpServletRequest request) {
        if(result.hasErrors()) {
            return getResponseEntity(result);
        }

        Group group = groupService.getGroup(new ObjectId(id));
        User user = httpRequestComponent.getUserFromToken(request);
        if (group != null && user != null) {
            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
            //изменять можно только администратору
            if (groupMember != null && groupMember.getRole().equals(RoleGroupMember.administrator)) {
                if (groupRequest.getName() != null)
                    group.setName(groupRequest.getName());
                if (groupRequest.getDescription() != null)
                    group.setDescription(groupRequest.getDescription());
                group.setPublic(groupRequest.isPublic);
                group = groupService.updateGroup(group);

                if (group != null) {
                    return new ResponseEntity<>(new GroupPub(group), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("You cannot do it", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/groups/{id}") //Вступить в группу //Покинуть группу
    public ResponseEntity<?> joinOrLeaveTheGroup(@PathVariable String id,
                                         HttpServletRequest request) {
        InvitationCodeRequest invitationCodeRequest = null;
        try {
            invitationCodeRequest = new ObjectMapper().readValue(request.getReader(), InvitationCodeRequest.class);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        if (request.getMethod().equals("LINK") || request.getMethod().equals("link")) {
            log.info("LINK METHOD"); //Вступить в группу
            User user = httpRequestComponent.getUserFromToken(request);
            Group group = groupService.getGroup(new ObjectId(id));
            if (invitationCodeRequest != null && user != null
                    && group != null && group.getInvitation_code() != null) {
                if (group.getInvitation_code().equals(invitationCodeRequest.getInvitation_code())) {
                    GroupMember groupMember = groupMemberService.getGroupMember(group, user);
                    if (groupMember == null) {
                        groupMemberService.createGroupMember(group, user, RoleGroupMember.speacker);
                    }
                    return new ResponseEntity<>(new GroupPub(group), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (request.getMethod().equals("UNLINK") || request.getMethod().equals("unlink")) {
            log.info("UNLINK METHOD"); //Покинуть группу
            User user = httpRequestComponent.getUserFromToken(request);
            Group group = groupService.getGroup(new ObjectId(id));
            if (user != null && group != null) {
                GroupMember groupMember = groupMemberService.getGroupMember(group, user);
                if (groupMember != null) {
                    groupMemberService.deleteGroupMember(groupMember);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

//    //TODO replace to LINK method
//    @PostMapping("/groups/{id}") //Вступить в группу
//    //@RequestMapping("/groups/{id}") //Вступить в группу
//    public ResponseEntity<?> joinToGroup(@PathVariable String id,
//                                         @Valid @RequestBody InvitationCodeRequest invitationCodeRequest,
//                                         HttpServletRequest request) {
//        User user = httpRequestComponent.getUserFromToken(request);
//        Group group = groupService.getGroup(new ObjectId(id));
//        if (invitationCodeRequest != null && user != null
//                && group != null && group.getInvitation_code() != null) {
//            if (group.getInvitation_code().equals(invitationCodeRequest.getInvitation_code())) {
//                //TODO add to GroupMember
//                List<GroupMember> groupMembers = groupMemberService.getGroupMembers(group);
//                GroupMember groupMember = groupMemberService.getGroupMember(group, user);
//                if (groupMember == null) {
//                    groupMemberService.createGroupMember(group, user, RoleGroupMember.speacker);
//                }
//                return new ResponseEntity<>(new GroupPub(group), HttpStatus.OK);
//            }
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    //TODO replace to UNLINK method
//    @DeleteMapping("/groups/{id}") //Покинуть группу
////    @RequestMapping("/groups/{id}") //Покинуть группу
//    //@RequestMapping ( path = "/groups/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<?> leaveTheGroup(@PathVariable String id,
//                                           HttpServletRequest request) {
//        User user = httpRequestComponent.getUserFromToken(request);
//        Group group = groupService.getGroup(new ObjectId(id));
//        if (user != null && group != null) {
//            GroupMember groupMember = groupMemberService.getGroupMember(group, user);
//            if (groupMember != null) {
//                groupMemberService.deleteGroupMember(groupMember);
//            }
//                return new ResponseEntity<>(HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
//    }

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
    public static class InvitationCodeRequest {

        //@Nullable
        private String invitation_code;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewGroupRequest {

        @NotNull
        @Size(min = 6, max = 32)
        private String name;

        @Nullable
        private String description;

        @JsonProperty("public")
        private boolean isPublic = false;

        public NewGroupRequest(@NotNull @Size(min = 6, max = 32) String name) {
            this.name = name;
        }

        public NewGroupRequest(@NotNull @Size(min = 6, max = 32) String name, @Nullable String description) {
            this.name = name;
            this.description = description;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditGroupRequest {

        @Nullable
        @Size(min = 6, max = 32)
        private String name;

        @Nullable
        private String description;

        @JsonProperty("public")
        private boolean isPublic = false;

    }
}
