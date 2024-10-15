package bsise.admin.retrieve.letter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/messages")
public class LetterRetrieveController {

    private final LetterRetrieveService replyService;

    @GetMapping
    public Page<LetterRetrieveResult> retrieveMessages(@PageableDefault(direction = Direction.DESC) Pageable pageable) {
        return replyService.retrieveReplies(pageable);
    }
}
