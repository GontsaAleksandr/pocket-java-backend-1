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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "groups.messages")
public class GroupMessage {

    @Id
    private ObjectId id;

    @DBRef
    @NotNull
    private User sender;

    @DBRef
    @Indexed
    @NotNull
    private Group group;

    @NotEmpty
    private String text;

    @Nullable
    private Object in_thread = null;

    @DBRef
    @Nullable
    private GroupMessage belongs_to = null;

    @Nullable
    private Attachment attachment = null;

    private Date sent_at;

    public GroupMessage(@NotNull User sender, @NotNull Group group, @NotEmpty String text) {
        this.sender = sender;
        this.group = group;
        this.text = text;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("GroupMessage {")
                .append("'id':'").append(id).append("'")
                .append(", ").append(sender)
                .append(", ").append(group)
                .append(",'text':'").append(text).append("'")
                .append(",'sent_at':'").append(sent_at).append("'")
                .append("}");
        return builder.toString();
    }

}
