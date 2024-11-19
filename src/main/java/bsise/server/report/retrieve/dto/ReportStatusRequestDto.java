package bsise.server.report.retrieve.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportStatusRequestDto {

    @NotBlank
    private String userId;
}
