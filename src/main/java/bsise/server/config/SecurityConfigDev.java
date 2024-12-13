package bsise.server.config;

import bsise.server.auth.CookieEncodingFilter;
import bsise.server.auth.OAuth2SuccessHandler;
import bsise.server.auth.UpOAuth2UserService;
import bsise.server.auth.jwt.JwtAuthenticationEntryPoint;
import bsise.server.auth.jwt.JwtGeneratorFilter;
import bsise.server.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
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

@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class SecurityConfigDev {

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

        // filter: 개발 환경에서는 검증만 사용하지 않음
        http.addFilterAfter(jwtGeneratorFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CookieEncodingFilter("nickname", "--user-data"),
                UsernamePasswordAuthenticationFilter.class);

        // cors
        http.cors(AbstractHttpConfigurer::disable);

        // url pattern
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());

        // oauth2 (dev)
        http.oauth2Login(oauth2 -> oauth2
                        .loginPage("http://localhost:3000" + "/login")
                        .userInfoEndpoint(config -> config.userService(upOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler))
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        return http.build();
    }

    @Bean
    public JwtGeneratorFilter jwtGeneratorFilter(JwtService jwtService) {
        return new JwtGeneratorFilter(jwtService);
    }
}
