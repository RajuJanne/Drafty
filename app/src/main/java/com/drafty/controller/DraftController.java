package com.drafty.controller;

import org.springframework.web.bind.annotation.*;

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
    private final Map<String, DraftSeries> activeSessions = new ConcurrentHashMap<>();
    private final DraftSeriesRepository draftSeriesRepository;

    public DraftController(DraftSeriesRepository draftSeriesRepository) {
        this.draftSeriesRepository = draftSeriesRepository;
    }

    @PostMapping("/start")
    public DraftSeries startSeries(
            @RequestParam String blueName,
            @RequestParam String redName,
            @RequestParam(defaultValue = "3") Integer seriesLength) {

        String sessionId = UUID.randomUUID().toString().substring(0, 8);

        DraftSeries series = new DraftSeries();
        series.setId(sessionId);
        series.setTeamBlueName(blueName);
        series.setTeamRedName(redName);
        series.setSeriesLength(seriesLength);

        GameDraft game1 = new GameDraft();
        game1.setGameNumber(1);
        series.getGames().add(game1);

        DraftSeries savedSeries = draftSeriesRepository.save(series);
        activeSessions.put(savedSeries.getId(), savedSeries);

        return savedSeries;
    }

    @PostMapping("/{sessionId}/next-game")
    @Transactional
    public DraftSeries nextGame(@PathVariable String sessionId, @RequestParam String winnerSide) {
        DraftSeries series = draftSeriesRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if ("BLUE".equalsIgnoreCase(winnerSide)) {
            series.setBlueScore(series.getBlueScore() + 1);
        } else {
            series.setRedScore(series.getRedScore() + 1);
        }

        GameDraft lastGame = series.getGames().get(series.getGames().size() - 1);
        series.getBlueBurnedPicks().addAll(lastGame.getBluePicks());
        series.getRedBurnedPicks().addAll(lastGame.getRedPicks());

        int winsNeeded = (series.getSeriesLength() / 2) + 1;
        if (series.getBlueScore() >= winsNeeded || series.getRedScore() >= winsNeeded) {
            DraftSeries finalSaved = draftSeriesRepository.save(series);
            activeSessions.put(sessionId, finalSaved);
            return finalSaved;
        }

        GameDraft nextGame = new GameDraft();
        nextGame.setGameNumber(series.getGames().size() + 1);
        series.getGames().add(nextGame);

        DraftSeries updated = draftSeriesRepository.save(series);
        activeSessions.put(sessionId, updated);
        return updated;
    }

    @GetMapping("/{sessionId}")
    public DraftSeries getStatus(@PathVariable String sessionId) {
        return draftSeriesRepository.findById(sessionId)
                .orElseGet(() -> activeSessions.get(sessionId));
    }
}