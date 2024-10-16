package bsise.admin.modifier.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RateLimitChangeRequest {

    private final String target;
    private final int changeValue;
}
