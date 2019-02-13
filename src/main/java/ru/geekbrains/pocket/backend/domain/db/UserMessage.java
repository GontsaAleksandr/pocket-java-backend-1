package ru.geekbrains.pocket.backend.domain.db;

import com.mongodb.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users.messages")
public class UserMessage {

    @Id
    private ObjectId id;

    @DBRef
    @Indexed
    @NotNull
    private User sender; //отправитель

    @DBRef
    @Indexed
    @NotNull
    private User recipient; //получатель

    @NotNull
    @NotEmpty
    private String text;

    @Nullable
    private Attachment attachment = null;

    private boolean read = false;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date sent_at;

    public UserMessage(@NotNull User sender, @NotNull User recipient, @NotEmpty String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserMessage {")
                .append("'id':'").append(id).append("'")
                .append(", ").append(sender)
                .append(", ").append(recipient)
                .append(",'text':'").append(text).append("'")
                .append(",'sent_at':'").append(sent_at).append("'")
                .append("}");
        return builder.toString();
    }
}
