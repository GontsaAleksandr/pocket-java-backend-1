package ru.geekbrains.pocket.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
        response.setStatus(403);

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null) {
            log.warn("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
        }

        response.getWriter().flush();
    }

}
