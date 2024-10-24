package bsise.server.user.dto;

import bsise.server.user.domain.WithdrawalReason;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;

@Schema(description = "회원 탈퇴 요청 DTO")
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDeleteRequestDto {

    @Schema(description = "유저가 선택한 번호", exampleClasses = WithdrawalReason.class, examples = {"selectedNumber: 1 => 자주 사용하지 않아요."})
    private int selectedNumber;

    @Schema(description = "유저가 입력한 상세 탈퇴 사유. 입력이 없으면 해당 필드를 포함시키지 않는다.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String detailReason;

    public WithdrawalReason getWithdrawalReason() {
        return WithdrawalReason.of(selectedNumber);
    }

    public Optional<String> getDetailReason() {
        return StringUtils.hasText(detailReason) ? Optional.of(detailReason) : Optional.empty();
    }
}
