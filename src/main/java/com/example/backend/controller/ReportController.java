package com.example.backend.controller;

import com.example.backend.model.Report;
import com.example.backend.model.ReportStatus;
import com.example.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @GetMapping("/post/{postId}")
    public List<Report> getReportsByPost(@PathVariable Integer postId) {
        return reportRepository.findByPostId(postId);
    }

    @PostMapping("/create")
    public Report createReport(@RequestBody Report report) {
        if (report.getReason() == null) {
            throw new IllegalArgumentException("Reason must be provided");
        }
        
        // Set the current time as the creation time
        report.setCreatedAt(LocalDateTime.now());

        // Set the status of the report to PENDING initially
        report.setStatus(ReportStatus.PENDING);
        
        // Save the report into the database
        return reportRepository.save(report);
    }
}
