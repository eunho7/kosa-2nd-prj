package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Region;
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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelBoard extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRAVEL_BOARD_SEQ_GENERATOR")
    private Integer id;
    @Column(length = 100, nullable = false)
    private String title;
    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Region region;
    @Column(nullable = false)
    private LocalDate statDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    private Integer isPublic;
    @Column(nullable = false)
    private String thumbnail;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Theme> themes = new ArrayList<>();
    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TravelImages> imagesList = new ArrayList<>();
    @Column(nullable = false)
    private int likeCount;

    @OneToMany(mappedBy = "travelIdx", cascade = CascadeType.ALL)
    private List<TravelLikes> likes = new ArrayList<>();
    public void addLike() {
        this.likeCount++;
    }

    public void removeLike() {
        this.likeCount--;
    }
}
