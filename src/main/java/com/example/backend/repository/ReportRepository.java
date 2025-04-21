package com.example.backend.repository;


import com.example.backend.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    // Find reports for a specific post
    List<Report> findByPostId(Integer postId);

    // Find reports by status
    List<Report> findByStatus(String status);
}