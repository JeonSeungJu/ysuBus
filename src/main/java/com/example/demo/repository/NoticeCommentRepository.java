package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.NoticeCommentEntity;
import com.example.demo.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeCommentRepository extends JpaRepository<NoticeCommentEntity, Long> {
    void deleteByNoticeEntity(NoticeEntity noticeEntity);

    List<NoticeCommentEntity> findByNoticeEntity_nid(Long nid);
}
