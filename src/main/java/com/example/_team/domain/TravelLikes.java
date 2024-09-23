package com.example._team.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class TravelLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAVEL_LIKES_SEQ_GENERATOR")
    private Integer likeIdx;
    @ManyToOne
    @JoinColumn(name = "travel_idx")
    @JsonBackReference
    private TravelBoard travelIdx;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
}
