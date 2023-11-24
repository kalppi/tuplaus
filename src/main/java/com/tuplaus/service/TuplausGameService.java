package com.tuplaus.service;

import com.tuplaus.component.Money;
import com.tuplaus.component.ResultType;
import com.tuplaus.component.TuplausPickType;
import com.tuplaus.dto.TuplausRoundResult;
import com.tuplaus.entity.TuplausGameEvent;
import com.tuplaus.entity.TuplausGame;
import com.tuplaus.entity.User;
import com.tuplaus.exception.TuplausException;
import com.tuplaus.repository.TuplausGameEventRepository;
import com.tuplaus.repository.TuplausGameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class TuplausGameService {
    private final TuplausGameRepository tuplausGameRepository;

    private final TuplausGameEventRepository tuplausGameEventRepository;

    private final TuplausGameWinningService tuplausGameWinningService;

    private final AccountService accountService;

    private Money calculatePot(Money bet) {
        return bet.multiply(2);
    }

    @Autowired
    public TuplausGameService(
            TuplausGameRepository tuplausGameRepository,
            TuplausGameEventRepository tuplausGameEventRepository,
            TuplausGameWinningService tuplausGameWinningService,
            AccountService accountService
    ) {
        this.tuplausGameRepository = tuplausGameRepository;
        this.tuplausGameEventRepository = tuplausGameEventRepository;
        this.tuplausGameWinningService = tuplausGameWinningService;
        this.accountService = accountService;
    }

    public TuplausGame findGame(long gameId, User user) {
        var game = tuplausGameRepository.findById(gameId).orElseThrow(() -> new TuplausException(("Game not found")));

        if (!game.belongsToUser(user)) {
            throw new TuplausException("Game does not belong to user");
        }

        return game;
    }

    @Transactional
    public TuplausGame startGame(User user, Money bet) {
        accountService.withdraw(user, bet.getAmountAsString());

        var time  = LocalDateTime.now();

        var game = TuplausGame.builder()
                .user(user)
                .initialBet(bet)
                .gameStart(time)
                .build();

        tuplausGameRepository.save(game);

        return game;
    }

    @Transactional
    public void cashOut(TuplausGame game) {
        endGame(game);

        var pot = getCurrentPot(game);

        accountService.deposit(game.getUser(), pot.getAmountAsString());
    }
    private void endGame(TuplausGame game) {
        if (game.hasEnded()) {
            throw new TuplausException("Can't end a game that has ended");
        }

        game.setGameEnd(LocalDateTime.now());

        tuplausGameRepository.save(game);
    }

    public Money getCurrentPot(TuplausGame game) {
        var latestEvent = tuplausGameEventRepository.findLatestByGame(game);

        return getCurrentPot(game, latestEvent);
    }

    private Money getCurrentPot(TuplausGame game, TuplausGameEvent latestEvent) {
        return latestEvent == null ? game.getInitialBet() : calculatePot(latestEvent.getPot());
    }

    private Money getCurrentBet(TuplausGame game, TuplausGameEvent latestEvent) {
        return latestEvent == null ? game.getInitialBet() : latestEvent.getBet();
    }

    @Transactional
    public TuplausRoundResult playRound(TuplausGame game, TuplausPickType pick) {
        if (game.hasEnded()) {
            throw new TuplausException("Can't play a game that has ended");
        }

        var result = tuplausGameWinningService.determineWinning(pick);
        var time  = LocalDateTime.now();

        var latestEvent = tuplausGameEventRepository.findLatestByGame(game);
        var bet = getCurrentBet(game, latestEvent);
        var pot = result.result() == ResultType.WIN ? getCurrentPot(game, latestEvent) : new Money("0");

        var gameEvent = TuplausGameEvent.builder()
                .timestamp(time)
                .game(game)
                .pot(pot)
                .bet(bet)
                .card(result.card())
                .pick(pick)
                .build();

        tuplausGameEventRepository.save(gameEvent);

        if (result.result() == ResultType.LOSE) {
            endGame(game);
        }

        return new TuplausRoundResult(result.result(), result.card());
    }
}
