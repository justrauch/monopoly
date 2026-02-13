package com.monopoly.demo.controller;

import com.monopoly.demo.model.Match;
import com.monopoly.demo.model.User;
import com.monopoly.demo.repository.MatchRepository;
import com.monopoly.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:5173")
public class MatchController {

    private final MatchRepository repository;
    private final UserRepository urepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MatchController(MatchRepository repository, UserRepository urepository) {
        this.repository = repository;
        this.urepository = urepository;
    }

    // --- LOGIN ---
    @PostMapping("/searchMatch")
    public ResponseEntity<String> login(@RequestBody User loginUser, HttpSession session) {
        User user = urepository.findByName(loginUser.getName());
        if (user != null && passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            session.setAttribute("userId", user.getId());
            return ResponseEntity.ok("Session abgelaufen!");
        }
        List<Match> listmatches = repository.findByIsActive(0);
        int anz = listmatches.size();

        String message = "";

        if (anz <= 0){
            Match newmatch = new Match();
            newmatch.setCreater(user.getId());
            newmatch.setIsActive(0);
            repository.save(newmatch);
            message = "Match created";
        }
        else {
            Match firstMatch = listmatches.get(0);

            if (firstMatch.getSecondplayer() != null){
                firstMatch.setSecondplayer(user.getId());
            }

            if (firstMatch.getThirdplayer() != null){
                firstMatch.setThirdplayer(user.getId());
            }

            message = "Match not full";

            if (firstMatch.getFourthplayer() != null){
                firstMatch.setFourthplayer(user.getId());
                firstMatch.setIsActive(1);
                message = "Match full";
            }

            repository.save(firstMatch);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
