package com.tuplaus.repository;

import com.tuplaus.entity.TuplausGame;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuplausGameRepository extends CrudRepository<TuplausGame, Long> {

}
