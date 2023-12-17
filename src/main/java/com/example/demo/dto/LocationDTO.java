package com.example.demo.dto;

import com.example.demo.entity.LocationEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDTO {
    private String driver_email;
    private String startings;
    private double latitude;
    private double longitude;
    private String time;
    private int geo_state;

    public static LocationDTO toDTO(LocationEntity locationEntity) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setDriver_email(locationEntity.getDriverEmail());
        locationDTO.setStartings(locationEntity.getStartings());
        locationDTO.setLatitude(locationEntity.getLatitude());
        locationDTO.setLongitude(locationEntity.getLongitude());
        locationDTO.setGeo_state(locationEntity.getGeoState());
        locationDTO.setTime(locationEntity.getTime());
        return locationDTO;
    }
}
