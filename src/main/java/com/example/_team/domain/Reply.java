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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_SEQ_GENERATOR")
    private Integer replyIdx;

    @Column(length = 2000, nullable = false)
    private String content;
    @ManyToOne
    @JoinColumn(name="user_idx")
    private Users userIdx;
//    @ManyToOne
//    @JoinColumn(name="board_idx")
//    private Board boardIdx;
    @ManyToOne
    @JoinColumn(name="board_idx")
    private Board board;

}
