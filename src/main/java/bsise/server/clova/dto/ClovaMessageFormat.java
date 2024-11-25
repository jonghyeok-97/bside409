package bsise.server.clova.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ClovaMessageFormat {

    private String role;
    private String content;

    public static ClovaMessageFormat of(ClovaRole role, String content) {
        return new ClovaMessageFormat(role.getRole(), content);
    }
}
