package com.drafty.controller;

import org.springframework.web.bind.annotation.*;

import com.drafty.entity.DraftAction;
import com.drafty.entity.DraftSeries;
import com.drafty.entity.GameDraft;
import com.drafty.repository.DraftSeriesRepository;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/draft")
@CrossOrigin(origins = "http://localhost:5173")
public class DraftController {
    // In-memory storage for active sessions (keyed by a session ID)
    private final Map<String, DraftSeries> activeSessions = new ConcurrentHashMap<>();
    private final DraftSeriesRepository draftSeriesRepository;

    public DraftController(DraftSeriesRepository draftSeriesRepository) {
        this.draftSeriesRepository = draftSeriesRepository;
    }

    @PostMapping("/start")
    public DraftSeries startSeries(@RequestParam String blueName, @RequestParam String redName) {
        // Generate a unique ID for this draft session
        String sessionId = UUID.randomUUID().toString().substring(0, 8); 
        
        DraftSeries series = new DraftSeries();
        series.setId(null); // Let JPA handle DB ID, or use the string below
        // Add a field to your DraftSeries entity called 'uuid' or just use the DB Long ID
        // For now, let's use the DB ID for simplicity:
        
        series.setTeamBlueName(blueName);
        series.setTeamRedName(redName);
        
        GameDraft game1 = new GameDraft();
        game1.setGameNumber(1);
        series.getGames().add(game1);
        
        // Save to DB so it has an ID
        DraftSeries savedSeries = draftSeriesRepository.save(series);
        
        // Put in our active map for quick access
        activeSessions.put(savedSeries.getId().toString(), savedSeries);
        
        return savedSeries;
    }

    @PostMapping("/{sessionId}/action")
    @Transactional
    public DraftSeries handleAction(@PathVariable String sessionId, @RequestBody DraftAction action) {
        DraftSeries series = draftSeriesRepository.findById(Long.parseLong(sessionId))
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (series.getGames().isEmpty()) {
            throw new IllegalStateException("No games found in this series");
        }
        GameDraft currentItem = series.getGames().get(series.getGames().size() - 1);

        if (isChampionAlreadySelected(currentItem, action.championId())) {
            return series; 
        }

        if (series.getBlueBurnedPicks().contains(action.championId()) || 
            series.getRedBurnedPicks().contains(action.championId())) {
            throw new IllegalStateException("Champion is Fearless-locked");
        }

        if ("BAN".equalsIgnoreCase(action.actionType())) {
            if ("BLUE".equalsIgnoreCase(action.team())) currentItem.getBlueBans().add(action.championId());
            else currentItem.getRedBans().add(action.championId());
        } else if ("PICK".equalsIgnoreCase(action.actionType())) {
            if ("BLUE".equalsIgnoreCase(action.team())) currentItem.getBluePicks().add(action.championId());
            else currentItem.getRedPicks().add(action.championId());
        }

        DraftSeries updated = draftSeriesRepository.save(series);
        activeSessions.put(sessionId, updated);
        
        return updated;
    }

    // Helper method to verify uniqueness within the current game
    private boolean isChampionAlreadySelected(GameDraft game, String championId) {
        return game.getBlueBans().contains(championId) ||
            game.getRedBans().contains(championId) ||
            game.getBluePicks().contains(championId) ||
            game.getRedPicks().contains(championId);
    }

    @PostMapping("/{sessionId}/next-game")
    public DraftSeries advanceToNextGame(@PathVariable String sessionId) {
        DraftSeries series = activeSessions.get(sessionId);
        GameDraft lastGame = series.getGames().get(series.getGames().size() - 1);
        
        series.getBlueBurnedPicks().addAll(lastGame.getBluePicks());
        series.getRedBurnedPicks().addAll(lastGame.getRedPicks());
        
        // Start new game
        GameDraft nextGame = new GameDraft();
        nextGame.setGameNumber(series.getGames().size() + 1);
        series.getGames().add(nextGame);
        
        return draftSeriesRepository.save(series);
    }

    @GetMapping("/{sessionId}")
    public DraftSeries getStatus(@PathVariable String sessionId) {
        return activeSessions.get(sessionId);
    }

    @DeleteMapping("/clear-all")
    public void clearDatabase() {
        draftSeriesRepository.deleteAll();
        activeSessions.clear();
    }
}