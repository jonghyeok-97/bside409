package bsise.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // url pattern
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-resources/**",
                                "/login",
                                "/login/**",
                                "/static/**", // 정적 리소스
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/api/v1/letters",
                                "/api/v1/letters/**"
                        ).permitAll()
                        .requestMatchers("/letterbox").hasRole("OAUTH")
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2.loginPage("/login").permitAll())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(LogoutConfigurer::permitAll)
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(null)) // TODO: jwtEntryPoint 추가
                ;
        return http.build();
    }
}
