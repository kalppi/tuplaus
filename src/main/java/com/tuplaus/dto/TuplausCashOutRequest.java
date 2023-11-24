package com.tuplaus.dto;

import jakarta.validation.constraints.NotNull;

public record TuplausCashOutRequest(@NotNull long playerId, @NotNull long gameId) {
}
