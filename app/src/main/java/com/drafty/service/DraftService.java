package com.drafty.service;

import com.drafty.entity.DraftAction;
import com.drafty.entity.DraftSeries;
import com.drafty.entity.GameDraft;
import com.drafty.repository.DraftSeriesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DraftService {

    private final DraftSeriesRepository draftSeriesRepository;
    // Keeping a cache for fast lookups, though the DB remains the source of truth
    private final Map<String, DraftSeries> activeSessions = new ConcurrentHashMap<>();

    public DraftService(DraftSeriesRepository draftSeriesRepository) {
        this.draftSeriesRepository = draftSeriesRepository;
    }

    @Transactional
    public DraftSeries startSeries(String blueName, String redName, Integer seriesLength) {
        String sessionId = UUID.randomUUID().toString();

        DraftSeries series = new DraftSeries();
        series.setId(sessionId);
        series.setTeamBlueName(blueName);
        series.setTeamRedName(redName);
        series.setSeriesLength(seriesLength);

        // Initialize the first game
        GameDraft game1 = new GameDraft();
        game1.setGameNumber(1);
        series.getGames().add(game1);

        DraftSeries savedSeries = draftSeriesRepository.save(series);
        activeSessions.put(savedSeries.getId(), savedSeries);

        return savedSeries;
    }

    @Transactional
    public DraftSeries handleAction(String sessionId, DraftAction action) {
        DraftSeries series = draftSeriesRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (series.getGames().isEmpty()) {
            throw new IllegalStateException("No games found in this series");
        }

        GameDraft currentGame = series.getGames().get(series.getGames().size() - 1);

        // 1. Validation: Is champion already picked/banned in CURRENT game?
        if (isChampionInCurrentGame(currentGame, action.championId())) {
            return series; 
        }

        // 2. Validation: Fearless Rule (Check burned picks from previous games)
        if (series.getBlueBurnedPicks().contains(action.championId()) || 
            series.getRedBurnedPicks().contains(action.championId())) {
            return series;
        }

        // 3. Logic: Apply Action
        if ("BAN".equalsIgnoreCase(action.actionType())) {
            if ("BLUE".equalsIgnoreCase(action.team())) currentGame.getBlueBans().add(action.championId());
            else currentGame.getRedBans().add(action.championId());
        } else if ("PICK".equalsIgnoreCase(action.actionType())) {
            if ("BLUE".equalsIgnoreCase(action.team())) currentGame.getBluePicks().add(action.championId());
            else currentGame.getRedPicks().add(action.championId());
        }

        DraftSeries saved = draftSeriesRepository.save(series);
        activeSessions.put(sessionId, saved);
        return saved;
    }

    @Transactional
    public DraftSeries advanceToNextGame(String sessionId, String winnerSide) {
        DraftSeries series = draftSeriesRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 1. Logic: Update Scores
        if ("BLUE".equalsIgnoreCase(winnerSide)) {
            series.setBlueScore(series.getBlueScore() + 1);
        } else {
            series.setRedScore(series.getRedScore() + 1);
        }

        // 2. Logic: Burn the picks (Fearless Rule)
        GameDraft lastGame = series.getGames().get(series.getGames().size() - 1);
        series.getBlueBurnedPicks().addAll(lastGame.getBluePicks());
        series.getRedBurnedPicks().addAll(lastGame.getRedPicks());

        // 3. Logic: Check if series is over
        int winsNeeded = (series.getSeriesLength() / 2) + 1;
        if (series.getBlueScore() >= winsNeeded || series.getRedScore() >= winsNeeded) {
            DraftSeries finalSaved = draftSeriesRepository.save(series);
            activeSessions.put(sessionId, finalSaved);
            return finalSaved;
        }

        // 4. Logic: Initialize next game if series continues
        GameDraft nextGame = new GameDraft();
        nextGame.setGameNumber(series.getGames().size() + 1);
        series.getGames().add(nextGame);

        DraftSeries updated = draftSeriesRepository.save(series);
        activeSessions.put(sessionId, updated);
        return updated;
    }

    public DraftSeries getSeriesStatus(String sessionId) {
        // Check cache first, then database
        if (activeSessions.containsKey(sessionId)) {
            return activeSessions.get(sessionId);
        }
        return draftSeriesRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    private boolean isChampionInCurrentGame(GameDraft game, String championId) {
        return game.getBlueBans().contains(championId) ||
                game.getRedBans().contains(championId) ||
                game.getBluePicks().contains(championId) ||
                game.getRedPicks().contains(championId);
    }
}