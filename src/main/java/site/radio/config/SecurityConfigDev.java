package site.radio.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static site.radio.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import site.radio.auth.CookieEncodingFilter;
import site.radio.auth.OAuth2SuccessHandler;
import site.radio.auth.UpOAuth2UserService;
import site.radio.auth.jwt.JwtAuthenticationEntryPoint;

@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class SecurityConfigDev {

    @Value("${security.base-url}")
    private String baseUrl;
    private final UpOAuth2UserService upOAuth2UserService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // filter: 개발 환경에서는 검증만 사용하지 않음
        http.addFilterBefore(new CookieEncodingFilter("nickname", "--user-data"),
                UsernamePasswordAuthenticationFilter.class);

        // cors
        http.cors(cors -> cors.configurationSource(source -> corsConfiguration()));

        // url pattern
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());

        // oauth2 (dev)
        http.oauth2Login(oauth2 -> oauth2
                        .loginPage(baseUrl + "/login")
                        .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        return http.build();
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Collections.singletonList(baseUrl));
        corsConfig.setAllowedMethods(Arrays.asList("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setExposedHeaders(Arrays.asList(AUTHORIZATION, X_REFRESH_TOKEN, "Cache-Control", "Content-Type"));
        corsConfig.setMaxAge(3600L);

        return corsConfig;
    }
}
