package bsise.server.config;

import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import bsise.server.auth.UpOAuth2UserService;
import bsise.server.auth.jwt.JwtGeneratorFilter;
import bsise.server.auth.jwt.JwtValidatorFilter;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

    @Value("${security.base-url}")
    private String baseUrl;
    private final UpOAuth2UserService upOAuth2UserService;
    private final JwtGeneratorFilter jwtGeneratorFilter;
    private final JwtValidatorFilter jwtValidatorFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // cors
        http.cors(config -> config.configurationSource(request -> {
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.setAllowedOrigins(Collections.singletonList(baseUrl));
            corsConfig.setAllowedMethods(Collections.singletonList("*"));
            corsConfig.setAllowedHeaders(Collections.singletonList("*"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION, X_REFRESH_TOKEN));
            corsConfig.setMaxAge(3600L);
            return corsConfig;
        }));

        // filter
        http.addFilterAfter(jwtGeneratorFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(jwtValidatorFilter, BasicAuthenticationFilter.class);

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
                        .anyRequest().authenticated())
                .formLogin(form -> form // 게스트 로그인
                        .loginPage(baseUrl + "/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인
                        .loginPage(baseUrl + "/login")
                        .defaultSuccessUrl("/")
                        .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
                )
                .logout(LogoutConfigurer::permitAll)
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(null)) // TODO: jwtEntryPoint 추가
        ;
        return http.build();
    }
}
