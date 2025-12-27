package com.drafty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Entity
@Data
public class DraftSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamBlueName;
    private String teamRedName;

    @ElementCollection
    private List<String> blueBurnedPicks = new ArrayList<>();

    @ElementCollection 
    private List<String> redBurnedPicks = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) 
    @JoinColumn(name = "series_id")
    private List<GameDraft> games = new ArrayList<>();
}