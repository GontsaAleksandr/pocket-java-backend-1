package ru.geekbrains.pocket.backend.controller.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.response.GreetingResponse;

@Controller
public class UserWebsocketController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public GreetingResponse greeting(@RequestBody User user) {
        return new GreetingResponse("Hello, " + user.getEmail() + "!");
    }
}
