package com.tuplaus.entity;

import com.tuplaus.component.Money;
import com.tuplaus.converter.MoneyConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuplausGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Convert(converter = MoneyConverter.class)
    @NotNull
    @Column(precision = 12, scale = 4)
    private Money initialBet;

    @Column(columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime gameStart;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime gameEnd;

    public boolean hasEnded() {
        return gameEnd != null;
    }

    public boolean belongsToUser(User user) {
        return Objects.equals(this.getUser().getId(), user.getId());
    }
}
