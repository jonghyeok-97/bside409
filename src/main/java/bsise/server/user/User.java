package bsise.server.user;

import bsise.server.auth.OAuth2Provider;
import bsise.server.auth.OAuth2UserInfo;
import bsise.server.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User extends BaseTimeEntity {

    private static final String TEMP_NICKNAME = "임시 닉네임";
    private static final String NONE_OF_EMAIL = "noneOfEmail@None.none";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false, length = 12)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference", nullable = false)
    private Preference preference;

    @Column(name = "is_synced")
    private boolean isSynced = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", nullable = false)
    private OAuth2Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder
    public User(String username, String email, String nickname, Preference preference, boolean isSynced,
                OAuth2Provider provider, Role role) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.preference = preference;
        this.isSynced = isSynced;
        this.provider = provider;
        this.role = role;
    }

    public static User makeFromOAuth2UserInfo(OAuth2UserInfo oAuth2UserInfo) {
        return User.builder()
                .username(oAuth2UserInfo.getUserName())
                .email(oAuth2UserInfo.getEmail())
                .nickname(TEMP_NICKNAME)
                .preference(Preference.F)
                .isSynced(false)
                .provider(OAuth2Provider.fromString(oAuth2UserInfo.getProvider()))
                .role(Role.OAUTH)
                .build();
    }

    public static User makeGuest() {
        return User.builder()
                .username(TEMP_NICKNAME)
                .email(NONE_OF_EMAIL)
                .nickname(TEMP_NICKNAME)
                .preference(Preference.F)
                .isSynced(false)
                .provider(OAuth2Provider.UNKNOWN)
                .role(Role.GUEST)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePreference(Preference preference) {
        this.preference = preference;
    }

    public void changeSynced(boolean isSynced) {
        this.isSynced = isSynced;
    }
}
