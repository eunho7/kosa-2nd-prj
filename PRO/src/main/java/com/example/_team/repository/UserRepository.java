package com.example._team.repository;

import com.example._team.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByResetToken(String token);

    boolean existsByResetToken(String resetToken);

    @Query("SELECT u.email FROM Users u WHERE u.phone = :phone")
    String findEmailByPhone(String phone);
}
