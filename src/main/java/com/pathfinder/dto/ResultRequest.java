package com.pathfinder.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ResultRequest {

    @NotNull(message = "Assessment ID is required")
    private Long assessmentId;

    @NotNull(message = "Answers are required")
    private Map<Long, Integer> answers; // questionId -> answer (1-5)
}
