package bsise.server.clovar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ClovaRole {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String role;
}
