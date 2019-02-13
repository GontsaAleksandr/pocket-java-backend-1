package ru.geekbrains.pocket.backend.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    void deleteByEmail(String email);

    User findByEmail(String email);

    //@Cacheable()
    User findByProfileUsername(String username);

    // Supports native JSON query string
    @Query("{firstname:'?0'}")
    List<User> findCustomByFirstname(String firstname);

    @Query(value = "{ 'firstname' : ?0 }", fields = "{ 'firstname' : 1, 'lastname' : 1}")
    List<User> findByTheUsersFirstname(String firstname);

    User findByEmailMatches(String email);

}

//Keyword 	    Sample 	                            Logical result
//GreaterThan 	findByAgeGreaterThan(int age) 	    {"age" : {"$gt" : age}}
//LessThan 	    findByAgeLessThan(int age) 	        {"age" : {"$lt" : age}}
//Between 	    findByAgeBetween(int from, int to) 	{"age" : {"$gt" : from, "$lt" : to}}
//IsNotNull, NotNull 	findByFirstnameNotNull() 	{"age" : {"$ne" : null}}
//IsNull, Null 	findByFirstnameNull() 	            {"age" : null}
//Like 	        findByFirstnameLike(String name) 	{"age" : age} ( age as regex)
//(No keyword) 	findByFirstname(String name) 	    {"age" : name}
//Not 	        findByFirstnameNot(String name) 	{"age" : {"$ne" : name}}
//Near 	        findByLocationNear(Point point) 	{"location" : {"$near" : [x,y]}}
//Within 	    findByLocationWithin(Circle circle) 	{"location" : {"$within" : {"$center" : [ [x, y], distance]}}}
//Within 	    findByLocationWithin(Box box) 	    {"location" : {"$within" : {"$box" : [ [x1, y1], x2, y2]}}}