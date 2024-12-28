package bsise.server.reply;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyQuerydslRepository {

    Page<Reply> findLatestRepliesBy(UUID userId, LocalDateTime startOfYear, LocalDateTime endOfYear, Boolean published,
                                    Pageable pageable);
}
