package com.pathfinder.controller;

import com.pathfinder.dto.ResultRequest;
import com.pathfinder.dto.ResultResponse;
import com.pathfinder.security.JwtUtil;
import com.pathfinder.service.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;
    private final JwtUtil jwtUtil;

    public ResultController(ResultService resultService, JwtUtil jwtUtil) {
        this.resultService = resultService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<List<ResultResponse>> submitResults(
            @Valid @RequestBody ResultRequest request,
            HttpServletRequest httpRequest) {

        Long userId = extractUserId(httpRequest);
        List<ResultResponse> results = resultService.submitResults(userId, request);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ResultResponse>> getResultsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getResultsByUserId(userId));
    }

    private Long extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("Missing authorization token");
    }
}
