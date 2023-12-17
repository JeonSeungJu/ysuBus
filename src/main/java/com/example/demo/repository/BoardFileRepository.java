package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository  extends JpaRepository<BoardFileEntity, Long> {

    List<BoardFileEntity> findByBoardEntity(BoardEntity boardEntity);
}
