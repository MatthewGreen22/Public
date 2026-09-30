package com.smartspeak.english_app.dataAssets.speaking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingTheory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String exercise;

    private String link;
}

