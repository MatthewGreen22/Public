package com.smartspeak.english_app.controllers;

import com.smartspeak.english_app.dto.responses.TrainingResponse;
import com.smartspeak.english_app.training.TrainingTest;
import com.smartspeak.english_app.training.TrainingTestRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class TrainingTestController {

    private final TrainingTestRepository trainingTestRepository;

    @PostMapping("/createTraining")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingTest createTraining(@RequestBody TrainingTest trainingTest) {
        return trainingTestRepository.save(trainingTest);
    }

    @GetMapping("/getTraining")
    public ResponseEntity<TrainingResponse> getTraining(@RequestParam int numberOfTraining) {
        List<TrainingTest> trainings = trainingTestRepository.findByNumberOfTraining(numberOfTraining);
        List<Integer> availableTrainingNumbers = trainingTestRepository.findAllDistinctNumberOfTrainings();
        TrainingResponse response = new TrainingResponse(trainings, availableTrainingNumbers);
        return ResponseEntity.ok(response);
    }
}
