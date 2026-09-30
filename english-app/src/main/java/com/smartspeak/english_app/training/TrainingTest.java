package com.smartspeak.english_app.training;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String exercise;

    private int numberOfTraining;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    private String audioUrl;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String exampleTextAnswer;

    private String imageUrl;
}
