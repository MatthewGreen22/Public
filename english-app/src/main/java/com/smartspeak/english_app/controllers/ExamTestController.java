package com.smartspeak.english_app.controllers;

import com.smartspeak.english_app.dto.responses.ExamResponse;
import com.smartspeak.english_app.examtest.ExamTest;
import com.smartspeak.english_app.examtest.ExamTestRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class ExamTestController {

    private final ExamTestRepository examTestRepository;

    @PostMapping("/createExam")
    @ResponseStatus(HttpStatus.CREATED)
    public ExamTest createExam(@RequestBody ExamTest examTest) {
        return examTestRepository.save(examTest);
    }

    @GetMapping("/getExam")
    public ResponseEntity<ExamResponse> getExam(@RequestParam int numberOfExam) {
        List<ExamTest> exams = examTestRepository.findByNumberOfExam(numberOfExam);
        List<Integer> availableExamNumbers = examTestRepository.findAllDistinctNumberOfExams();
        ExamResponse response = new ExamResponse(exams, availableExamNumbers);
        return ResponseEntity.ok(response);
    }
}