package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BORAD_SEQ_GENERATOR")
    private Integer boardIdx;
    @Column(length = 100, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String content;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false)
    private Integer views;
    @Column(nullable = false)
    private Integer status;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
    @ManyToOne
    @JoinColumn(name = "answer_board_idx")
    private Board answerBoardIdx;
}
