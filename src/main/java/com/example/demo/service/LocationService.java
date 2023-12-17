package com.example.demo.service;

import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.staDTO;

import com.example.demo.entity.LocationEntity;

import com.example.demo.repository.LocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final JdbcTemplate jdbcTemplate;
    private final LocationRepository locationRepository;

    String url = "jdbc:mysql://localhost:3306/bus?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
    String username = "root";
    String password = "123456";


    public boolean updateOrInsertLocation(LocationDTO locationDTO) throws JsonProcessingException {
        LocationEntity existingLocation = locationRepository.findByDriverEmail(locationDTO.getDriver_email());

        LocationEntity locationEntity = LocationEntity.toEntity(locationDTO);
        int geo = locationDTO.getGeo_state();
        if(geo == -124){
            String Startings = "연성대";
            locationEntity.setStartings(Startings);
            locationRepository.save(locationEntity);
        }else if(geo == -134){
            String Startings = "안양역";
            locationEntity.setStartings(Startings);
            locationRepository.save(locationEntity);
        }else {
            locationRepository.save(locationEntity);
        }
        int geoState = locationDTO.getGeo_state();
        String driverEmail = locationDTO.getDriver_email();

        if (shouldCallKakaoApi(geoState)) {
            List<staDTO> matchingLocations = new ArrayList<>();
            String startings = locationDTO.getStartings();
            double Longitude = locationDTO.getLongitude();
            double Latitude = locationDTO.getLatitude();
            if (startings.contains("인천") || startings.contains("목동") || startings.contains("시흥")) {
                String query = "SELECT * FROM location_table WHERE startings = ?";
                List<LocationDTO> start = jdbcTemplate.query(query, new Object[]{startings}, new BeanPropertyRowMapper<>(LocationDTO.class));
                // 일치하는 데이터가 없거나 geo_state가 -1인 경우 실행
                if (start.isEmpty() || start.get(0).getGeo_state() != -1) {
                    String longsQuery = "SELECT * FROM longs WHERE area = ?";
                    List<staDTO> matchingLongs = jdbcTemplate.query(longsQuery, new Object[]{startings}, new BeanPropertyRowMapper<>(staDTO.class));
                    matchingLocations.addAll(matchingLongs);
                } else if (start.get(0).getGeo_state() == -1) {
                    // 데이터가 존재하면서 geo_state가 -1인 경우
                    // 데이터베이스에서 해당 데이터 삭제
                    String deleteQuery = "DELETE FROM location_table WHERE startings = ?";
                    int deletedRows = jdbcTemplate.update(deleteQuery, new Object[]{startings});
                    if (deletedRows > 0) {

                    } else {
                        System.out.println("해당 데이터가 이미 삭제되었거나 존재하지 않습니다.");
                    }
                    String longsQuery = "SELECT * FROM longs WHERE area = ?";
                    List<staDTO> matchingLongs = jdbcTemplate.query(longsQuery, new Object[]{startings}, new BeanPropertyRowMapper<>(staDTO.class));
                    matchingLocations.addAll(matchingLongs);
                }
            } else if (startings.contains("범계역") || startings.contains("안양역") || startings.contains("연성대")) {
                String shortsQuery = "SELECT * FROM shorts WHERE area = ?";
                List<staDTO> matchingLongs = jdbcTemplate.query(shortsQuery, new Object[]{startings}, new BeanPropertyRowMapper<>(staDTO.class));
                matchingLocations.addAll(matchingLongs);
                System.out.println("Matching Longs: " + matchingLongs);
            } else {
                return false; // 실패한 경우 false 반환
            }

            // 경유지 정보 필터링
            if (geoState >= 51 && geoState <= 61) {
                for (int i = 51; i <= 61; i += 2) {
                    if (geoState == i) {
                        matchingLocations.remove(0); // 맨 위의 정보 제거
                        System.out.println(matchingLocations);
                    }
                }
            }

            if (geoState >= 71 && geoState <= 81) {
                for (int i = 71; i <= 81; i += 2) {
                    if (geoState == i) {
                        matchingLocations.remove(0); // 맨 위의 정보 제거
                        System.out.println(matchingLocations);
                    }
                }
            }

            if (geoState >= 91 && geoState <= 105) {
                for (int i = 91; i <= 105; i += 2) {
                    if (geoState == i) {
                        matchingLocations.remove(0); // 맨 위의 정보 제거
                        System.out.println(matchingLocations);
                    }
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode originJson = objectMapper.createObjectNode();
            if (geoState == -124) {
                originJson.put("name", "연성대");
                originJson.put("x", Longitude);
                originJson.put("y", Latitude);
            }
            else if(geoState == -134) {
                originJson.put("name", "안양역");
                originJson.put("x", Longitude);
                originJson.put("y", Latitude);
            }else{
                originJson.put("name", startings);
                originJson.put("x", Longitude);
                originJson.put("y", Latitude);
            }

            ObjectNode destinationJson = objectMapper.createObjectNode();
            if(geoState == -124){
                destinationJson.put("name", "안양역");
                destinationJson.put("x", 126.9225333);
                destinationJson.put("y", 37.4012079);
            }else {
                destinationJson.put("name", "연성대");
                destinationJson.put("x", 126.9089277);
                destinationJson.put("y", 37.3962396);
            }

            ArrayNode waypointsJson = objectMapper.createArrayNode();
            for (staDTO location : matchingLocations) {
                String sta = location.getStation();
                ObjectNode waypoint = objectMapper.createObjectNode();
                waypoint.put("name", location.getStation());
                waypoint.put("x", location.getLongitude());
                waypoint.put("y", location.getLatitude());
                waypointsJson.add(waypoint);
            }
            ObjectNode requestJson = objectMapper.createObjectNode();
            requestJson.set("origin", originJson);
            requestJson.set("destination", destinationJson);
            requestJson.set("waypoints", waypointsJson);
            requestJson.put("priority", "RECOMMEND");
            requestJson.put("car_fuel", "DIESEL");
            requestJson.put("car_type", 2);
            requestJson.put("car_hipass", true);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + "51a29229ee1c17ecdfb06a1f9750d61a");
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestJson.toString(), headers);

            RestTemplate restTemplate = new RestTemplate();
            String kakaoApiUrl = "https://apis-navi.kakaomobility.com/v1/waypoints/directions";
            ResponseEntity<String> response = restTemplate.exchange(kakaoApiUrl, HttpMethod.POST, entity, String.class);
            String responseBody = response.getBody();

                JsonNode responseJson = objectMapper.readTree(responseBody);
                JsonNode firstRoute = responseJson.get("routes").get(0);
                JsonNode summary = firstRoute.get("summary");
                JsonNode origin = summary.get("origin");
                String originName = origin.get("name").asText();
                JsonNode sections = firstRoute.get("sections");
            if ("인천".equals(originName) || "시흥".equals(originName) || "목동".equals(originName)) {
                if (sections != null && sections.isArray()) {
                    String insertSQL = "INSERT INTO kakao_table (driver_email, origin, sections) VALUES (?, ?, ?)";
                    String deleteSQL = "DELETE FROM kakao_table WHERE driver_email = ?";

                    try (Connection connection = DriverManager.getConnection(url, username, password)) {
                        connection.setAutoCommit(false); // Turn off auto-commit for better performance
                        if (connection.getAutoCommit()) {
                            connection.setAutoCommit(false); // Ensure autocommit is false for the transaction
                        }
                        // 첫 번째 블록에서 수행한 delete 작업과 동일한 delete 작업 수행
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)) {
                            deleteStatement.setString(1, driverEmail);
                            deleteStatement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // Insert 작업을 수행
                        for (int i = 0; i < sections.size(); i++) {
                            JsonNode sectionObj = sections.get(i);
                            int duration = sectionObj.get("duration").asInt();

                            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                                preparedStatement.setString(1, driverEmail);
                                preparedStatement.setString(2, originName);
                                preparedStatement.setInt(3, duration);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                connection.rollback();
                                e.printStackTrace();
                            }
                        }

                        connection.commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                } else {
                    JsonNode sectionObj = sections.get(0);
                    int duration = sectionObj.get("duration").asInt();
                try (Connection connection = DriverManager.getConnection(url, username, password)) {
                    connection.setAutoCommit(false);
                    if (connection != null) {
                        // 아이디가 이미 존재하는지 확인
                        String checkSQL = "SELECT driver_email FROM kakao_table WHERE driver_email = ?";
                        try (PreparedStatement checkStatement = connection.prepareStatement(checkSQL)) {
                            checkStatement.setString(1, driverEmail);
                            ResultSet resultSet = checkStatement.executeQuery();

                            if (resultSet.next()) {
                                // 아이디가 이미 존재하면 해당 데이터를 삭제
                                String deleteSQL = "DELETE FROM kakao_table WHERE driver_email = ?";
                                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)) {
                                    deleteStatement.setString(1, driverEmail);
                                    deleteStatement.executeUpdate();
                                } catch (SQLException e) {
                                    // 삭제 시 오류 처리, 필요한 경우 롤백
                                    connection.rollback();
                                    e.printStackTrace();
                                }
                            }

                            // SQL 쿼리 작성 및 삽입
                            String insertSQL = "INSERT INTO kakao_table (driver_email, origin, sections) VALUES (?, ?, ?)";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                                preparedStatement.setString(1, driverEmail);
                                preparedStatement.setString(2, originName);
                                preparedStatement.setInt(3, duration);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                // 삽입 시 오류 처리, 필요한 경우 롤백
                                connection.rollback();
                                e.printStackTrace();
                            }

                            // 커밋
                            connection.commit();
                        } catch (SQLException e) {
                            // 확인 시 오류 처리, 필요한 경우 롤백
                            connection.rollback();
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    // 연결 시 오류 처리
                    e.printStackTrace();
                }
                }

            }
            // matchingLocations 리스트에는 해당하는 정류장 데이터가 저장됨
            // 이후에 필요한 작업을 수행하도록 구현해주세요.
            return true;

    }
        private List<staDTO> getMatchingLocations (List < staDTO > stationList, String startingLocation){
            List<staDTO> matchingLocations = new ArrayList<>();

            for (staDTO station : stationList) {
                if (station.getArea().equals(startingLocation)) {
                    matchingLocations.add(station);
                }
            }

            return matchingLocations;
        }

        private boolean shouldCallKakaoApi ( int geoState){
            int[] excludedStates = {-1, -100, -101, -102, -103, -104, -105};
            for (int excludedState : excludedStates) {
                if (geoState == excludedState) {
                    return false;
                }
            }
            return true;
        }

}












