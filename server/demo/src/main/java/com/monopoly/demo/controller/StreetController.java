package com.monopoly.demo.controller;

import com.monopoly.demo.model.Street;

import com.monopoly.demo.repository.StreetRepository;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/streets")
public class StreetController {

    private final StreetRepository streetRepository;

    public StreetController(StreetRepository streetRepository) {
        this.streetRepository = streetRepository;
    }

    @GetMapping("/{id}")
    public Street getStreet(@PathVariable Long id) {
        return streetRepository.findById(id).orElse(null);
    }

    @GetMapping("/match/{matchId}")
    public List<Street> getByMatch(@PathVariable Long matchId) {
        return streetRepository.findByMatchId(matchId);
    }
}
