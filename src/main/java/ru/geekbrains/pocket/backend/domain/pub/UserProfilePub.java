package ru.geekbrains.pocket.backend.domain.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.User;

import javax.validation.constraints.NotNull;
import java.util.Date;

//this class for class User (collection = "users")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePub {
    private String id;

    private String username;

    private String fullname;

    @JsonProperty("last_seen")
    private Date lastSeen;

    public UserProfilePub(@NotNull User user) {
        this.id = user.getId().toString();
        this.username = user.getProfile().getUsername();
        this.fullname = user.getProfile().getFullName();
        //this.lastSeen = null;
    }

    @Override
    public String toString() {
        return "profile{" +
                "'id':" + id +
                "'username':" + username +
                ", 'fullname':" + fullname +
                '}';
    }
}
