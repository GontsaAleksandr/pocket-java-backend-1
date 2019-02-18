package ru.geekbrains.pocket.backend.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.exception.UserNotFoundException;
import ru.geekbrains.pocket.backend.service.UserTokenService;

import javax.servlet.http.HttpServletRequest;

@Component
class HttpRequestComponent {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    private UserTokenService userTokenService;

    User getUserFromToken(HttpServletRequest request) throws UserNotFoundException {
        String header = request.getHeader(HEADER_STRING);
        String token = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            token = header.replace(TOKEN_PREFIX, "");
            return userTokenService.getUserByToken(token);
        }
        return null;
    }

    User getUserFromToken(String token) throws UserNotFoundException {
        return userTokenService.getUserByToken(token);
    }
}
