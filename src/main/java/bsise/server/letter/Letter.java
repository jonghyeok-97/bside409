package bsise.server.letter;

import bsise.server.common.BaseTimeEntity;
import bsise.server.user.Preference;
import bsise.server.user.User;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "letter")
public class Letter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "letter_id")
    private UUID id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "preference")
    private Preference preference;

    @Column(name = "like_t")
    private AtomicLong likeT = new AtomicLong();

    @Column(name = "like_f")
    private AtomicLong likeF = new AtomicLong();

    @Builder
    public Letter(User user, String message, Preference preference) {
        this.user = user;
        this.message = message;
        this.preference = preference;
        this.likeT = new AtomicLong();
        this.likeF = new AtomicLong();
    }

    public void plusLikeT() {
        likeT.incrementAndGet();
    }

    public void plusLikeF() {
        likeF.incrementAndGet();
    }

    public void cancelLikeT() {
        likeT.decrementAndGet();
    }

    public void cancelLikeF() {
        likeF.decrementAndGet();
    }
}
