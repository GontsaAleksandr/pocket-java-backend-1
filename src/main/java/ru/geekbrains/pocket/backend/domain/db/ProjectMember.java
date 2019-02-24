package ru.geekbrains.pocket.backend.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;

@Data
@NoArgsConstructor
@Document(collection = "projects.members")
//@TypeAlias("projects.members")
public class ProjectMember {

    @Id
    private ObjectId id;

    @DBRef
    private Project project;

    @DBRef
    private User member;

    private RoleGroupMember role = RoleGroupMember.speacker;
}
