package com.pathfinder.controller;

import com.pathfinder.dto.AssessmentDTO;
import com.pathfinder.model.Role;
import com.pathfinder.repository.AssessmentRepository;
import com.pathfinder.repository.ResultRepository;
import com.pathfinder.repository.UserRepository;
import com.pathfinder.service.AssessmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AssessmentService assessmentService;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final ResultRepository resultRepository;

    public AdminController(AssessmentService assessmentService, UserRepository userRepository, AssessmentRepository assessmentRepository, ResultRepository resultRepository) {
        this.assessmentService = assessmentService;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.resultRepository = resultRepository;
    }

    // ─── Assessment CRUD ────────────────────────────────────────────────

    @PostMapping("/assessments")
    public ResponseEntity<AssessmentDTO> createAssessment(@Valid @RequestBody AssessmentDTO dto) {
        return ResponseEntity.ok(assessmentService.createAssessment(dto));
    }

    @PutMapping("/assessments/{id}")
    public ResponseEntity<AssessmentDTO> updateAssessment(@PathVariable Long id,
                                                          @Valid @RequestBody AssessmentDTO dto) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, dto));
    }

    @DeleteMapping("/assessments/{id}")
    public ResponseEntity<Map<String, String>> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Assessment deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ─── Platform Stats ─────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.countByRole(Role.STUDENT));
        stats.put("totalAssessments", assessmentRepository.count());
        stats.put("totalResults", resultRepository.count());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/assessments")
    public ResponseEntity<List<AssessmentDTO>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }
}
