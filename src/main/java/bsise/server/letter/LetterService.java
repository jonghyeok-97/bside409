package bsise.server.letter;

import bsise.server.user.User;
import bsise.server.user.UserRepository;
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

    public LetterResponseDto saveLetter(LetterRequestDto letterDto) {
        User user = userRepository.findByEmail(letterDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + letterDto.getUserId()));

        Letter letter = letterDto.toLetterWithoutUser();
        letter.setUser(user);

        Letter savedLetter = letterRepository.save(letter);

        return LetterResponseDto.fromLetter(savedLetter);
    }

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

        Page<Letter> page = letterRepository.findAllByUserId(userId, pageable);

        return page.map(LetterResponseDto::fromLetter);
    }
}
