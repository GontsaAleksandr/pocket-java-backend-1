package ru.geekbrains.pocket.backend.domain.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.messaging.handler.annotation.Payload;
import ru.geekbrains.pocket.backend.enumeration.MessageType;

//this class for class UserMessage & setGroupMessage

@Data
@NoArgsConstructor
@TypeAlias("attachment")
public class Attachment {
    private MessageType type;
    private Payload payload;

    @Override
    public String toString() {
        return "Attachment{" +
                "'type':" + type +
                ", 'payload':" + payload +
                '}';
    }
}
