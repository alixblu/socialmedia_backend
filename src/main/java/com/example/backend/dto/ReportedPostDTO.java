package com.example.backend.dto;

import com.example.backend.model.Report;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportedPostDTO {
    private Integer id;
    private String content;
    private List<String> mediaUrls;
    private String username;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private boolean isHidden;
    private List<ReportDTO> reports;

    @Data
    public static class ReportDTO {
        private Integer id;
        private String username;
        private String reason;
        private String status;
        private LocalDateTime createdAt;
    }
} 