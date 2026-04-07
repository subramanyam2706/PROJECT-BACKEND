package com.pathfinder.config;

import com.pathfinder.model.*;
import com.pathfinder.repository.AssessmentRepository;
import com.pathfinder.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, AssessmentRepository assessmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        loadUsers();
        loadAssessments();
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@career.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            User student = User.builder()
                    .name("Student J")
                    .email("student@career.com")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .build();

            userRepository.save(admin);
            userRepository.save(student);
            log.info("✅ Sample users created (admin@career.com / admin123, student@career.com / student123)");
        }
    }

    private void loadAssessments() {
        if (assessmentRepository.count() == 0) {
            // Assessment 1: Personality Matrix
            Assessment personality = Assessment.builder()
                    .title("Personality Matrix")
                    .description("Discover your core personality traits and how they align with workplace dynamics.")
                    .build();

            personality.addQuestion(Question.builder().text("I enjoy working in large, fast-paced teams.").type("likert").build());
            personality.addQuestion(Question.builder().text("I prefer logical analysis over emotional reasoning.").type("likert").build());
            personality.addQuestion(Question.builder().text("I find creative problem solving more fulfilling than repetitive tasks.").type("likert").build());
            personality.addQuestion(Question.builder().text("I enjoy taking leadership roles in group projects.").type("likert").build());
            personality.addQuestion(Question.builder().text("I prefer working independently rather than in a group.").type("likert").build());

            assessmentRepository.save(personality);

            // Assessment 2: Technical Aptitude
            Assessment technical = Assessment.builder()
                    .title("Technical Aptitude")
                    .description("Evaluate your strengths in logic, mathematics, and systematic thinking.")
                    .build();

            technical.addQuestion(Question.builder().text("I can easily spot patterns in complex data.").type("likert").build());
            technical.addQuestion(Question.builder().text("I enjoy building and assembling mechanical or digital systems.").type("likert").build());
            technical.addQuestion(Question.builder().text("I am comfortable learning new software or tools quickly.").type("likert").build());
            technical.addQuestion(Question.builder().text("I enjoy solving mathematical puzzles and brain teasers.").type("likert").build());
            technical.addQuestion(Question.builder().text("I can debug and troubleshoot technical problems efficiently.").type("likert").build());

            assessmentRepository.save(technical);

            // Assessment 3: Creative Thinking
            Assessment creative = Assessment.builder()
                    .title("Creative Thinking")
                    .description("Measure your creative abilities, design thinking, and innovation potential.")
                    .build();

            creative.addQuestion(Question.builder().text("I often come up with unconventional solutions to problems.").type("likert").build());
            creative.addQuestion(Question.builder().text("I enjoy expressing ideas through art, writing, or design.").type("likert").build());
            creative.addQuestion(Question.builder().text("I notice aesthetic details that others often miss.").type("likert").build());
            creative.addQuestion(Question.builder().text("I like brainstorming and generating new ideas.").type("likert").build());
            creative.addQuestion(Question.builder().text("I enjoy exploring different perspectives on a topic.").type("likert").build());

            assessmentRepository.save(creative);

            log.info("✅ Sample assessments created (Personality Matrix, Technical Aptitude, Creative Thinking)");
        }
    }
}
