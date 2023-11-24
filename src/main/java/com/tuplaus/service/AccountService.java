package com.tuplaus.service;

import com.tuplaus.component.Money;
import com.tuplaus.entity.AccountTransaction;
import com.tuplaus.entity.User;
import com.tuplaus.exception.MoneyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountTransactionService accountTransactionService;

    @Autowired
    public AccountService(AccountTransactionService accountTransactionService) {
        this.accountTransactionService = accountTransactionService;
    }

    public Money getAccountBalance(User user) {
        var transactions = accountTransactionService.findByUser(user);

        return transactions.stream()
            .map(AccountTransaction::getChange)
            .reduce(new Money("0"), Money::add);
    }

    public AccountTransaction deposit(User user, String amount) {
        var money = new Money(amount);

        if (money.isNegative()) {
            throw new MoneyException("Can't deposit negative money");
        }

        return accountTransactionService.addTransaction(user, money);
    }

    public AccountTransaction withdraw(User user, String amount) {
        Money money = new Money(amount);

        if (money.isNegative()) {
            throw new MoneyException("Can't withdraw negative money");
        }

        Money balance = this.getAccountBalance(user);
        Money newBalance = balance.subtract(money);

        if (newBalance.isNegative()) {
            throw new MoneyException("account negative");
        }

        return accountTransactionService.addTransaction(user, money.negate());
    }
}
