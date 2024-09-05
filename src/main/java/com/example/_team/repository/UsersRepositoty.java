package com.example._team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example._team.domain.Users;

public interface UsersRepositoty extends JpaRepository<Users, Integer> {

}
