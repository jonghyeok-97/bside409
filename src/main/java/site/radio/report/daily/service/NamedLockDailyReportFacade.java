package site.radio.report.daily.service;

import site.radio.common.NamedLockRepository;
import site.radio.error.NamedLockAcquisitionException;
import site.radio.report.daily.dto.DailyReportResponseDto;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NamedLockDailyReportFacade {

    private final NamedLockRepository namedLockRepository;
    private final DailyReportService dailyReportService;

    @Transactional
    public DailyReportResponseDto createDailyReportWithNamedLock(UUID userId, LocalDate targetDate) {
        String shortUserId = userId.toString().substring(8);
        String lockName = String.format("createDailyReport:%s:%s", shortUserId, targetDate.toString());

        boolean lockAcquired = namedLockRepository.acquireLock(lockName, 2);

        if (!lockAcquired) {
            throw new NamedLockAcquisitionException("Failed to acquire lock:" + Thread.currentThread().getName());
        }

        try {
            return dailyReportService.createDailyReportWithFacade(userId, targetDate);
        } finally {
            boolean released = namedLockRepository.releaseLock(lockName);
            if (!released) {
                log.warn("Failed to release lock: {}", lockName);
            }
        }
    }
}
