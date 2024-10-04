package bsise.server.letter;

import bsise.server.limiter.RateLimitException;
import bsise.server.limiter.RateLimitService;
import bsise.server.user.User;
import bsise.server.user.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;
    private final RateLimitService rateLimitService;

    public LetterResponseDto saveLetter(LetterRequestDto letterDto) {
        if (!rateLimitService.isRequestAllowed(letterDto.getUserId())) {
            throw new RateLimitException("요청 제한 횟수 초과");
        }
        User user = userRepository.findById(UUID.fromString(letterDto.getUserId()))
                .orElseThrow(() -> new NoSuchElementException("User not found: " + letterDto.getUserId()));

        Letter letter = letterDto.toLetterWithoutUser();
        letter.setUser(user);

        Letter savedLetter = letterRepository.save(letter);

        return LetterResponseDto.fromLetter(savedLetter);
    }

    // FIXME: presentation layer 에 절대 나가지 않도록 개선하기
    @Transactional(readOnly = true)
    public Letter findLetter(UUID letterId) {
        return letterRepository.findById(letterId)
                .orElseThrow(() -> new NoSuchElementException("Letter not found: " + letterId));
    }

    @Transactional(readOnly = true)
    public Page<LetterResponseDto> findMyLetters(Pageable pageable, UUID userId) {
        if (!userRepository.existsUserById(userId)) {
            throw new NoSuchElementException("User not found: " + userId);
        }

        Page<Letter> page = letterRepository.findLettersByUserId(userId, pageable);

        return page.map(LetterResponseDto::fromLetter);
    }

    public void deleteLetter(UUID letterId) {
        letterRepository.deleteById(letterId);
    }

    @Transactional(readOnly = true)
    public List<LetterResponseDto> getLatestLetters() {
        List<Letter> top10Letters = letterRepository.findTop10ByOrderByCreatedAtDesc();

        return top10Letters.stream()
                .map(LetterResponseDto::fromLetter)
                .toList();
    }
}
