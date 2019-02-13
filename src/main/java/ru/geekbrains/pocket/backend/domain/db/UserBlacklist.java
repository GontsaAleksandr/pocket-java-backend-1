package ru.geekbrains.pocket.backend.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "users.blacklists")
public class UserBlacklist {

    @Id
    private ObjectId id;

    @DBRef
    @Indexed
    @Valid
    @NotNull
    private User user;

    @DBRef
    @NotNull
    private User banned;

    @Field(value = "added_at")
    private Date addedAt = new Date();

    public UserBlacklist(@Valid @NotNull User user, @NotNull User banned) {
        this.user = user;
        this.banned = banned;
    }

    public UserBlacklist(User user, User banned, String byName, Date addedAt) {
        this.user = user;
        this.banned = banned;
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "id=" + id +
                ", user=" + user +
                ", banned=" + banned +
                ", added_at=" + addedAt +
                '}';
    }
}
