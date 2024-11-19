package bsise.server.report;

import bsise.server.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_report")
public class DailyReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "daily_report_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_report_id")
    private WeeklyReport weeklyReport;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "core_emotion", nullable = false)
    private CoreEmotion coreEmotion;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder
    public DailyReport(CoreEmotion coreEmotion, LocalDate targetDate, String description) {
        this.coreEmotion = coreEmotion;
        this.targetDate = targetDate;
        this.description = description;
    }
}

