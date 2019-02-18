package ru.geekbrains.pocket.backend.security.token;

//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.impl.crypto.MacProvider;

import org.springframework.stereotype.Service;

//https://habr.com/ru/post/278411/

@Service
public class GetTokenServiceImpl { //implements GetTokenService {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    public TokenObject getUserToken(String username, String password) throws Exception {
//        if (username == null || password == null)
//            return null;
//        User user = (User) userDetailsService.loadUserByUsername(username);
//        Map<String, Object> tokenData = new HashMap<>();
//        if (password.equals(user.getPassword())) {
//            tokenData.put("clientType", "user");
//            tokenData.put("userID", user.getUserId().toString());
//            tokenData.put("username", authorizedUser.getUsername());
//            tokenData.put("token_create_date", new Date().getTime());
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.YEAR, 100);
//            tokenData.put("token_expiration_date", calendar.getTime());
//            JwtBuilder jwtBuilder = Jwts.builder();
//            jwtBuilder.setExpiration(calendar.getTime());
//            jwtBuilder.setClaims(tokenData);
//            String key = "abc123";
//            String token = jwtBuilder.signWith(SignatureAlgorithm.HS512, key).compact();
//            return token;
//        } else {
//            throw new Exception("Authentication error");
//        }
//    }

}
