package ru.geekbrains.pocket.backend.security.token;

//@Service
public class TokenAuthenticationManager { //implements AuthenticationManager {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        try {
//            if (authentication instanceof TokenAuthentication) {
//                TokenAuthentication readyTokenAuthentication = processAuthentication((TokenAuthentication) authentication);
//                return readyTokenAuthentication;
//            } else {
//                authentication.setAuthenticated(false);
//                return authentication;
//            }
//        } catch (Exception ex) {
//            if(ex instanceof AuthenticationServiceException)
//                throw ex;
//        }
//    }
//
//    private TokenAuthentication processAuthentication(TokenAuthentication authentication) throws AuthenticationException {
//        String token = authentication.getUserToken();
//        String key = "key123";
//        DefaultClaims claims;
//        try {
//            claims = (DefaultClaims) Jwts.parser().setSigningKey(key).parse(token).getBody();
//        } catch (Exception ex) {
//            throw new AuthenticationServiceException("Token corrupted");
//        }
//        if (claims.get("TOKEN_EXPIRATION_DATE", Long.class) == null)
//            throw new AuthenticationServiceException("Invalid token");
//        Date expiredDate = new Date(claims.get("TOKEN_EXPIRATION_DATE", Long.class));
//        if (expiredDate.after(new Date()))
//            return buildFullTokenAuthentication(authentication, claims);
//        else
//            throw new AuthenticationServiceException("Token expired date error");
//    }
//
//    private TokenAuthentication buildFullTokenAuthentication(TokenAuthentication authentication, DefaultClaims claims) {
//        User user = (User) userDetailsService.loadUserByUsername(claims.get("USERNAME", String.class));
//        if (user.isEnabled()) {
//            Collection<GrantedAutority> authorities = user.getAuthorities();
//            TokenAuthentication fullTokenAuthentication =
//                    new TokenAuthentication(authentication.getUserToken(), authorities, true, user);
//            return fullTokenAuthentication;
//        } else {
//            throw new AuthenticationServiceException("User disabled");;
//        }
//    }
//
//    public void setUserDetailsService(UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }

}
