package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;

import java.util.List;

@Repository
public interface UserMessageRepository extends MongoRepository<UserMessage, ObjectId> {

    List<UserMessage> findBySender(User sender);

    List<UserMessage> findByRecipient(User recipient);

    List<UserMessage> findBySenderOrRecipient(User sender, User recipient);

    List<UserMessage> findBySenderAndReadFalse(User recipient);

    List<UserMessage> findByRecipientAndReadFalse(User recipient);

}
