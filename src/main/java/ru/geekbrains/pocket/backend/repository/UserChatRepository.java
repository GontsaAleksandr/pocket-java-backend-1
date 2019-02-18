package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.geekbrains.pocket.backend.domain.db.Group;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;

public interface UserChatRepository extends MongoRepository<UserChat, ObjectId> {

    UserChat findFirstByUserAndGroup(User user, Group group);

    UserChat findFirstByUserAndDirect(User user, User direct);

    List<UserChat> findByGroup(Group group);

    List<UserChat> findByUser(User user);

    List<UserChat> findByUserId(ObjectId id);

    @Query("{'user':?0}")
    List<UserChat> findCustomByUser(User user);
}
