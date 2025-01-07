package bsise.server.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationProblemDetails {
    private final String field;
    private final String problem;
}
