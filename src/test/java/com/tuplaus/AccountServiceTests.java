package com.tuplaus;

import com.tuplaus.entity.AccountTransaction;
import com.tuplaus.entity.User;
import com.tuplaus.exception.MoneyException;
import com.tuplaus.repository.UserRepository;
import com.tuplaus.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTests {
	@Autowired
	private AccountService service;
	@Autowired
	private UserRepository userRepository;
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder().name("Raimo Ruletti").build();
		userRepository.save(user);
	}

	@Test
	void moneyCanBeDepositedToAccount() {
		AccountTransaction transaction = service.deposit(user, "100.00");

		assertEquals("100.0000", service.getAccountBalance(user).getAmountAsString());
		assertEquals("100.0000", transaction.getChange().getAmountAsString());
	}

	@Test
	void moneyCanBeWithdrawnFromAccount() {
		service.deposit(user, "1000.00");
		AccountTransaction transaction = service.withdraw(user, "100.00");

		assertEquals("900.0000", service.getAccountBalance(user).getAmountAsString());
		assertEquals("-100.0000", transaction.getChange().getAmountAsString());
	}

	@Test
	void accountBalanceCantGoNegative() {
		Exception exception = assertThrows(MoneyException.class, () -> service.withdraw(user, "1.00"));

		assertTrue(exception.getMessage().contains("negative"));
	}

	@Test
	void getAccountBalanceReturnsCorrectValue() {
		service.deposit(user, "500.50");
		service.withdraw(user, "23.00");
		service.deposit(user, "123.00");

		assertEquals("600.5000", service.getAccountBalance(user).getAmountAsString());
	}

	@Test
	void getAccountBalanceReturnsInitiallyZero() {
		user = User.builder().name("Kirsi Kahiseva").build();
		userRepository.save(user);

		assertEquals("0.0000", service.getAccountBalance(user).getAmountAsString());
	}
}
