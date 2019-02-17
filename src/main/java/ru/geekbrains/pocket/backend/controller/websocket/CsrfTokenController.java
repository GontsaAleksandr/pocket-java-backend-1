package ru.geekbrains.pocket.backend.controller.websocket;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//https://www.baeldung.com/spring-security-websockets
//5. Same Origin Policy
@Controller
public class CsrfTokenController {
    @GetMapping("/csrf")
    public @ResponseBody
    String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrf.getToken();
    }
}
