package com.example._team.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example._team.domain.BoardFiles;

public interface BoardFileRepository extends JpaRepository<BoardFiles, Integer> {
	Optional<BoardFiles> findByFilepath(String filepath);
}
