package com.tuplaus.controller;

import com.tuplaus.component.Money;
import com.tuplaus.dto.*;
import com.tuplaus.exception.TuplausException;
import com.tuplaus.repository.UserRepository;
import com.tuplaus.service.AccountService;
import com.tuplaus.service.TuplausGameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class TuplausController {
	private final UserRepository userRepository;

	private final AccountService accountService;

	private final TuplausGameService tuplausGameService;

	@Autowired
	public TuplausController(UserRepository userRepository, AccountService accountService, TuplausGameService tuplausGameService) {
		this.userRepository = userRepository;
		this.accountService = accountService;
		this.tuplausGameService = tuplausGameService;
	}

	@PostMapping("/api/start")
	public ResponseEntity<SuccessResponse<TuplausStartResponse>> start(@Valid @RequestBody TuplausStartRequest request) {
		var user = userRepository.findById(request.playerId()).orElseThrow(() -> new TuplausException("User not found"));

		var game = tuplausGameService.startGame(user, new Money(request.bet()));
		var roundResponse = tuplausGameService.playRound(game, request.pick());

		var response = new SuccessResponse<>(new TuplausStartResponse(
			game.getId(),
			roundResponse.result(),
			roundResponse.card(),
			tuplausGameService.getCurrentPot(game).getFormattedAmount(),
			accountService.getAccountBalance(user).getFormattedAmount()
		));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/double")
	public ResponseEntity<SuccessResponse<TuplausRoundResponse>> round(@Valid @RequestBody TuplausRoundRequest request) {
		var user = userRepository.findById(request.playerId()).orElseThrow(() -> new TuplausException("User not found"));
		var game = tuplausGameService.findGame(request.gameId(), user);

		var roundResponse = tuplausGameService.playRound(game, request.pick());

		var response = new SuccessResponse<>(new TuplausRoundResponse(
			roundResponse.result(),
			roundResponse.card(),
			tuplausGameService.getCurrentPot(game).getFormattedAmount(),
			accountService.getAccountBalance(user).getFormattedAmount()
		));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/api/cashOut")
	public ResponseEntity<SuccessResponse<TuplausCashOutResponse>> round(@Valid @RequestBody TuplausCashOutRequest request) {
		var user = userRepository.findById(request.playerId()).orElseThrow(() -> new TuplausException("User not found"));
		var game = tuplausGameService.findGame(request.gameId(), user);

		tuplausGameService.cashOut(game);

		var response = new SuccessResponse<>(new TuplausCashOutResponse(
			accountService.getAccountBalance(user).getFormattedAmount()
		));

		return ResponseEntity.ok(response);
	}
}
