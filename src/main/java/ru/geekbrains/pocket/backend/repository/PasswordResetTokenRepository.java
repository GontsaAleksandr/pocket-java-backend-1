//package ru.geekbrains.pocket.backend.repository;
//
//import org.bson.types.ObjectId;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import ru.geekbrains.pocket.backend.domain.db.PasswordResetToken;
//import ru.geekbrains.pocket.backend.domain.db.User;
//
//import java.util.Date;
//import java.util.stream.Stream;
//
//public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, ObjectId> {
//
//    PasswordResetToken findByToken(String token);
//
//    PasswordResetToken findByUser(User user);
//
//    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);
//
//    void deleteByExpiryDateLessThan(Date now);
//
////    @Modifying
////    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
////    void deleteAllExpiredSince(Date now);
//}
