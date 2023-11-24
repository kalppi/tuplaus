package com.tuplaus.entity;

import com.tuplaus.component.Money;
import com.tuplaus.component.TuplausPickType;
import com.tuplaus.converter.MoneyConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuplausGameEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @NotNull
    private TuplausGame game;

    @Convert(converter = MoneyConverter.class)
    @NotNull
    @Column(precision = 12, scale = 4)
    private Money bet;

    @Convert(converter = MoneyConverter.class)
    @NotNull
    @Column(precision = 12, scale = 4)
    private Money pot;

    @NotNull
    private TuplausPickType pick;

    @NotNull
    private Integer card;
}
