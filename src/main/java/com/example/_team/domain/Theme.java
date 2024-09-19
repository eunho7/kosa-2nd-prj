package com.example._team.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "THEME_SEQ_GENERATOR")
    private Integer themeIdx;

    @Column(length = 30, nullable = false)
    private String name;
    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name="travel_id")
    private TravelBoard travelIdx;

}
