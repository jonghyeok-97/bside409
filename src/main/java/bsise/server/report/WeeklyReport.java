package bsise.server.report;

import bsise.server.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "weekly_report")
public class WeeklyReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "weekly_report_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "weekly_name", nullable = false)
    private String weeklyName;

    @Column(name = "published_count", nullable = false)
    private int publishedCount;

    @Column(name = "unpublished_count", nullable = false)
    private int unpublishedCount;

    @Column(name = "cheer_up", nullable = false)
    private String cheerUp;

    @Builder
    public WeeklyReport(String weeklyName, int publishedCount, int unpublishedCount, String cheerUp) {
        this.weeklyName = weeklyName;
        this.publishedCount = publishedCount;
        this.unpublishedCount = unpublishedCount;
        this.cheerUp = cheerUp;
    }
}
