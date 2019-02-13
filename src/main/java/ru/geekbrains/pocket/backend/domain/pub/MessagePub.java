package ru.geekbrains.pocket.backend.domain.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;
import ru.geekbrains.pocket.backend.enumeration.MessageType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePub {

    private String id;

    private MessageType type; //direct|group

    private String sender; //<users:_id>

    private String recipient; //<users:_id> Только для личный сообщений

    private String group; //<group:_id> Только для групповых сообщений

    private String text;

    @JsonProperty("read")
    private boolean isRead; //Только для личный сообщений

    private Date sent_at;

    public MessagePub(UserMessage userMessage){
        this.id = userMessage.getId().toString();
        if (userMessage.getAttachment() != null)
            this.type = userMessage.getAttachment().getType();
        this.sender = userMessage.getSender().getId().toString();
        this.recipient = userMessage.getRecipient().getId().toString();
        this.group = null;
        this.text = userMessage.getText();
        this.isRead = userMessage.isRead();
        this.sent_at = userMessage.getSent_at();
    }

    public MessagePub(GroupMessage groupMessage){
        this.id = groupMessage.getId().toString();
        if (groupMessage.getAttachment() != null)
            this.type = groupMessage.getAttachment().getType();
        this.sender = groupMessage.getSender().getId().toString();
        this.recipient = null;
        this.group = groupMessage.getGroup().getId().toString();
        this.text = groupMessage.getText();
        this.isRead = false;
        this.sent_at = groupMessage.getSent_at();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Message {")
                .append("'id':'").append(id).append("'")
                .append(",'type':'").append(type).append("'")
                .append(", ").append(sender)
                .append(", ").append(recipient)
                .append(group == null ? "" : ", " + group)
                .append(",'text':'").append(text).append("'")
                .append(group == null ? "" : ", 'isRead':'" + isRead + "'")
                .append(",'sent_at':'").append(sent_at).append("'")
                .append("}");
        return builder.toString();
    }
}