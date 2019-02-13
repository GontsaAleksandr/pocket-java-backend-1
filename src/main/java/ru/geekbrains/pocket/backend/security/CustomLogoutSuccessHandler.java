package ru.geekbrains.pocket.backend.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    public CustomLogoutSuccessHandler() {
        super();
    }

    @Override
    public void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        final String refererUrl = request.getHeader("Referer");
        System.out.println("CustomLogoutSuccessHandler.onLogoutSuccess refererUrl : " + refererUrl);

        if (authentication != null) {
            System.out.println("User logout: " + authentication.getName());
        }

        request.getSession(false);
        response.getOutputStream().print("Logout");
        //response.sendRedirect(request.getContextPath() + "/login");
        response.setStatus(HttpStatus.OK.value());
        super.onLogoutSuccess(request, response, authentication);
    }

}
