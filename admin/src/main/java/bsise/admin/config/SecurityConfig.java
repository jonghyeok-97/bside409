package bsise.admin.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // cors
        http.cors(config -> config.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOrigins(Collections.singletonList("localhost:5173/login"));
            corsConfig.setAllowedMethods(Arrays.asList("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfig.setAllowedHeaders(Collections.singletonList("*"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
            corsConfig.setMaxAge(3600L);
            return corsConfig;
        }));

        // url pattern
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll()); // FIXME: 개발 후 `.authenticated()`
        http.formLogin(form -> form.loginPage("localhost:5173/login"));
        return http.build();
    }
}
