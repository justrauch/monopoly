package com.monopoly.demo.controller;

import com.monopoly.demo.model.User;
import com.monopoly.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    // --- ALLE NUTZER ABRUFEN ---
    @GetMapping
    public List<User> getUsers() {
        return repository.findAll();
    }

    // --- SIGNUP ---
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User newUser) {
        if (repository.findByName(newUser.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nutzername bereits vergeben!");
        }

        // Passwort hashen
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        repository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("Nutzer erfolgreich registriert!");
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser, HttpSession session) {
        User user = repository.findByName(loginUser.getName());
        if (user != null && passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            session.setAttribute("userId", user.getId());
            return ResponseEntity.ok("Login erfolgreich!");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login fehlgeschlagen!");
    }

    // --- LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout erfolgreich!");
    }
}
