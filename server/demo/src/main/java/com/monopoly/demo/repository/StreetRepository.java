package com.monopoly.demo.repository;

import com.monopoly.demo.model.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StreetRepository extends JpaRepository<Street, Long> {

    List<Street> findByMatchId(Long matchId);

    List<Street> findByOwnerId(Long ownerId);

    Optional<Street> findByMatchIdAndIndex(Long matchId, Integer index);
}

