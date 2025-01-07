package site.admin.retrieve.letter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LetterRetrieveService {

    private final ReplyRepository replyRepository;

    public Page<LetterRetrieveResult> retrieveReplies(Pageable pageable) {
        Page<Reply> replies = replyRepository.findMessages(pageable);

        List<LetterRetrieveResult> content = replies.stream()
                .map(LetterRetrieveResult::of)
                .toList();

        return new PageImpl<>(content, pageable, replies.getTotalElements());
    }
}
