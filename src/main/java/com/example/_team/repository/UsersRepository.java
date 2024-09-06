package com.example._team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example._team.domain.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {

}
