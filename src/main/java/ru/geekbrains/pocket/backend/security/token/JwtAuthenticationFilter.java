package ru.geekbrains.pocket.backend.security.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.geekbrains.pocket.backend.security.MyUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//https://www.devglan.com/spring-security/spring-boot-jwt-auth

@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        String token = req.getParameter("token");
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX,"");
            username = getUsernameFromToken(authToken);
        } else if (token != null && !token.equals("")) {
            authToken = token;
            username = getUsernameFromToken(authToken);
        } else {
            log.warn("couldn't find bearer string, will ignore the header");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                            userDetails.getAuthorities());
//                    new UsernamePasswordAuthenticationToken(userDetails, null,
//                            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                log.info("authenticated user " + username + ", setting security context.");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(req, res);
    }

    private String getUsernameFromToken(String authToken) {
        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(authToken);
        } catch (IllegalArgumentException e) {
            log.error("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            log.warn("the token is expired and not valid anymore", e);
        } catch(SignatureException e){
            log.error("Authentication Failed. Username or Password not valid.");
        }
        return username;
    }
}