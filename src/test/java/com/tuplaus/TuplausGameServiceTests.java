package com.tuplaus;


import com.tuplaus.component.Money;
import com.tuplaus.component.ResultType;
import com.tuplaus.component.TuplausPickType;
import com.tuplaus.dto.TuplausWinningCheckResult;
import com.tuplaus.entity.User;
import com.tuplaus.exception.MoneyException;
import com.tuplaus.exception.TuplausException;
import com.tuplaus.repository.TuplausGameEventRepository;
import com.tuplaus.repository.TuplausGameRepository;
import com.tuplaus.repository.UserRepository;
import com.tuplaus.service.AccountService;
import com.tuplaus.service.TuplausGameService;
import com.tuplaus.service.TuplausGameWinningService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TuplausGameServiceTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TuplausGameService service;

    @Autowired
    private TuplausGameRepository tuplausGameRepository;

    @Autowired
    private TuplausGameEventRepository tuplausGameEventRepository;

    @Autowired
    private AccountService accountService;

    @MockBean
    private TuplausGameWinningService tuplausGameWinningService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().name("Keijo Keno").build();
        userRepository.save(user);

        accountService.deposit(user, "100");
    }

    @Test
    void gameCanBeStarted() {
        var game = service.startGame(user, new Money("10"));
        var repositoryGame = tuplausGameRepository.findById(game.getId());

        assertTrue(repositoryGame.isPresent());
        assertFalse(repositoryGame.get().hasEnded());
        assertEquals("10.0000", repositoryGame.get().getInitialBet().getAmountAsString());
    }

    @Test
    void gameCanBeEnded() {
        var game = service.startGame(user, new Money("10"));
        service.cashOut(game);

        var repositoryGame = tuplausGameRepository.findById(game.getId());

        assertTrue(repositoryGame.isPresent());
        assertTrue(repositoryGame.get().hasEnded());
    }

    @Test
    void cantEndGameThatHasEnded() {
        var game = service.startGame(user, new Money("10"));
        service.cashOut(game);

        assertThrows(TuplausException.class, () -> service.cashOut(game));
    }

    @Test
    void startingGameReducesAccountBalance() {
        assertEquals("100.0000", accountService.getAccountBalance(user).getAmountAsString());

        service.startGame(user, new Money("10"));

        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());
    }

    @Test
    void cantBetMoreThanBalance() {
        assertEquals("100.0000", accountService.getAccountBalance(user).getAmountAsString());

        assertThrows(MoneyException.class, () -> service.startGame(user, new Money("100.0001")));
    }

    @Test
    void gameRoundCanBePlayed() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        var game = service.startGame(user, new Money("10"));
        service.playRound(game, TuplausPickType.BIG);
    }

    @Test
    void gameEventIsCreatedFromPlayingRound() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        var game = service.startGame(user, new Money("10"));

        service.playRound(game, TuplausPickType.SMALL);

        var events = tuplausGameEventRepository.findByGame(game);

        assertEquals(1, events.size());
        assertEquals(game.getId(), events.get(0).getGame().getId());
    }

    @Test
    void pickingCorrectlyWinsRound() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        var game = service.startGame(user, new Money("10"));

        var result = service.playRound(game, TuplausPickType.SMALL);

        assertEquals(ResultType.WIN, result.result());
        assertFalse(game.hasEnded());
    }

    @Test
    void pickingIncorrectlyLosesGame() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.LOSE, 1));

        var game = service.startGame(user, new Money("10"));

        var result = service.playRound(game, TuplausPickType.BIG);

        assertEquals(ResultType.LOSE, result.result());
        assertTrue(game.hasEnded());
    }

    @Test
    void winningRoundDoublesPot() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));
        
        var game = service.startGame(user, new Money("10"));

        assertEquals("10.0000", service.getCurrentPot(game).getAmountAsString());

        service.playRound(game, TuplausPickType.SMALL);
        assertEquals("20.0000", service.getCurrentPot(game).getAmountAsString());

        service.playRound(game, TuplausPickType.SMALL);
        assertEquals("40.0000", service.getCurrentPot(game).getAmountAsString());
    }

    @Test
    void winningRoundDoesntIncreaseBalance() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        var game = service.startGame(user, new Money("10"));

        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());

        var result = service.playRound(game, TuplausPickType.SMALL);

        assertEquals(ResultType.WIN, result.result());
        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());
    }

    @Test
    void cashingOutAfterWinningRoundIncreasesBalance() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.WIN, 1));

        var game = service.startGame(user, new Money("10"));

        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());

        service.playRound(game, TuplausPickType.SMALL);
        service.cashOut(game);

        assertEquals("110.0000", accountService.getAccountBalance(user).getAmountAsString());
    }

    @Test
    void cantCashOutAfterLosingRound() {
        when(tuplausGameWinningService.determineWinning(any())).thenReturn(new TuplausWinningCheckResult(ResultType.LOSE, 1));

        var game = service.startGame(user, new Money("10"));

        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());

        service.playRound(game, TuplausPickType.BIG);

        assertThrows(TuplausException.class, () -> service.cashOut(game));

        assertEquals("90.0000", accountService.getAccountBalance(user).getAmountAsString());
    }
}
