package com.example.backend.controller;

import com.example.backend.model.Report;
import com.example.backend.model.ReportStatus;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @GetMapping("/post/{postId}")
    public List<Report> getReportsByPost(@PathVariable Integer postId) {
        return reportRepository.findByPostId(postId);
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus(ReportStatus.PENDING);
        return reportRepository.save(report);
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<?> reviewReport(@PathVariable Integer id) {
        try {
            Report report = reportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found"));
            report.setStatus(ReportStatus.REVIEWED);
            Report updatedReport = reportRepository.save(report);
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating report: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable Integer id) {
        try {
            Report report = reportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found"));
            report.setStatus(ReportStatus.RESOLVED);
            Report updatedReport = reportRepository.save(report);
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating report: " + e.getMessage());
        }
    }
}