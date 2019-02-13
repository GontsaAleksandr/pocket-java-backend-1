package ru.geekbrains.pocket.backend.domain.pub;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.enumeration.RoleGroupMember;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberPub {

    private RoleGroupMember role;

    private UserProfilePub user;

    public GroupMemberPub(GroupMember groupMember){
        this.role = groupMember.getRole();
        this.user = new UserProfilePub(groupMember.getMember());
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "role='" + role + '\'' +
                ", user=" + user +
                '}';
    }
}
