package com.drafty.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Entity
@Data
public class GameDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<String> blueBans = new ArrayList<>();
    @ElementCollection
    private List<String> redBans = new ArrayList<>();

    @ElementCollection
    private List<String> bluePicks = new ArrayList<>();
    @ElementCollection
    private List<String> redPicks = new ArrayList<>();
    
    private Integer gameNumber; // 1, 2, 3...
}