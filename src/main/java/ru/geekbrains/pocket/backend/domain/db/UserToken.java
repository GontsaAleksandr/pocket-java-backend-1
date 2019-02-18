package ru.geekbrains.pocket.backend.domain.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users.tokens")
public class UserToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    private ObjectId id;

    @NotNull
    @Valid
    @DBRef
    private User user;

    @NotNull
    @Indexed(unique = true)
    private String token; //несколько токенов на разные устройства

    @Field("user_ip")
    private String userip = "0.0.0.0";

    private String agent;

    @Field("loggedAt")
    private Date loggedAt = new Date();

    //TODO убрать лишнее
    private Date expiryDate;

    public UserToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public UserToken(final String token, final User user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public UserToken(final String token, final User user, final Date expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public void updateToken(final String token) {
        this.token = token;
        this.loggedAt = new Date();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public void updateToken(final String token, final Date expiryDate) {
        this.token = token;
        this.loggedAt = new Date();
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Token [String=").append(token).append("]")
                .append("[loggedAt=").append(expiryDate).append("]")
                .append("[Expires=").append(expiryDate).append("]");
        return builder.toString();
    }

    //

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
//        result = prime * result + ((token == null) ? 0 : token.hashCode());
//        result = prime * result + ((user == null) ? 0 : user.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(final Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final UserToken other = (UserToken) obj;
//        if (expiryDate == null) {
//            if (other.expiryDate != null) {
//                return false;
//            }
//        } else if (!expiryDate.equals(other.expiryDate)) {
//            return false;
//        }
//        if (token == null) {
//            if (other.token != null) {
//                return false;
//            }
//        } else if (!token.equals(other.token)) {
//            return false;
//        }
//        if (user == null) {
//            if (other.user != null) {
//                return false;
//            }
//        } else if (!user.equals(other.user)) {
//            return false;
//        }
//        return true;
//    }

}
