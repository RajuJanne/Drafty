package com.drafty.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drafty.entity.Champion;
import com.drafty.repository.ChampionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/champions")
@CrossOrigin(origins = "http://localhost:5173") // Crucial for Vue communication
public class ChampionController {

    private final ChampionRepository championRepository;

    public ChampionController(ChampionRepository championRepository) {
        this.championRepository = championRepository;
    }

    @GetMapping
    public List<Champion> getAllChampions() {
        // This returns the 172 champions imported by the DataDragonService
        return championRepository.findAll();
    }
}