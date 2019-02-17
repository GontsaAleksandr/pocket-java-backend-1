package ru.geekbrains.pocket.backend.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import ru.geekbrains.pocket.backend.domain.db.User;
import ru.geekbrains.pocket.backend.security.AuthenticationUser;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

//@Log4j2
//@CrossOrigin(origins = "*", maxAge = 3600)
//@RestController
public class SocketRestController {

//    private SimpUserRegistry userRegistry;

    @Autowired
    private HttpRequestComponent httpRequestComponent;

//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;

//    @Autowired
//    public SocketRestController(SimpUserRegistry userRegistry) {
//        this.userRegistry = userRegistry;
//    }

    //@GetMapping("/socket") //Создать WS соеденение
    //В отличие от других запросов, токен передается не в заголовке а в get-параметре
    public ResponseEntity<?> openSocket(@RequestParam("token") String token,
                                        HttpServletRequest request) {
        User user = httpRequestComponent.getUserFromToken(token);
        List<String> result = new ArrayList<>();
        if (user != null){
//            for (SimpUser simpUser : this.userRegistry.getUsers()) {
//                result.add(simpUser.toString());
//            }

            AuthenticationUser.authWithoutPassword(user);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

//https://www.baeldung.com/spring-security-websockets
    //var endpoint = '/ws/?access_token=' + auth.access_token;
    //var socket = new SockJS(endpoint);
    //var stompClient = Stomp.over(socket);
}
