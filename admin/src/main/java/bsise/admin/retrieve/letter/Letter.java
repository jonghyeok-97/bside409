package bsise.admin.retrieve.letter;

import bsise.admin.retrieve.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "letter")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "letter_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "message")
    private String message;

    @Column(name = "preference")
    private String preference;

    @Column(name = "like_t")
    private Long likeT;

    @Column(name = "like_f")
    private Long likeF;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
