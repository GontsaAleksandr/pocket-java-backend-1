package ru.geekbrains.pocket.backend.config;

//@Log4j2
//@Configuration
public class CORSConfig { //implements WebMvcConfigurer {

    //===CORS===

    //https://spring.io/guides/gs/rest-service-cors/
    //https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-cors
    //надо прописать для любых ответов: и 200+ и ошибок и редиректов, если будут и даже для 500.
    //res.setHeader('Access-Control-Allow-Origin', '*'); - разрешает передачу данных на любой домен (включая тот же домен, но другой порт)
    //res.setHeader('Access-Control-Allow-Methods','OPTIONS, GET, POST, PUT, PATCH, DELETE, UNLINK, LINK'); - наименование разрешенных типов http-запросов
    //res.setHeader('Access-Control-Allow-Headers', 'Authentication'); - список разрешенных заголовков (только дополнительных, стандартные разрешены по умолчанию)
/*    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:9000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
//                        .exposedHeaders("*")
                        .allowCredentials(true).maxAge(3600);
//                registry.addMapping("/web/**")
//                        .allowedOrigins("http://localhost:9000")
//                        .allowedMethods("*")
//                        .allowedHeaders("*");
//                registry.addMapping("/test/**")
//                        .allowedOrigins("http://localhost:9000")
//                        .allowedMethods("*")
//                        .allowedHeaders("*");
//                registry.addMapping("/ws/**")
//                        .allowedOrigins("http://localhost:9000")
//                        .allowedMethods("*")
//                        .allowedHeaders("*");

//                registry.addMapping("/api/users/**")
//                        .allowedOrigins("http://localhost:9000")
//                        .allowedMethods("PUT", "DELETE")
//                        .allowedHeaders("header1", "header2", "header3")
//                        .exposedHeaders("header1", "header2")
            }
        };
    }*/

//    @Bean
//    public CorsFilter corsFilter() {
//        log.debug("Filchain");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // you USUALLY want this
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/api/**", config);
//        log.debug("Filchain");
//        return new CorsFilter(source);
//    }

}
