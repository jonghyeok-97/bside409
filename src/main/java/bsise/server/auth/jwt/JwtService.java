package bsise.server.auth.jwt;

import static bsise.server.auth.jwt.JwtConstant.ACCESS_VALID_MILLIS;
import static bsise.server.auth.jwt.JwtConstant.REFRESH_VALID_MILLIS;
import static bsise.server.auth.jwt.JwtConstant.X_REFRESH_TOKEN;

import bsise.server.auth.UpOAuth2UserService;
import bsise.server.auth.UpUserDetails;
import bsise.server.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtService {

    private static final String SEOUL = "Asia/Seoul";

    @Value("${security.jwt.token.access-key}")
    private String accessKey;

    @Value("${security.jwt.token.refresh-key}")
    private String refreshKey;

    private final UpOAuth2UserService oAuth2UserService;
    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;

    @PostConstruct
    protected void init() {
        accessSecretKey = Keys.hmacShaKeyFor(accessKey.getBytes());
        refreshSecretKey = Keys.hmacShaKeyFor(refreshKey.getBytes());
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
        Instant seoulInstant = getSeoulInstant();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer(JwtConstant.ISSUER)
                .claims(claims)
                .issuedAt(Date.from(seoulInstant))
                .expiration(Date.from(seoulInstant.plus(expireAt, ChronoUnit.MILLIS)))
                .signWith(secretKey, SIG.HS256);
    }

    private Instant getSeoulInstant() {
        return ZonedDateTime.now(ZoneId.of(SEOUL)).toInstant();
    }

    public String reIssueAccessToken(String jwt) {
        return reIssue(getClaims(jwt, accessSecretKey), accessSecretKey, ACCESS_VALID_MILLIS);
    }

    public String reIssueRefreshToken(String jwt) {
        return reIssue(getClaims(jwt, refreshSecretKey), refreshSecretKey, REFRESH_VALID_MILLIS);
    }

    private String reIssue(Claims oldClaims, SecretKey secretKey, int expireAt) {
        Instant seoulInstant = getSeoulInstant();
        return Jwts.builder()
                .id(oldClaims.getId())
                .issuer(oldClaims.getIssuer())
                .subject(oldClaims.getSubject())
                .issuedAt(Date.from(seoulInstant))
                .expiration(Date.from(seoulInstant.plus(expireAt, ChronoUnit.MILLIS)))
                .signWith(secretKey, SIG.HS256)
                .compact();
    }

    public Authentication getAuthentication(String jwt) {
        String userId = getUserId(jwt);
        UserDetails userDetails = oAuth2UserService.loadUserByUsername(userId);

        RememberMeAuthenticationToken authenticationToken = new RememberMeAuthenticationToken(userId, userDetails,
                userDetails.getAuthorities());
        authenticationToken.setDetails(userDetails);

        return authenticationToken;
    }

    public String getUserId(String jwt) {
        return Jwts.parser()
                .clock(() -> Date.from(getSeoulInstant()))
                .verifyWith(accessSecretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    public Claims makeNewClaims(Authentication authentication) {
        UpUserDetails principal = (UpUserDetails) authentication.getPrincipal();
        User authenticatedUser = principal.getUser();
        String userId = authenticatedUser.getId().toString();
        String profileImageUrl = authenticatedUser.getProfileImageUrl();

        return Jwts.claims()
                .subject(userId)
                .add("role", authentication.getAuthorities())
                .add("profileImageUrl", profileImageUrl)
                .build();
    }

    public Claims getClaims(String jwt, SecretKey secretKey) {
        return Jwts.parser()
                .clock(() -> Date.from(getSeoulInstant()))
                .verifyWith(secretKey)
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
        try {
            Jwts.parser()
                    .clock(() -> Date.from(getSeoulInstant()))
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
