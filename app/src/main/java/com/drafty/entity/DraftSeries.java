package com.drafty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Entity
@Data
public class DraftSeries {
    @Id
    private String id;

    private String teamBlueName;
    private String teamRedName;

    private Integer seriesLength; // 1, 3, or 5
    private Integer blueScore = 0;
    private Integer redScore = 0;

    @ElementCollection
    private List<String> blueBurnedPicks = new ArrayList<>();

    @ElementCollection 
    private List<String> redBurnedPicks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) 
    @JoinColumn(name = "series_id")
    private List<GameDraft> games = new ArrayList<>();
}