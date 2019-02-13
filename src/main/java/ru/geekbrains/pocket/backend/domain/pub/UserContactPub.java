package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.UserContact;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserContactPub {

    private UserProfilePub contact;
    private String byname;
    private Date added_at;

    public UserContactPub(UserContact userContact){
        this.contact = new UserProfilePub(userContact.getContact());
        this.byname = userContact.getByName();
        this.added_at = userContact.getAddedAt();
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "'contact':'" + contact + "'" +
                ", 'byname':'" + byname + "'" +
                ", 'added_at':'" + added_at + "'" +
                '}';
    }

}