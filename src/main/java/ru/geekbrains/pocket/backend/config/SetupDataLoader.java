package ru.geekbrains.pocket.backend.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.geekbrains.pocket.backend.domain.db.*;
import ru.geekbrains.pocket.backend.repository.PrivilegeRepository;
import ru.geekbrains.pocket.backend.repository.RoleRepository;
import ru.geekbrains.pocket.backend.service.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Log4j2
@Configuration
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private UserChatService userChatService;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupMessageService groupMessageService;
    @Autowired
    private UserContactService userContactService;


    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
//        alreadySetup = true;
        if (alreadySetup) {
            return;
        }

//        userService.deleteAll();
//        userTokenService.deleteAllUserToken();
//        userChatService.deleteAllUserChats();
//        userMessageService.deleteAllMessages();
//        groupService.deleteAllGroups();
//        groupMessageService.deleteAllMessages();

        // == createRoleIfNotFound initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == createRoleIfNotFound initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        final Role userRole = createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == createRoleIfNotFound initial user
        User user1 = createUserIfNotFound("test@test.com", "Test", "Test1234", Arrays.asList(adminRole, userRole));
        User user2 = createUserIfNotFound("a@mail.ru", "Alex", "Abc12345", Arrays.asList(adminRole, userRole));
        User user3 = createUserIfNotFound("b@mail.ru", "Bob", "Abc12345", Arrays.asList(userRole));
        User user4 = createUserIfNotFound("i@mail.ru", "ivan", "Qwe12345", Arrays.asList(userRole));

        createTokenForUserIfNotFound(user1);
        createTokenForUserIfNotFound(user2);
        createTokenForUserIfNotFound(user3);
        //createTokenForUserIfNotFound(user4); не создаём токен специально для тестирования

        createUserChat(user1, user2, user3);
        createUserChat(user1, user3, user4);
        createUserChat(user1, user4, user2);
        createUserChat(user4, user1, user3);

        Group group1 = createGroupIfNotFound(user2, "group1");
        Group group2 = createGroupIfNotFound(user2, "group2");
        Group group3 = createGroupIfNotFound(user3, "group3");

        createUserMessage(user2, user3, "Сообщение №1");
        createUserMessage(user3, user2, "Сообщение №2");
        createUserMessage(user2, user3, "Сообщение №3");
        createUserMessage(user2, user4, "Сообщение №4");
        createUserMessage(user2, user1, "Сообщение №5");
        createUserMessage(user1, user2, "Сообщение №6");
        createUserMessage(user1, user3, "Сообщение №7");

        createGroupMessage(user2, group1, "Сообщение №1 для группы №1");
        createGroupMessage(user3, group1, "Сообщение №2 для группы №1");
        createGroupMessage(user2, group2, "Сообщение №1 для группы №2");
        createGroupMessage(user1, group3, "Сообщение №1 для группы №3");
        createGroupMessage(user2, group3, "Сообщение №2 для группы №3");
        createGroupMessage(user3, group3, "Сообщение №3 для группы №3");

        createUserContact(user2, user1);
        createUserContact(user2, user3);
        createUserContact(user2, user4);
        createUserContact(user3, user1);
        createUserContact(user3, user2);


        alreadySetup = true;
    }

    @Transactional
    private Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
            log.info("Preloading " + privilege);
        }
        return privilege;
    }

    @Transactional
    private Role createRoleIfNotFound(final String name, final List<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            role = roleRepository.save(role);
            log.info("Preloading " + role);
        }
        return role;
    }

    @Transactional
    private User createUserIfNotFound(final String email, final String userName, final String password, final Collection<Role> roles) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setProfile(new UserProfile(userName));
            user.setEnabled(true);
            user.setRoles(roles);
            user = userService.update(user);
            log.info("Preloading " + user);
        }
        return user;
    }

    @Transactional
    private UserToken createTokenForUserIfNotFound(User user) {
        UserToken userToken = userTokenService.getUserToken(user, "0.0.0.0");
        if (userToken == null) {
            userToken = userTokenService.createOrUpdateToken(user, "0.0.0.0");
            log.info("Preloading for user '" + user.getEmail() + "' " + userToken);
        }
        return userToken;
    }

    @Transactional
    private UserChat createUserChat(User user, User direct, User sender) {
        UserChat userChat = userChatService.getUserChat(user, direct);
        if (userChat == null) {
            userChat = userChatService.createUserChat(user, direct, sender);
            log.info("Preloading " + userChat);
        }
        return userChat;
    }

    @Transactional
    private UserMessage createUserMessage(User sender, User recipient, String text) {
        UserMessage message = userMessageService.getMessage(sender, recipient, text);
        if (message == null) {
            message = userMessageService.createMessage(sender, recipient, text);
            log.info("Preloading " + message);
        }
        return message;
    }

    @Transactional
    private Group createGroupIfNotFound(final User creator, final String name) {
        List<Group> groups = groupService.getGroups(name);
        Group group;
        if (groups.size() == 0) {
            group = new Group();
            group.setCreator(creator);
            //group.setProject();
            group.setName(name);
            group.setDescription("");
            group.setInvitation_code("Invitation to " + group.getName());
            group.setPublic(false);
            group = groupService.createGroup(group);
            log.info("Preloading " + group);
        } else
            group = groups.get(0);

        return group;
    }

    @Transactional
    private GroupMessage createGroupMessage(User sender, Group group, String text) {
        GroupMessage message = groupMessageService.getMessage(sender, group, text);
        if (message == null) {
            message = groupMessageService.createMessage(sender, group, text);
            log.info("Preloading " + message);
        }
        return message;
    }

    @Transactional
    private UserContact createUserContact(User user, User contact){
        UserContact userContact = userContactService.getUserContact(user, contact);
        if (userContact == null) {
            userContact = userContactService.createUserContact(user, contact);
            log.info("Preloading " + userContact);
        }
        return userContact;
    }

//    @Bean
//    CommandLineRunner initDatabase() {
//        return args -> {
//            log.info("initDatabase");
//        };
//    }

}
