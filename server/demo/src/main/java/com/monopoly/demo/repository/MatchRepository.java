package com.monopoly.demo.repository;

import com.monopoly.demo.model.Match;
import com.monopoly.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByIsActive(Integer isActive);
    List<Match> findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
        User creater,
        User secondplayer,
        User thirdplayer,
        User fourthplayer
    );
}
