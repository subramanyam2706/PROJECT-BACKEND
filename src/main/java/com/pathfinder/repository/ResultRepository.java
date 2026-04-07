package com.pathfinder.repository;

import com.pathfinder.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserId(Long userId);
    long countByAssessmentId(Long assessmentId);
}
