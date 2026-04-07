package com.pathfinder.service;

import com.pathfinder.dto.ResultRequest;
import com.pathfinder.dto.ResultResponse;
import com.pathfinder.model.Assessment;
import com.pathfinder.model.Result;
import com.pathfinder.model.User;
import com.pathfinder.repository.AssessmentRepository;
import com.pathfinder.repository.ResultRepository;
import com.pathfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;

    public ResultService(ResultRepository resultRepository, UserRepository userRepository, AssessmentRepository assessmentRepository) {
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
    }

    // ─── Career recommendation engine ───────────────────────────────────
    // Maps average score ranges to career profiles.
    // This is a simplified but functional recommendation algorithm.

    private static final List<CareerProfile> CAREER_PROFILES = List.of(
        new CareerProfile("Software Architect",
            "Design and oversee complex digital ecosystems.",
            "$120k - $200k", List.of("logical", "technical", "creative"), 4.0),
        new CareerProfile("UX Designer",
            "Create intuitive and beautiful digital experiences.",
            "$80k - $150k", List.of("creative", "empathetic", "technical"), 3.5),
        new CareerProfile("Data Scientist",
            "Extract meaningful insights from vast amounts of information.",
            "$100k - $180k", List.of("analytical", "mathematical", "technical"), 3.8),
        new CareerProfile("Project Manager",
            "Lead cross-functional teams to deliver complex projects on time and within budget.",
            "$90k - $160k", List.of("leadership", "organizational", "communication"), 3.2),
        new CareerProfile("Cybersecurity Analyst",
            "Protect organizations from digital threats and vulnerabilities.",
            "$95k - $170k", List.of("analytical", "detail-oriented", "technical"), 4.2),
        new CareerProfile("AI/ML Engineer",
            "Build intelligent systems that learn and adapt from data.",
            "$130k - $220k", List.of("mathematical", "technical", "innovative"), 4.5),
        new CareerProfile("Product Designer",
            "Shape the strategy and vision of digital products from concept to launch.",
            "$85k - $155k", List.of("creative", "strategic", "empathetic"), 2.8),
        new CareerProfile("DevOps Engineer",
            "Bridge development and operations to enable rapid, reliable software delivery.",
            "$105k - $185k", List.of("technical", "systematic", "collaborative"), 3.6),
        new CareerProfile("Business Analyst",
            "Translate business needs into technical solutions and data-driven insights.",
            "$75k - $140k", List.of("analytical", "communication", "strategic"), 2.5)
    );

    @Transactional
    public List<ResultResponse> submitResults(Long userId, ResultRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // Calculate average score from answers
        Map<Long, Integer> answers = request.getAnswers();
        double avgScore = answers.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(3.0);

        // Pick top 3 career matches based on score proximity
        List<CareerProfile> matches = CAREER_PROFILES.stream()
                .sorted((a, b) -> Double.compare(
                        Math.abs(a.targetScore - avgScore),
                        Math.abs(b.targetScore - avgScore)))
                .limit(3)
                .collect(Collectors.toList());

        // Save each match as a Result entity
        List<Result> savedResults = matches.stream().map(career -> {
            double matchScore = Math.max(0, 100 - Math.abs(career.targetScore - avgScore) * 20);
            matchScore = Math.round(matchScore * 10.0) / 10.0;

            Result result = Result.builder()
                    .user(user)
                    .assessment(assessment)
                    .career(career.title)
                    .careerDescription(career.description)
                    .salary(career.salary)
                    .score(matchScore)
                    .alignment(String.join(",", career.alignment))
                    .completedAt(LocalDateTime.now())
                    .build();
            return resultRepository.save(result);
        }).collect(Collectors.toList());

        return savedResults.stream().map(r -> toDTO(r, assessment.getTitle())).collect(Collectors.toList());
    }

    public List<ResultResponse> getResultsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        return resultRepository.findByUserId(userId).stream()
                .map(r -> toDTO(r, r.getAssessment().getTitle()))
                .collect(Collectors.toList());
    }

    private ResultResponse toDTO(Result result, String assessmentTitle) {
        return ResultResponse.builder()
                .id(result.getId())
                .career(result.getCareer())
                .careerDescription(result.getCareerDescription())
                .salary(result.getSalary())
                .score(result.getScore())
                .alignment(Arrays.asList(result.getAlignment().split(",")))
                .assessmentTitle(assessmentTitle)
                .completedAt(result.getCompletedAt())
                .build();
    }

    // ─── Inner helper ───────────────────────────────────────────────────
    private record CareerProfile(String title, String description, String salary,
                                  List<String> alignment, double targetScore) {}
}
