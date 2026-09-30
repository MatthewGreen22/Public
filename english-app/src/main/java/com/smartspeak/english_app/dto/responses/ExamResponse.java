package com.smartspeak.english_app.dto.responses;

import com.smartspeak.english_app.examtest.ExamTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {

    private List<ExamTest> exams;
    private List<Integer> availableExamNumbers;
}
