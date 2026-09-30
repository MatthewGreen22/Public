package com.smartspeak.english_app.examtest;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String exercise;

    private int numberOfExam;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    private String audioUrl;

    private String answers;

    private String imageUrl;
}
