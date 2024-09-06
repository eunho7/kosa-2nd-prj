package com.example._team.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitedLocations {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "V_LOC_SEQ_GENERATOR")

    private Integer locationIdx;
    @ManyToOne
    @JoinColumn(name = "travel_idx")
    private TravelBoard travelIdx;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 1000, nullable = false)
    private String address;
}
