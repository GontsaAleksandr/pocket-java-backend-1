package ru.geekbrains.pocket.backend.exception;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

public class WebSocketException extends ExceptionWebSocketHandlerDecorator {
    public WebSocketException(WebSocketHandler delegate) {
        super(delegate);
    }
}
