package ru.geekbrains.pocket.backend.domain.pub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.GroupMessage;
import ru.geekbrains.pocket.backend.domain.db.UserMessage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MessageCollection {

    private Integer offset;

    private List<MessagePub> data;

    public void setUserMessages(Integer offset, List<UserMessage> messages){
        this.offset = offset;
        this.data = messages.stream().map(MessagePub::new).collect(Collectors.toList());
    }

    public void setGroupMessages(Integer offset, List<GroupMessage> messages){
        this.offset = offset;
        this.data = messages.stream().map(MessagePub::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "MessageCollection{" +
                "'offset':'" + offset + "'" +
                ", " + data +
                '}';
    }

}