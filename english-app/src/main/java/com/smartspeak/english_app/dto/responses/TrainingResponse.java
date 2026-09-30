package com.smartspeak.english_app.dto.responses;

import com.smartspeak.english_app.training.TrainingTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponse {

    private List<TrainingTest> trainings;
    private List<Integer> availableTrainingNumbers;
}