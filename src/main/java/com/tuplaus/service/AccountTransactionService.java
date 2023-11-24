package com.tuplaus.service;

import com.tuplaus.component.Money;
import com.tuplaus.entity.AccountTransaction;
import com.tuplaus.entity.User;
import com.tuplaus.repository.AccountTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountTransactionService {
    private final AccountTransactionRepository accountTransactionRepository;

    @Autowired
    public AccountTransactionService(AccountTransactionRepository accountTransactionRepository) {
        this.accountTransactionRepository = accountTransactionRepository;
    }

    public AccountTransaction addTransaction(User user, Money change, LocalDateTime timestamp) {
        AccountTransaction accountTransaction = AccountTransaction.builder()
                .user(user)
                .change(change)
                .timestamp(timestamp)
                .build();

        accountTransactionRepository.save(accountTransaction);

        return accountTransaction;
    }

    public AccountTransaction addTransaction(User user, Money money) {
        return this.addTransaction(user, money, LocalDateTime.now());
    }

    public List<AccountTransaction> findByUser(User user) {
        return accountTransactionRepository.findByUser(user);
    }
}
