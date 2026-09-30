package com.smartspeak.english_app.examtest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamTestRepository extends JpaRepository<ExamTest, Integer> {

    List<ExamTest> findByNumberOfExam(int numberOfExam);

    @Query("SELECT DISTINCT e.numberOfExam FROM ExamTest e")
    List<Integer> findAllDistinctNumberOfExams();
}
