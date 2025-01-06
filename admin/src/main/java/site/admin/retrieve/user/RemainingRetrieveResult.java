package site.admin.retrieve.user;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RemainingRetrieveResult {

    private UUID userId;
    private int remaining;

    public static RemainingRetrieveResult of(String userId, int remaining) {
        return new RemainingRetrieveResult(UUID.fromString(userId), remaining);
    }
}
