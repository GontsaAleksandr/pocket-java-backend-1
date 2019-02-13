package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;
import ru.geekbrains.pocket.backend.domain.db.UserContact;

import java.util.List;

@Repository
public interface UserBlacklistRepository extends MongoRepository<UserBlacklist, ObjectId> {

    List<UserBlacklist> findByUser(User user);

    UserBlacklist findFirstByUserAndBanned(User user, User banned);

    UserBlacklist findFirstByUserAndBannedId(User user, ObjectId id);

}
