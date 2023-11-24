package com.tuplaus.dto;

import com.tuplaus.component.TuplausPickType;
import jakarta.validation.constraints.NotNull;

public record TuplausRoundRequest(@NotNull long playerId, @NotNull  long gameId, @NotNull  TuplausPickType pick) {
}
