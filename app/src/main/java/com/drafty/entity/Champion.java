package com.drafty.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Champion {

    @Id
    private String id;        // e.g., "Aatrox" (Technical ID)
    private String name;      // e.g., "Aatrox" (Display Name)
    private String title;     // e.g., "the Darkin Blade"
    // private List<String> tags;
    private String imageFull; // e.g., "Aatrox.png"
    
    // Optional: useful for filtering later
    // @ElementCollection 
    // private List<String> tags; 
}