package com.tuplaus.repository;

import com.tuplaus.entity.AccountTransaction;
import com.tuplaus.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionRepository extends CrudRepository<AccountTransaction, Long> {
    List<AccountTransaction> findByUser(User user);
}
