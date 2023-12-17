package com.example.demo.repository;

import com.example.demo.entity.TemporaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryRepository extends JpaRepository<TemporaryEntity, String> {

    TemporaryEntity findByDriverEmail(String driverEmail);
}