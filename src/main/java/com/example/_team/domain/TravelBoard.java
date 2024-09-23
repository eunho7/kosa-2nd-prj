package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Region;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelBoard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAVEL_BOARD_SEQ_GENERATOR")
    private Integer id;
    @Column(length = 100, nullable = true)
    private String title;
    @Lob
    @Column(nullable = true)
    private String content;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private Region region;
    @Column(nullable = true)
    private LocalDate statDate;
    @Column(nullable = true)
    private LocalDate endDate;
    @Column(nullable = true)
    private Integer isPublic;
    @Column(nullable = true)
    private String thumbnail;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnore
    @JoinColumn(name="user_idx")
    private Users userIdx;
    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private List<Theme> themes = new ArrayList<>();
    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @JsonManagedReference
    private List<TravelImages> imagesList = new ArrayList<>();
    @Column(nullable = true)
    private int likeCount;

    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TravelLikes> likes = new ArrayList<>();
    public void addLike() {
        this.likeCount++;
    }

    public void removeLike() {
        this.likeCount--;
    }
}
