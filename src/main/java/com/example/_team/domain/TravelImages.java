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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelImages {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAVEL_IMAGES_SEQ_GENERATOR")
    private Integer imageIdx;
    @Column(length = 1000, nullable = false)
    private String imagePath;
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    @ManyToOne
    @JoinColumn(name = "travel_idx")
    private TravelBoard travelIdx;
}
