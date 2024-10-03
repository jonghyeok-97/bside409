package bsise.server.config;

import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import bsise.server.auth.OAuth2SuccessHandler;
import bsise.server.auth.UpOAuth2UserService;
import bsise.server.auth.jwt.JwtAuthenticationEntryPoint;
import bsise.server.auth.jwt.JwtGeneratorFilter;
import bsise.server.auth.jwt.JwtValidatorFilter;
import java.util.Arrays;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityConfig {

    @Value("${security.base-url}")
    private String baseUrl;
    private final UpOAuth2UserService upOAuth2UserService;
    private final JwtGeneratorFilter jwtGeneratorFilter;
    private final JwtValidatorFilter jwtValidatorFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
            corsConfig.setAllowedMethods(Arrays.asList("HEAD", "OPTIONS", "GET", "POST", "PUT", "DELETE"));
            corsConfig.setAllowedHeaders(Collections.singletonList("*"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setExposedHeaders(
                    Arrays.asList(HttpHeaders.AUTHORIZATION, X_REFRESH_TOKEN, "Cache-Control", "Content-Type"));
            corsConfig.setMaxAge(3600L);
            return corsConfig;
        }));

        // filter
        http.addFilterAfter(jwtGeneratorFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(jwtValidatorFilter, LogoutFilter.class);

        // url pattern
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/login",
                                "/login/**",
                                "/oauth2/**",
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
                        .defaultSuccessUrl(baseUrl + "/")
                )
                .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인
                        .loginPage(baseUrl + "/login")
//                        .defaultSuccessUrl(baseUrl + "/")
                        .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        ;
        return http.build();
    }
}
