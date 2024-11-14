package bsise.server.report;

import bsise.server.common.BaseTimeEntity;
import bsise.server.letter.Letter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "letter_analysis")
public class LetterAnalysis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_analysis_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;

    // TODO: JSON타입
    // private sensitiveEmotion;

    @Column(name = "topic", nullable = false)
    private String topic;

    @ElementCollection // 값 타입 콜렉션
    @CollectionTable(name = "letter_core_emotions",
            joinColumns = @JoinColumn(name = "letter_analysis_id"))
    @Column(name = "core_emotion", nullable = false)
    private List<CoreEmotion> coreEmotions;

    @Builder
    public LetterAnalysis(Letter letter, String topic, List<CoreEmotion> coreEmotions) {
        this.letter = letter;
        this.topic = topic;
        this.coreEmotions = coreEmotions;
    }
}
