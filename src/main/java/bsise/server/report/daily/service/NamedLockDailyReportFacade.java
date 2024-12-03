package bsise.server.report.daily.service;

import bsise.server.common.NamedLockRepository;
import bsise.server.error.NamedLockAcquisitionException;
import bsise.server.report.daily.dto.DailyReportDto;
import bsise.server.report.daily.dto.DailyReportResponseDto;
import java.time.LocalDate;
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
    public DailyReportResponseDto createDailyReportWithNamedLock(DailyReportDto.CreateRequest dailyReportDto) {
        String shortUserId = dailyReportDto.getUserId().substring(8);
        LocalDate targetDate = dailyReportDto.getDate();
        String lockName = String.format("createDailyReport:%s:%s", shortUserId, targetDate.toString());

        boolean lockAcquired = namedLockRepository.acquireLock(lockName, 2);

        if (!lockAcquired) {
            throw new NamedLockAcquisitionException("Failed to acquire lock:" + Thread.currentThread().getName());
        }

        try {
            return dailyReportService.createDailyReportWithFacade(dailyReportDto);
        } finally {
            boolean released = namedLockRepository.releaseLock(lockName);
            if (!released) {
                log.warn("Failed to release lock: {}", lockName);
            }
        }
    }
}
