package com.drafty.controller;

import com.drafty.entity.DraftAction;
import com.drafty.entity.DraftSeries;
import com.drafty.service.DraftService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/draft")
@CrossOrigin(origins = "http://localhost:5173")
public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    @PostMapping("/start")
    public DraftSeries startSeries(
            @RequestParam String blueName,
            @RequestParam String redName,
            @RequestParam(defaultValue = "3") Integer seriesLength) {
        
        return draftService.startSeries(blueName, redName, seriesLength);
    }

    @PostMapping("/{sessionId}/action")
    public DraftSeries handleAction(
            @PathVariable String sessionId, 
            @RequestBody DraftAction action) {
        
        return draftService.handleAction(sessionId, action);
    }

    @PostMapping("/{sessionId}/next-game")
    public DraftSeries nextGame(
            @PathVariable String sessionId, 
            @RequestParam String winnerSide) {
        
        return draftService.advanceToNextGame(sessionId, winnerSide);
    }

    @GetMapping("/{sessionId}")
    public DraftSeries getStatus(@PathVariable String sessionId) {
        return draftService.getSeriesStatus(sessionId);
    }
}