package ru.geekbrains.pocket.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.token.ClientKeyGenerator;
import org.springframework.security.oauth2.client.token.DefaultClientKeyGenerator;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import ru.geekbrains.pocket.backend.security.*;
import ru.geekbrains.pocket.backend.security.token.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfig() {
        super();
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.httpFirewall(allowHttpMethodsFirewall());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()   //Межсайтовая подделка запроса
                //.anonymous().disable()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(new AuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
//                .addFilterBefore(new ManagementEndpointAuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class)
                .addFilterAfter(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            //более конкретные правила должны стоять первыми, а затем более общие
                .antMatchers("/auth/**").permitAll() //rest
                .antMatchers("/socket/**").permitAll() //websocket
                //.antMatchers("/test/**").permitAll() //websocket
                .antMatchers("/**").hasAnyRole("ADMIN", "USER")
                //.anyRequest().hasAuthority("READ_PRIVILEGE")
                .anyRequest().authenticated()
            .and()
            .formLogin()
                //.passwordParameter("")
                .loginPage("/auth/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            .and()
            .logout()
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .deleteCookies("JSESSIONID")
                .permitAll()
//            .and() //не нужно из-за websocket http://qaru.site/questions/10468988/failed-to-create-websocket-connection-when-spring-security-is-on
//                .sessionManagement() //https://www.baeldung.com/spring-security-session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
//        https://www.baeldung.com/spring-security-websockets
                .headers().frameOptions().sameOrigin()
        ;
    }

    @Bean
    public HttpFirewall allowHttpMethodsFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "LINK", "UNLINK"));
        return firewall;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public ClientKeyGenerator clientKeyGenerator(){
        return new DefaultClientKeyGenerator();
    }

//    @Bean(name = "restTokenAuthenticationFilter")
//    public TokenAuthenticationFilter restTokenAuthenticationFilter() {
//        TokenAuthenticationFilter restTokenAuthenticationFilter = new TokenAuthenticationFilter();
//        tokenAuthenticationManager.setUserDetailsService(userService);
//        restTokenAuthenticationFilter.setAuthenticationManager(tokenAuthenticationManager);
//        return restTokenAuthenticationFilter;
//    }

}