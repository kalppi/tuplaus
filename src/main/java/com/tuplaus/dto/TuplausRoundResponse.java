package com.tuplaus.dto;

import com.tuplaus.component.ResultType;

public record TuplausRoundResponse(ResultType result, int card, String potentialPot, String balance) {
}
