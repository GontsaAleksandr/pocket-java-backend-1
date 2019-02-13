package ru.geekbrains.pocket.backend.domain.pub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.domain.db.UserContact;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserContactCollection {
    private String user;
    private Integer offset;
    private List<UserContactPub> data;

    public UserContactCollection(User user, Integer offset, List<UserContact> userContacts){
        this.user = user.getId().toString();
        this.offset = offset;
        this.data = userContacts.stream().map(UserContactPub::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UserContactCollection{" +
                "'user':'" + user + "'" +
                ", 'offset':'" + offset + "'" +
                ", " + data +
                '}';
    }

}