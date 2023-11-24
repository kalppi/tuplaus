package com.tuplaus.service;

import com.tuplaus.component.ResultType;
import com.tuplaus.component.TuplausPickType;
import com.tuplaus.dto.TuplausWinningCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TuplausGameWinningService {
    private final RandomCardService randomCardService;

    @Autowired
    TuplausGameWinningService(RandomCardService randomCardService) {
        this.randomCardService = randomCardService;
    }

    private ResultType getResult(int card, TuplausPickType pick) {
        if (card == 7) {
            return ResultType.LOSE;
        }

        if (card <= 6 && pick == TuplausPickType.SMALL) {
            return ResultType.WIN;
        }

        if (card >= 8 && pick == TuplausPickType.BIG) {
            return ResultType.WIN;
        }

        return ResultType.LOSE;
    }
    public TuplausWinningCheckResult determineWinning(TuplausPickType pick) {
        var card = randomCardService.getRandomCard();

        return new TuplausWinningCheckResult(getResult(card, pick), card);
    }
}
