package com.pathfinder.service;

import com.pathfinder.dto.AssessmentDTO;
import com.pathfinder.dto.QuestionDTO;
import com.pathfinder.model.Assessment;
import com.pathfinder.model.Question;
import com.pathfinder.repository.AssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public List<AssessmentDTO> getAllAssessments() {
        return assessmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AssessmentDTO getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));
        return toDTO(assessment);
    }

    @Transactional
    public AssessmentDTO createAssessment(AssessmentDTO dto) {
        Assessment assessment = Assessment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        if (dto.getQuestions() != null) {
            dto.getQuestions().forEach(qDto -> {
                Question question = Question.builder()
                        .text(qDto.getText())
                        .type(qDto.getType() != null ? qDto.getType() : "likert")
                        .build();
                assessment.addQuestion(question);
            });
        }

        Assessment saved = assessmentRepository.save(assessment);
        return toDTO(saved);
    }

    @Transactional
    public AssessmentDTO updateAssessment(Long id, AssessmentDTO dto) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));

        assessment.setTitle(dto.getTitle());
        assessment.setDescription(dto.getDescription());

        // Clear existing questions and re-add
        assessment.getQuestions().clear();

        if (dto.getQuestions() != null) {
            dto.getQuestions().forEach(qDto -> {
                Question question = Question.builder()
                        .text(qDto.getText())
                        .type(qDto.getType() != null ? qDto.getType() : "likert")
                        .build();
                assessment.addQuestion(question);
            });
        }

        Assessment saved = assessmentRepository.save(assessment);
        return toDTO(saved);
    }

    @Transactional
    public void deleteAssessment(Long id) {
        if (!assessmentRepository.existsById(id)) {
            throw new RuntimeException("Assessment not found with id: " + id);
        }
        assessmentRepository.deleteById(id);
    }

    private AssessmentDTO toDTO(Assessment assessment) {
        List<QuestionDTO> questionDTOs = assessment.getQuestions().stream()
                .map(q -> QuestionDTO.builder()
                        .id(q.getId())
                        .text(q.getText())
                        .type(q.getType())
                        .build())
                .collect(Collectors.toList());

        return AssessmentDTO.builder()
                .id(assessment.getId())
                .title(assessment.getTitle())
                .description(assessment.getDescription())
                .questions(questionDTOs)
                .build();
    }
}
