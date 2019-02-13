package ru.geekbrains.pocket.backend.domain.db;

import com.mongodb.lang.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Document(collection = "groups")
public class Group {

    @Id
    private ObjectId id;

    @DBRef
    @NotNull
    private User creator;

    @DBRef
    @Nullable
    private Project project = null;

    @Indexed
    @NotNull
    @NotEmpty
    private String name;

    private String description;

    @Indexed
    private String invitation_code; //приглашение

    @NotNull
    @Field("public")
    private boolean isPublic = false;

//    @Override
//    public String toString() {
//        return "Group{" +
//                "id=" + id +
//                ", creator=" + creator.getUsername() +
//                ", name=" + name +
//                ", description=" + description +
//                ", invitation_code=" + invitation_code +
//                ", isPublic=" + isPublic +
//                '}';
//    }

}
