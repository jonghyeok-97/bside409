package bsise.server.reply;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "reply", description = "답장 API")
@RestController
@RequestMapping(path = "/api/v1/replies", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "답장 조회 API", description = "편지의 식별자로 답장을 찾아 반환합니다.")
    @GetMapping("/{letterId}")
    @ResponseStatus(HttpStatus.OK)
    public ReplyResponseDto retrieveReply(@PathVariable("letterId") String letterId) {
        return replyService.findReply(UUID.fromString(letterId));
    }

}
