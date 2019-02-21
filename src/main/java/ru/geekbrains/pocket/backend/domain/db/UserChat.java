package ru.geekbrains.pocket.backend.domain.db;

import com.mongodb.lang.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Document(collection = "users.chats")
public class UserChat {
    //TODO index unique = user + group
    //TODO index unique = user + direct
    @Id
    private ObjectId id;

    @DBRef
    @Indexed
    @Valid
    @NotNull
    private User user;

    @DBRef
    @Nullable
    private Group group = null;

    @DBRef
    @Nullable
    private User direct;

    @DBRef
    private User sender;

    private String preview;

    private Integer unread;

    public UserChat(@Valid @NotNull User user, @Nullable User direct) {
        this.user = user;
        this.direct = direct;
    }

    public UserChat(@Valid User user, @Nullable User direct, @NotNull User sender) {
        this.user = user;
        this.direct = direct;
        this.sender = sender;
    }

    public UserChat(@Valid User user, @Nullable Group group, @NotNull User sender) {
        this.user = user;
        this.group = group;
        this.sender = sender;
    }

}
