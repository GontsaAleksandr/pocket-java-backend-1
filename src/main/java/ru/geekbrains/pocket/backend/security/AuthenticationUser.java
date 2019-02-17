package ru.geekbrains.pocket.backend.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.geekbrains.pocket.backend.domain.db.Privilege;
import ru.geekbrains.pocket.backend.domain.db.Role;
import ru.geekbrains.pocket.backend.domain.db.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationUser {
    //аутентификация в Spring

    public static void authWithoutPassword(User user) throws AuthenticationException {
        List<Privilege> privileges = user.getRoles().stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<GrantedAuthority> authorities = privileges.stream().map(
                p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void authWithPassword(User user) throws AuthenticationException {
        List<Privilege> privileges = user.getRoles().stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        List<GrantedAuthority> authorities = privileges.stream().map(
                p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toList());

        //https://www.baeldung.com/manually-set-user-authentication-spring-security
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), authorities);
//        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    public Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<String>();
        final List<Privilege> collection = new ArrayList<Privilege>();
        for (final Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}
