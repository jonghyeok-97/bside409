package site.radio.config;

import static site.radio.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import java.util.Arrays;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import site.radio.auth.CookieEncodingFilter;
import site.radio.auth.OAuth2SuccessHandler;
import site.radio.auth.UpOAuth2UserService;
import site.radio.auth.jwt.JwtAuthenticationEntryPoint;
import site.radio.auth.jwt.JwtGeneratorFilter;
import site.radio.auth.jwt.JwtService;
import site.radio.auth.jwt.JwtValidatorFilter;

@EnableWebSecurity(debug = false)
@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Profile("prod")
public class SecurityConfig {

    @Value("${security.base-url}")
    private String baseUrl;
    private final JwtService jwtService;
    private final UpOAuth2UserService upOAuth2UserService;
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
            corsConfig.setAllowedMethods(Arrays.asList("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfig.setAllowedHeaders(Collections.singletonList("*"));
            corsConfig.setAllowCredentials(true);
            corsConfig.setExposedHeaders(
                    Arrays.asList(HttpHeaders.AUTHORIZATION, X_REFRESH_TOKEN, "Cache-Control", "Content-Type"));
            corsConfig.setMaxAge(3600L);
            return corsConfig;
        }));

        // filter
        http.addFilterAfter(jwtGeneratorFilter(jwtService), OAuth2LoginAuthenticationFilter.class);
        http.addFilterAfter(jwtValidatorFilter(jwtService), LogoutFilter.class);
        http.addFilterBefore(new CookieEncodingFilter("nickname", "--user-data"),
                OAuth2LoginAuthenticationFilter.class);

        // url pattern
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error",
                                "/login",
                                "/login/**",
                                "/oauth2/**",
                                "/static/**", // 정적 리소스
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/api/v1/users/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/replies**").authenticated()
                        .requestMatchers("/api/v1/replies/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인
                        .loginPage(baseUrl + "/login")
                        .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        ;
        return http.build();
    }

    @Bean
    public JwtGeneratorFilter jwtGeneratorFilter(JwtService jwtService) {
        return new JwtGeneratorFilter(jwtService);
    }

    @Bean
    public JwtValidatorFilter jwtValidatorFilter(JwtService jwtService) {
        return new JwtValidatorFilter(jwtService);
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowedHeaderNames((header) -> true);  // 모든 헤더 이름 허용
        firewall.setAllowedHeaderValues((header) -> true); // 모든 헤더 값 허용
        return firewall;
    }
}
