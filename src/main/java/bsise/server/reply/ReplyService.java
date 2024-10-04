package bsise.server.reply;

import bsise.server.clovar.TwoTypeMessage;
import bsise.server.clovar.ClovaResponseDto;
import bsise.server.clovar.ClovaService;
import bsise.server.letter.Letter;
import bsise.server.letter.LetterResponseDto;
import bsise.server.letter.LetterService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ClovaService clovaService;
    private final LetterService letterService;
    private final ReplyRepository replyRepository;

    /**
     * <ol> 이 메서드는 순차대로 아래 작업을 수행합니다.
     * <li>유저가 저장한 편지를 기반으로 클로바로부터 답장을 생성합니다.</li>
     * <li>클로바가 생성한 답장을 유저가 작성한 편지와 연관짓고 저장합니다.</li>
     * </ol>
     *
     * @param letterResponse 유저의 편지 정보가 저장되어있는 dto
     * @return 저장한 답장에 대한 응답 dto
     */
    public ReplyResponseDto makeAndSaveReply(LetterResponseDto letterResponse) {
        ClovaResponseDto clovaResponse = clovaService.send(letterResponse.getContent());
        TwoTypeMessage twoTypeMessage = clovaService.extract(clovaResponse);

        Letter letter = letterService.findLetter(letterResponse.getLetterId());

        Reply reply = Reply.builder()
                .letter(letter)
                .messageForF(twoTypeMessage.getMessageForF())
                .messageForT(twoTypeMessage.getMessageForT())
                .build();

        Reply savedReply = replyRepository.save(reply);

        return ReplyResponseDto.of(savedReply);
    }

    public ReplyResponseDto findReply(UUID letterId) {
        Letter letter = letterService.findLetter(letterId);
        Reply reply = replyRepository.findByLetter(letter)
                .orElseThrow(() -> new NoSuchElementException("letter not found: " + letterId.toString()));

        return ReplyResponseDto.of(reply);
    }

    public List<ReplyResponseDto> findTopNLetterAndReply(Integer topN) {
        if (topN > 10) {
            topN = 10;
        }

        List<Reply> replies = replyRepository.findRepliesByOrderByCreatedAtDesc(topN);

        return replies.stream()
                .map(ReplyResponseDto::of)
                .toList();
    }
}
