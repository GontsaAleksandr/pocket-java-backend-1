package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.UserBlacklist;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBlacklistPub {

    private UserProfilePub user;

    public UserBlacklistPub(UserBlacklist userBlacklist){
        this.user = new UserProfilePub(userBlacklist.getBanned());
    }

    @Override
    public String toString() {
        return "UserBlacklist{" +
                "" + user +
                '}';
    }

}