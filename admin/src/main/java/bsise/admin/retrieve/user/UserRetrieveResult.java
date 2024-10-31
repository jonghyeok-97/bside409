package bsise.admin.retrieve.user;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserRetrieveResult {

    private UUID userId;
    private String nickname;
    private String email;
    private String preference;
    private String provider;
    private LocalDateTime createdAt;
    private boolean dormant;
    private LocalDateTime dormantAt;

    public static UserRetrieveResult of(User user) {
        return UserRetrieveResult.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .preference(user.getPreference())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .dormant(user.isDormant())
                .dormantAt(user.getDormantAt())
                .build();
    }
}
