package ru.geekbrains.pocket.backend.domain.pub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserChatCollection {

    private Integer offset;

    private List<UserChatPub> data;

    public UserChatCollection(Integer offset, List<UserChat> userChats){
        this.offset = offset;
        this.data = userChats.stream().map(UserChatPub::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserChatCollection{" +
                "'offset':'" + offset + "'" +
                ", " + data +
                '}';
    }

}