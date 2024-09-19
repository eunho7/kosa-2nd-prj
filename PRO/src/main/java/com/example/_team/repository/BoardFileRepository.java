package com.example._team.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example._team.domain.BoardFiles;

public interface BoardFileRepository extends JpaRepository<BoardFiles, Integer> {

}
