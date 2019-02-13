package ru.geekbrains.pocket.backend.domain.pub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.geekbrains.pocket.backend.domain.db.UserChat;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChatPub {
    private String id;

    private GroupPub group; //Если это групповое сообщение

    private UserProfilePub direct; //Если это личное сообщение

    private UserProfilePub sender;

    private String preview; //Небольшое текст-превью последнего сообщения

    private Integer unread; //Сколько непрочитанных сообщений в истории

    public UserChatPub(@NotNull UserChat userChat) {
        this.id = userChat.getId().toString();
        if (userChat.getGroup() != null)
            this.group = new GroupPub(userChat.getGroup());
        this.direct = new UserProfilePub(userChat.getDirect());
        this.sender = new UserProfilePub(userChat.getSender());
        this.preview = userChat.getPreview();
        this.unread = userChat.getUnread();
    }

    @Override
    public String toString() {
        return "UserChat{" +
                "'id':'" + id + "'" +
                ", " + group +
                ", " + direct +
                ", " + sender +
                ", 'preview':'" + preview + "'" +
                ", 'unread':'" + unread + "'" +
                '}';
    }

}