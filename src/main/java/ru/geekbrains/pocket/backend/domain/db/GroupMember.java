package ru.geekbrains.pocket.backend.domain.db;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Document(collection = "groups.members")
public class GroupMember {

    @Id
    private ObjectId id;

    @DBRef
    @NotNull
    private Group group;

    @DBRef
    @NotNull
    private User member;

    private RoleGroupMember role = RoleGroupMember.speacker;

    public GroupMember(@NotNull Group group, @NotNull User member) {
        this.group = group;
        this.member = member;
    }

    public GroupMember(@NotNull Group group, @NotNull User member, RoleGroupMember role) {
        this.group = group;
        this.member = member;
        this.role = role;
    }
}
