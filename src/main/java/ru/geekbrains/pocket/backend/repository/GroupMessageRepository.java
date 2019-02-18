package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.List;

@Repository
public interface GroupMessageRepository extends MongoRepository<GroupMessage, ObjectId> {

    GroupMessage findFirstBySenderAndGroupAndText(User sender, Group group, String text);

    List<GroupMessage> findBySender(User sender);

    List<GroupMessage> findByGroup(Group group);

    List<GroupMessage> findByGroupId(ObjectId id);

}
