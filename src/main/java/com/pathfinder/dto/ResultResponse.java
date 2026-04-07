package com.pathfinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Long id;
    private String career;
    private String careerDescription;
    private String salary;
    private Double score;
    private List<String> alignment;
    private String assessmentTitle;
    private LocalDateTime completedAt;
}
