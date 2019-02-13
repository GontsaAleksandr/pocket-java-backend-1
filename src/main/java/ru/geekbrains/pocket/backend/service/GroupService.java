package ru.geekbrains.pocket.backend.service;

import org.bson.types.ObjectId;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.List;

public interface GroupService {

    Group createGroup(Group group);

    Group createGroupAndMember(Group group, User member);

    void deleteGroup(Group group);

    void deleteAllGroups();

    Group getGroup(ObjectId id);

    List<Group> getGroups(User creator);

    List<Group> getGroups(String name);

    List<Group> getOpenedGroups();

    List<Group> getClosedGroups();

    String setGroupToClose();

    String setGroupToOpen();

    Group updateGroup(Group group);

}
