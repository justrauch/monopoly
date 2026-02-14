package com.monopoly.demo.controller;

import com.monopoly.demo.model.Match;
import com.monopoly.demo.model.User;
import com.monopoly.demo.repository.MatchRepository;
import com.monopoly.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import java.util.Random;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:5173")
public class MatchController {

    private final MatchRepository repository;
    private final UserRepository urepository;

    public MatchController(MatchRepository repository, UserRepository urepository) {
        this.repository = repository;
        this.urepository = urepository;
    }

    // --- MATCH SUCHEN / BEITRETEN ---
    @PostMapping("/searchMatch")
    public ResponseEntity<?> searchMatch(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> listmatches = repository.findByIsActive(0);

        if (listmatches.isEmpty()) {
            Match newmatch = new Match();
            newmatch.setCreater(user);
            newmatch.setIsActive(0);
            user.setMoney(1);
            urepository.save(user);
            repository.save(newmatch);

            return ResponseEntity.ok("Match created");
        }

        Match firstMatch = listmatches.get(0);
        String message = "Match not full";

        if (firstMatch.getSecondplayer() == null) {
            firstMatch.setSecondplayer(user);
            user.setMoney(2);
        } else if (firstMatch.getThirdplayer() == null) {
            firstMatch.setThirdplayer(user);
            user.setMoney(3);
        } else if (firstMatch.getFourthplayer() == null) {
            firstMatch.setFourthplayer(user);
            firstMatch.setIsActive(1);
            user.setMoney(4);
            message = "Match full";
        }
        
        urepository.save(user);
        repository.save(firstMatch);
        return ResponseEntity.ok(message);
    }

    // --- GAMESTATE ABRUFEN ---
    @GetMapping("/getGamestate")
    public ResponseEntity<?> getGamestate(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.ok("In Search");
        }

        return ResponseEntity.ok(matches.get(0));
    }

    @GetMapping("/makeMove")
    public ResponseEntity<?> makeMove(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Optional<User> userOpt = urepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOpt.get();

        List<Match> matches = repository.findByCreaterOrSecondplayerOrThirdplayerOrFourthplayer(
                user, user, user, user
        );

        if (matches.isEmpty()) {
            return ResponseEntity.status(404).body("No active match found");
        }

        Match match = matches.get(0);

        Integer isActive = match.getIsActive();
        if (isActive == null) isActive = 0;
        match.setIsActive((isActive % 4) + 1);

        int randomInt = new Random().nextInt(12) + 1;


        int newPosition = (user.getPosition() + randomInt) % 40;
        user.setPosition(newPosition);

        urepository.save(user);
        repository.save(match);

        return ResponseEntity.ok(randomInt);
    }

}
