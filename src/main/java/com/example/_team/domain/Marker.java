package com.example._team.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marker_seq")
    @SequenceGenerator(name = "marker_seq", sequenceName = "marker_seq", allocationSize = 1)
    private Long id;

    private String placeName;
    private String address;
    private double latitude;
    private double longitude;
    private int markerNumber;
    @ManyToOne
    @JoinColumn(name = "travel_idx")
    private TravelBoard travelBoard;
}
