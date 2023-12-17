package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface  BoardRepository  extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findAll(Sort sort);
    Page<BoardEntity> findByCidLessThanOrderByCidDesc(Long cid, Sort sort, PageRequest of);
    Page<BoardEntity> findByTitleIgnoreCaseContainingOrBodyIgnoreCaseContaining(String search, String search1, Pageable pageable);
    Page<BoardEntity> findByCidLessThanAndTitleContainingOrBodyContainingOrderByCidDesc(long cid, String title, String body, Pageable pageable);

}
