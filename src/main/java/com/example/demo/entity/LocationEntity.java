package com.example.demo.entity;

import com.example.demo.dto.LocationDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "Location_table")
public class LocationEntity {
    @Id // pk 컬럼 지정. 필수
    private String driverEmail;
    @Column(nullable = false)
    private String startings;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    @Column(nullable = false)
    private int geoState;
    @Column(nullable = false)
    private String time;

    public static LocationEntity toEntity(LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDriverEmail(locationDTO.getDriver_email());
        locationEntity.setStartings(locationDTO.getStartings());
        locationEntity.setLatitude(locationDTO.getLatitude());
        locationEntity.setLongitude(locationDTO.getLongitude());
        locationEntity.setGeoState(locationDTO.getGeo_state());
        locationEntity.setTime(locationDTO.getTime());
        return locationEntity;
    }



}