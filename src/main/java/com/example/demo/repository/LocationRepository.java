package com.example.demo.repository;

import com.example.demo.entity.LocationEntity;
import com.example.demo.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {

    List<LocationEntity> findAllByStartings(String origin);

    LocationEntity findByDriverEmail(String driverEmail);

    List<LocationEntity> findByStartings(String origin);
}