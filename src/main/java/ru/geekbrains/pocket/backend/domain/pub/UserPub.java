package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonRootName;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.util.validation.ValidEmail;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeName("User")
//@JsonRootName(value = "User")
//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
public class UserPub {
    private String id;

    @NotEmpty
    @ValidEmail
    private String email;

    private UserProfilePub profile;

    public UserPub(@NotNull User user) {
        this.id = user.getId().toString();
        this.email = user.getEmail();
        this.profile = new UserProfilePub(user);
    }

    @Override
    public String toString() {
        return "User{" +
                "'id':'" + id + "'" +
                ", 'email':'" + email + "'" +
                ", " + profile +
                '}';
    }

}