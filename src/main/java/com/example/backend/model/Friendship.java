package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Friendship")
@Getter
@Setter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id1", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", nullable = false)
    private User user2;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    private LocalDateTime createdAt;
}
