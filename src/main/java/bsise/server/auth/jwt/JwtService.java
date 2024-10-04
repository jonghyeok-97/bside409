package bsise.server.auth.jwt;

import static bsise.server.auth.jwt.JwtConstant.ACCESS_VALID_MILLIS;
import static bsise.server.auth.jwt.JwtConstant.REFRESH_VALID_MILLIS;
import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import bsise.server.auth.OAuth2Provider;
import bsise.server.auth.UpUserDetails;
import bsise.server.auth.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtService {

    @Value("${security.jwt.token.access-key}")
    private String accessKey;

    @Value("${security.jwt.token.refresh-key}")
    private String refreshKey;

    private final UserService userService;
    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;

    @PostConstruct
    protected void init() {
        accessSecretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(accessKey.getBytes()));
        refreshSecretKey = Keys.hmacShaKeyFor(Base64.getEncoder().encode(refreshKey.getBytes()));
    }

    public String issueAccessToken(Claims claims) {
        return issueToken(claims, accessSecretKey, ACCESS_VALID_MILLIS)
                .compact();
    }

    public String issueRefreshToken(Claims claims) {
        return issueToken(claims, refreshSecretKey, REFRESH_VALID_MILLIS)
                .compact();
    }

    private JwtBuilder issueToken(Claims claims, SecretKey secretKey, int expireAt) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(JwtConstant.ISSUER)
                .claims(claims)
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .expiration(Timestamp.valueOf(LocalDateTime.now().plus(expireAt, ChronoUnit.MILLIS)))
                .signWith(secretKey, SIG.HS256);
    }

    public String reIssueAccessToken(String jwt) {
        return reIssue(getClaims(jwt), accessSecretKey, ACCESS_VALID_MILLIS);
    }

    public String reIssueRefreshToken(String jwt) {
        return reIssue(getClaims(jwt), refreshSecretKey, REFRESH_VALID_MILLIS);
    }

    private String reIssue(Claims oldClaims, SecretKey secretKey, int expireAt) {
        return Jwts.builder()
                .id(oldClaims.getId())
                .issuer(oldClaims.getIssuer())
                .subject(oldClaims.getSubject())
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .expiration(Timestamp.valueOf(LocalDateTime.now().plus(expireAt, ChronoUnit.MILLIS)))
                .signWith(secretKey, SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String jwt) {
        String userId = getUserId(jwt);
        UserDetails userDetails;

        if (userService.isOAuth2User(userId)) {
            userDetails = userService.loadUserByOAuth2UserId(userId);
        } else {
            userDetails = userService.loadUserByUsername(userId);
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserId(String jwt) {
        return Jwts.parser()
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public Claims makeNewClaims(Authentication authentication) {
        UpUserDetails principal = (UpUserDetails) authentication.getPrincipal();
        String userId = principal.getUserId();
        Map<String, Object> attributes = principal.getAttributes();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String profileImageUrl = "";

        if (registrationId.equalsIgnoreCase(OAuth2Provider.KAKAO.getName())) {
            profileImageUrl = ((Map) ((Map) attributes.get("kakao_account")).get("profile")).get("profile_image_url").toString();
        }

        return Jwts.claims()
                .subject(userId)
                .add("role", authentication.getAuthorities())
                .add("profileImageUrl", profileImageUrl)
                .build();
    }

    public Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(refreshSecretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return resolveToken(request, HttpHeaders.AUTHORIZATION);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return resolveToken(request, X_REFRESH_TOKEN);
    }

    private String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isValidAccessToken(String jwt) {
        return validateToken(jwt, accessSecretKey);
    }

    public boolean isValidRefreshToken(String jwt) {
        return validateToken(jwt, refreshSecretKey);
    }

    private boolean validateToken(String jwt, SecretKey secretKey) {
        Jws<Claims> claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt);
            return !claims.getPayload().getExpiration().before(Timestamp.valueOf(LocalDateTime.now()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new ExpiredJwtException(null, null, "EXPIRED TOKEN");
        }
    }
}
