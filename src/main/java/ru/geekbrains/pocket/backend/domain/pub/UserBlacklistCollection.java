package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBlacklistCollection {
    private String user;
    private Integer offset;
    private List<UserBlacklistPub> data;

    public UserBlacklistCollection(User user, Integer offset, List<UserBlacklist> userBlacklists){
        this.user = user.getId().toString();
        this.offset = offset;
        this.data = userBlacklists.stream().map(UserBlacklistPub::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserBlacklistCollection{" +
                "'user':'" + user + "'" +
                ", 'offset':'" + offset + "'" +
                ", " + data +
                '}';
    }

}