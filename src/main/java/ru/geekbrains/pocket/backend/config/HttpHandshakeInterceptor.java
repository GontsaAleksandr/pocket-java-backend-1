package ru.geekbrains.pocket.backend.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

//for class WebSocketConfig

@Log4j2
@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        log.info("Call beforeHandshake");
//        if (SecurityContextHolder.getContext().getAuthentication() != null) {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String principal = authentication.getPrincipal().toString();
//            boolean isAuthenticated = authentication.isAuthenticated();
//            String name = authentication.getName();
//            log.info("name=" + name + ", isAuthenticated=" + isAuthenticated + ", principal=" + principal);
//        }

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("sessionId", session.getId());
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        log.info("Call afterHandshake");
//        if (SecurityContextHolder.getContext().getAuthentication() != null) {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            String principal = authentication.getPrincipal().toString();
//            boolean isAuthenticated = authentication.isAuthenticated();
//            String name = authentication.getName();
//            log.info("name=" + name + ", isAuthenticated=" + isAuthenticated + ", principal=" + principal);
//        }
    }

}
