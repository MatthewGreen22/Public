package com.smartspeak.english_app.dataAssets.grammar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrammarTheory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String exercise;

    private String link;
}
/*
    --default-character-set=utf8mb4
 */