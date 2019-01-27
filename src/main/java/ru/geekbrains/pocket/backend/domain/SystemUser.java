package ru.geekbrains.pocket.backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.geekbrains.pocket.backend.util.validation.FieldMatch;
import ru.geekbrains.pocket.backend.util.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//this class for Spring Security

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldMatch(first = "password", second = "matchingPassword", message = "The password fields must match")
public class SystemUser {
    @NotNull(message = "is required")
    @Size(min = 6, message = "username length must be greater than 3 symbols")
    //@Pattern(regexp = "^[a-zA-Z0-9]{4}", message = "only 4 letters/digits")
//    @Pattern(regexp = "^\\S*(?=\\S{6,})(?=\\S*[a-z])(?=\\S*[A-Z])(?=\\S*[\\d])\\S*$",
//            message = "Enter a password containing 6 characters with at least one capital letter and one number.")
    private String username;

    @NotNull(message = "is required")
    @Size(min = 3, message = "is required")
    private String password;

    @NotNull(message = "is required")
    @Size(min = 3, message = "is required")
    private String matchingPassword;

    @NotNull(message = "is required")
    @Size(min = 3, message = "is required")
    private String lastname;

    @NotNull(message = "is required")
    @Size(min = 3, message = "is required")
    private String firstname;

    @ValidEmail
    @NotNull(message = "is required")
    @Size(min = 6, message = "is required")
    private String email;
}
