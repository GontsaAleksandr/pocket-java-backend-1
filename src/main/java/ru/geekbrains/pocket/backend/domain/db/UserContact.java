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
@Document(collection = "users.contacts")
public class UserContact {

    @Id
    private ObjectId id;

    @DBRef
    @Indexed
    @Valid
    @NotNull
    private User user;

    @DBRef
    @NotNull
    private User contact;

    //@Size(min = 1, max = 50)
    @Field(value = "byname")
    private String byName;

    @Field(value = "added_at")
    private Date addedAt = new Date();

    public UserContact(@Valid @NotNull User user, @NotNull User contact) {
        this.user = user;
        this.contact = contact;
        this.byName = user.getProfile().getUsername();
    }

    public UserContact(@Valid @NotNull User user, @NotNull User contact, String byName) {
        this.user = user;
        this.contact = contact;
        this.byName = byName;
    }

    public UserContact(User user, User contact, String byName, Date addedAt) {
        this.user = user;
        this.contact = contact;
        this.byName = byName;
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "UserContact{" +
                "id=" + id +
                ", user=" + user +
                ", contact=" + contact +
                ", byname=" + byName +
                ", added_at=" + addedAt +
                '}';
    }
}
