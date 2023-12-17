package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.ManagerEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<ManagerEntity, String> {

}
