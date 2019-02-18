package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserToken;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public interface UserTokenRepository extends MongoRepository<UserToken, ObjectId> {

    UserToken findByToken(String token);

    UserToken findFirstByUserAndUserip(User user, String userIp);

    List<UserToken> findByUser(User user);

    Stream<UserToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    void deleteByUser(User user);

//    @Modifying
//    @Query("delete from UserToken t where t.expiryDate <= ?1")
//    void deleteAllExpiredSince(Date now);

}
