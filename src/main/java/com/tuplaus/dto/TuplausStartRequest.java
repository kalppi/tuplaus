package com.tuplaus.dto;

import com.tuplaus.component.TuplausPickType;
import jakarta.validation.constraints.NotNull;

public record TuplausStartRequest(@NotNull long playerId, @NotNull String bet, @NotNull TuplausPickType pick) {
}
