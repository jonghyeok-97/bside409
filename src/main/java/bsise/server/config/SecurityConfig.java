package bsise.server.config;

import bsise.server.auth.UpOAuth2UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

    private final UpOAuth2UserService upOAuth2UserService;

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
                        .requestMatchers("/api/v1/replies/**").hasRole("OAUTH")
                        .anyRequest().permitAll())
                .formLogin(form -> form // 게스트 로그인
                        .loginPage("http://localhost:5173/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
//                        .successHandler() // TODO: jwt 발급 및 리다이렉트
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인
                                .loginPage("http://localhost:5173/login")
                                .defaultSuccessUrl("http://localhost:5173/")
                                .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
//                        .successHandler() // TODO: jwt 발급 및 리다이렉트
                )
                .logout(LogoutConfigurer::permitAll)
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(null)) // TODO: jwtEntryPoint 추가
                ;
        return http.build();
    }
}
