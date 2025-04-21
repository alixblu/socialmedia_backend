package com.example.backend.controller;

import com.example.backend.model.Report;
import com.example.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return reportRepository.save(report);
    }
}