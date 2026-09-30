package com.smartspeak.english_app.training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainingTestRepository extends JpaRepository<TrainingTest, Integer> {

    List<TrainingTest> findByNumberOfTraining(int numberOfTraining);

    @Query("SELECT DISTINCT t.numberOfTraining FROM TrainingTest t")
    List<Integer> findAllDistinctNumberOfTrainings();
}
