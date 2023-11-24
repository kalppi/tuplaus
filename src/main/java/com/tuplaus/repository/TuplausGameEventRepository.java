package com.tuplaus.repository;

import com.tuplaus.entity.TuplausGameEvent;
import com.tuplaus.entity.TuplausGame;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface TuplausGameEventRepository extends CrudRepository<TuplausGameEvent, Long> {
    List<TuplausGameEvent> findByGame(TuplausGame game);

    @Query("SELECT ge FROM TuplausGameEvent ge WHERE ge.game = :game ORDER BY ge.timestamp DESC LIMIT 1")
    TuplausGameEvent findLatestByGame(@Param("game") TuplausGame game);
}
