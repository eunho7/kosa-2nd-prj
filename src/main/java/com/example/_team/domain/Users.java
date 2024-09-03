package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Authority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ_GENERATOR")
    private Integer userIdx;

    @Column(length = 50, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(length = 50, nullable = false)
    private String phone;
    @Column(length = 50, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;
}
