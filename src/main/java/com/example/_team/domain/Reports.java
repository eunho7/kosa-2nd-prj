package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reports extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORTS_SEQ_GENERATOR")
    private Integer reportsIdx;
    @Column(length = 1000, nullable = false)
    private String content;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
}