package bsise.server.report.daily.domain;

import bsise.server.common.BaseTimeEntity;
import bsise.server.letter.Letter;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    @Type(JsonType.class)
    @Column(name = "sensitive_emotions", columnDefinition = "json")
    private List<String> sensitiveEmotions;

    @Column(name = "topic", nullable = false)
    private String topic;

    @ElementCollection // 값 타입 콜렉션
    @CollectionTable(name = "letter_core_emotions",
            joinColumns = @JoinColumn(name = "letter_analysis_id"))
    @Column(name = "core_emotion", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<CoreEmotion> coreEmotions;

    @Builder
    public LetterAnalysis(Letter letter, List<String> sensitiveEmotions, String topic, List<CoreEmotion> coreEmotions) {
        this.letter = letter;
        this.sensitiveEmotions = sensitiveEmotions;
        this.topic = topic;
        this.coreEmotions = coreEmotions;
    }
}
