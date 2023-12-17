package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.BoardFileEntity;
import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByBoardEntity_Cid(Long cid);

    void deleteByBoardEntity(BoardEntity boardEntity);

}
