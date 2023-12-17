package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.BoardFileEntity;
import com.example.demo.entity.NoticeEntity;
import com.example.demo.entity.NoticeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFileRepository extends JpaRepository<NoticeFileEntity, Long> {
    List<NoticeFileEntity> findByNoticeEntity(NoticeEntity noticeEntity);
}
