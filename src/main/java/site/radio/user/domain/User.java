package site.radio.user.domain;

import site.radio.auth.OAuth2Provider;
import site.radio.auth.OAuth2UserInfo;
import site.radio.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`user`")
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

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_email_ads_consented")
    private boolean isEmailAdsConsented = true;

    @Column(name = "agree_to_terms")
    private boolean agreeToTerms = false;

    @Column(name = "agree_to_privacy_policy")
    private boolean agreeToPrivacyPolicy = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider", nullable = false)
    private OAuth2Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "isDormant")
    private boolean isDormant = false;

    @Column(name = "dormant_at")
    private LocalDateTime dormantAt;

    @Builder
    public User(String username, String email, String nickname, Preference preference, boolean isSynced,
                String profileImageUrl, boolean isEmailAdsConsented, boolean agreeToTerms, boolean agreeToPrivacyPolicy,
                OAuth2Provider provider, Role role) {
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.preference = preference;
        this.isSynced = isSynced;
        this.profileImageUrl = profileImageUrl;
        this.isEmailAdsConsented = isEmailAdsConsented;
        this.agreeToTerms = agreeToTerms;
        this.agreeToPrivacyPolicy = agreeToPrivacyPolicy;
        this.isDormant = false;
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
                .profileImageUrl(oAuth2UserInfo.getProfileImage())
                .provider(OAuth2Provider.fromString(oAuth2UserInfo.getProvider()))
                .isEmailAdsConsented(true)
                .agreeToTerms(false)
                .agreeToPrivacyPolicy(false)
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
                .profileImageUrl("")
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

    public void changeEmailAdsConsent(boolean isConsented) {
        this.isEmailAdsConsented = isConsented;
    }

    public void changeToDormantAccount() {
        this.isDormant = true;
        this.dormantAt = LocalDateTime.now();
    }

    public void recoverFromDormantAccount() {
        this.isDormant = false;
        this.dormantAt = null;
    }

    public void changeAgreeToTerms(boolean agreeToTerms) {
        this.agreeToTerms = agreeToTerms;
    }

    public void changeAgreeToPrivacyPolicy(boolean agreeToPrivacyPolicy) {
        this.agreeToPrivacyPolicy = agreeToPrivacyPolicy;
    }
}
