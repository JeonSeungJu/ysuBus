package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {
    List<NoticeEntity> findAll(Sort sort);

    Page<NoticeEntity> findByTitleIgnoreCaseContainingOrBodyIgnoreCaseContaining(String search, String search1, Pageable pageable);

    Page<NoticeEntity> findByNidLessThanOrderByNidDesc(long nid, Sort sort, PageRequest of);

    Page<NoticeEntity> findByNidLessThanAndTitleContainingOrBodyContainingOrderByNidDesc(long nid, String search, String search1, Pageable pageable);
}
