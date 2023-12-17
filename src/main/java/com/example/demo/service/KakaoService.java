package com.example.demo.service;

import com.example.demo.dto.KakaoDTO;
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
public class KakaoService {

    private final JdbcTemplate jdbcTemplate;
    private String url = "jdbc:mysql://localhost:3306/bus?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
    private String username = "root";
    private String password = "123456";

    public List<KakaoDTO> getKakaoDataByOrigin(String origin) {
        List<KakaoDTO> kakaoDataList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection != null) {
                // SQL 쿼리 작성
                String sql = "SELECT * FROM kakao_table WHERE origin = ?";

                // SQL 쿼리 실행
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, origin);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            // 결과를 KakaoDTO로 변환하여 리스트에 추가
                            KakaoDTO kakaoDTO = new KakaoDTO();
                            kakaoDTO.setDriver_email(resultSet.getString("driver_email"));
                            kakaoDTO.setOrigin(resultSet.getString("origin"));
                            kakaoDTO.setSection(resultSet.getInt("sections"));
                            // 필요한 다른 필드 설정

                            kakaoDataList.add(kakaoDTO);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return kakaoDataList;
    }
}




