package com.example._team.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BORAD_FILES_SEQ_GENERATOR")
    private Integer fileIdx;
    @Column(length = 1000, nullable = false)
    private String filepath;
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    @ManyToOne
    @JoinColumn(name = "board_idx")
    private Board board;
}
