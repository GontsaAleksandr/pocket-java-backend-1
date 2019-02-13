package ru.geekbrains.pocket.backend.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;
import ru.geekbrains.pocket.backend.exception.GroupNotFoundException;
import ru.geekbrains.pocket.backend.repository.GroupRepository;
import ru.geekbrains.pocket.backend.service.GroupMemberService;
import ru.geekbrains.pocket.backend.service.GroupService;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberService groupMemberService;

    @Override
    public Group createGroup(Group group) {
        return groupRepository.insert(group);
    }

    @Override
    public Group createGroupAndMember(Group group, User member) {
        group = groupRepository.insert(group);
        if (group != null) {
            groupMemberService.createGroupMember(group, member, RoleGroupMember.administrator);
            return group;
        }
        return null;
    }

    @Override
    public void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public void deleteAllGroups() {
        groupRepository.deleteAll();
    }

    @Override
    public Group getGroup(ObjectId id) {
        Optional<Group> group = Optional.of(groupRepository.findById(id).orElseThrow(
                () -> new GroupNotFoundException("Group with id = " + id + " not found")));
        return group.get();
    }

    @Override
    public List<Group> getGroups(User creator) {
        return groupRepository.findByCreator(creator);
    }

    @Override
    public List<Group> getGroups(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public List<Group> getOpenedGroups() {
        return null;
    }

    @Override
    public List<Group> getClosedGroups() {
        return null;
    }

    @Override
    public String setGroupToClose() {
        return null;
    }

    @Override
    public String setGroupToOpen() {
        return null;
    }

    @Override
    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }

}
