package com.example._team.domain;

import com.example._team.domain.common.BaseEntity;
import com.example._team.domain.enums.Authority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Builder
public class Users extends BaseEntity implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ_GENERATOR")
    private Long userIdx;

    @Column(length = 50, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(length = 50, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private Integer status;
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    // 비밀번호 재설정 또는 이메일 인증용 토큰
    @Column(name = "reset_token", length = 256)
    private String resetToken;

    // 토큰 만료 시간
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 관련 로직을 여기서 구현하거나, 필요시 Authority와 연결된 GrantedAuthority 리스트를 반환합니다.
        return List.of(() -> authority.name());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }
}
