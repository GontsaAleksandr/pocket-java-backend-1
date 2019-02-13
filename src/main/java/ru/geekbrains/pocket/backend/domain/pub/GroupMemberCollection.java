package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.GroupMember;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberCollection {

    private String group;

    private int offset;

    private List<GroupMemberPub> data;

    public GroupMemberCollection(Integer offset, List<GroupMember> groupMembers){
        this.offset = offset;
        this.data = groupMembers.stream().map(GroupMemberPub::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "GroupMemberCollection{" +
                "'group':'" + group + "'" +
                ", 'offset':'" + offset + "'" +
                ", 'data':'" + data + "'" +
                '}';
    }

}
