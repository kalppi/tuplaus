package com.tuplaus.dto;

import com.tuplaus.component.ResultType;

public record TuplausStartResponse(long gameId, ResultType result, int card, String potentialPot, String balance) {
}
