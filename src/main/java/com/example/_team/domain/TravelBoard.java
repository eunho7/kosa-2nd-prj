package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelBoard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAVEL_BOARD_SEQ_GENERATOR")
    private Integer travelIdx;
    @Column(length = 100, nullable = false)
    private String title;
    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Region region;
    @Column(nullable = false)
    private LocalDateTime statDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    @Column(nullable = false)
    private Integer isPublic;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
}
