package ru.geekbrains.pocket.backend.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "projects")
@TypeAlias("projects")
public class Project {

    @Id
    ObjectId id;

    @DBRef
    User creator;

    String name;

    String description;
}
