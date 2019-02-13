package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.List;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, ObjectId> {

    GroupMember findFirstByGroupAndMember(Group group, User member);

    GroupMember findFirstByGroupIdAndMember(ObjectId idGroup, User member);

    GroupMember findFirstByGroupAndMemberId(Group group, ObjectId idMember);

    GroupMember findFirstByGroupIdAndMemberId(ObjectId idGroup, ObjectId idMember);

    List<GroupMember> findByGroup(Group group);

    List<GroupMember> findByGroupId(ObjectId id);

    List<GroupMember> findByMember(User member);
}
