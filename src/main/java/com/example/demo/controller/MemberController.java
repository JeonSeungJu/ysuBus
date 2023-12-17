package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.KakaoDTO;
import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.MemberDTO;
import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.BoardFileEntity;
import com.example.demo.entity.LocationEntity;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.LocationRepository;
import com.example.demo.response.ApiResponse;
/*import com.example.demo.service.LocationService;*/
import com.example.demo.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final TemporaryService temporaryService;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;
    private final KakaoService kakaoService;


    //  회원가입 api
    @PostMapping("/temp-users")
    public ResponseEntity<ApiResponse> signUp(@RequestBody MemberDTO memberDTO) {
        temporaryService.signUp(memberDTO);

        // 회원가입 성공 시
        ApiResponse response = new ApiResponse("success");
        System.out.println(response);
        System.out.println(response + " ad");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody MemberDTO memberDTO) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            // 입력한 비밀번호를 해시하여 저장된 해시값과 비교
            if (passwordEncoder.matches(memberDTO.getDriver_pw(), loginResult.getDriver_pw())) {
                ApiResponse response = new ApiResponse("success");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse response = new ApiResponse("false");
                return ResponseEntity.ok(response);
            }
        } else {
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse> checkData(@RequestBody MemberDTO memberDTO) {
        String driver_email = memberDTO.getDriver_email();

        if (driver_email != null) {
            // 이메일 중복 체크
            boolean isEmailDuplicate = memberService.isEmailDuplicate(driver_email);
            if (isEmailDuplicate) {
                return ResponseEntity.ok(new ApiResponse("success"));
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("false"));
        }
    }

    @PostMapping("/search-email")
    public ResponseEntity<ObjectNode> findUserId(@RequestBody MemberDTO memberDTO) {
        String userEmail = memberService.checkEmail(memberDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode email = objectMapper.createObjectNode();
        if (userEmail != null) {
            email.put("result", userEmail);
            email.put("driver_email", userEmail);
            return ResponseEntity.ok(email);
        } else {
            email.put("result", "false");
            return ResponseEntity.ok(email);
        }
    }

    @PostMapping("/search-pw")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody MemberDTO memberDTO) {
        boolean updated = memberService.updatePassword(memberDTO);

        if (updated) {
            ApiResponse response = new ApiResponse("success");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/mypage")
    public ResponseEntity<ObjectNode> getMemberInfo(@RequestBody MemberDTO requestDTO) {
        String driver_Email = requestDTO.getDriver_email();
        MemberDTO memberDTO = memberService.getMemberInfoById(driver_Email);
        if (memberDTO == null) {
            return ResponseEntity.notFound().build();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode mypage = objectMapper.createObjectNode();
        mypage.put("result", "success");
        mypage.put("driver_email", driver_Email);
        mypage.put("driver_name", memberDTO.getDriver_name());
        mypage.put("driver_birth", memberDTO.getDriver_birth());
        mypage.put("driver_tel", memberDTO.getDriver_tel());
        return ResponseEntity.ok(mypage);
    }

    @PostMapping("/infoChange")
    public ResponseEntity<String> updateDriverInfo(@RequestBody MemberDTO memberDTO) {
        try {
            // Check if the required fields are not null or empty
            if (memberDTO != null && memberDTO.getDriver_email() != null && !memberDTO.getDriver_email().isEmpty()) {
                // Perform the update operation using the provided data
                boolean isUpdated = memberService.updateDriverInfo(memberDTO);
                if (isUpdated) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode infoChange = objectMapper.createObjectNode();
                    infoChange.put("result", "success");
                    return ResponseEntity.ok(infoChange.toString());
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode infoChange = objectMapper.createObjectNode();
                    infoChange.put("result", "false");
                    return ResponseEntity.ok(infoChange.toString());
                }
            } else {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode infoChange = objectMapper.createObjectNode();
                infoChange.put("result", "false");
                return ResponseEntity.ok(infoChange.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @PostMapping("/get-user-list")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getAllEmails() {
        List<String> emails = memberService.getAllEmails();
        if (!emails.isEmpty()) {
            List<Map<String, String>> responseList = new ArrayList<>();
            for (String email : emails) {
                Map<String, String> emailMap = new HashMap<>();
                emailMap.put("driver_email", email); // email을 사용합니다.
                responseList.add(emailMap);
            }

            Map<String, List<Map<String, String>>> response = new HashMap<>();
            response.put("members", responseList);

            System.out.println(response + " asdasdasd");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.noContent().build(); // 데이터가 없는 경우
        }
    }

    @PostMapping("/ban-user-force")
    public ResponseEntity<ApiResponse> deleteMember(@RequestBody MemberDTO memberDTO) {
        boolean deleted = memberService.deleteMemberByEmail(memberDTO.getDriver_email());

        if (deleted) {
            return ResponseEntity.ok(new ApiResponse("success"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("false"));
        }
    }
    @PostMapping("/member_drop")
    public ResponseEntity<ApiResponse> deleteMembers(@RequestBody MemberDTO memberDTO) {
        // Perform the member deletion using the provided data
        boolean isDeleted = memberService.deleteMember(memberDTO.getDriver_email(),memberDTO.getDriver_pw());
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse("success"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("false"));
        }

    }

    @PostMapping("/bus")
    public ResponseEntity<ApiResponse> addOrUpdateLocation(@RequestBody LocationDTO locationDTO) throws JsonProcessingException {
        boolean success = locationService.updateOrInsertLocation(locationDTO);
        if (success) {
            System.out.println("위도 경도" + locationDTO.getLatitude() + locationDTO.getLatitude());
            return ResponseEntity.ok(new ApiResponse("success"));
        } else {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<String> Uesrservice(@RequestBody LocationDTO locationDTO) throws JsonProcessingException {
        String origin = locationDTO.getStartings();
        List<LocationEntity> locationEntities = locationRepository.findAllByStartings(origin);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode responseJson = objectMapper.createArrayNode();
        ArrayNode mokdongSections = objectMapper.createArrayNode();
        if (locationEntities.isEmpty()) {
            if (origin.equals("목동")) {
                for (int i = 0; i < 6; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Mokdong_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -1);
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("시흥")) {
                for (int i = 0; i < 8; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Siheung_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -1);
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("인천")) {
                for (int i = 0; i < 6; i++) {
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Incheon_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));

                    sectionObject.put(sectionKey, -1);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + sectionObject.get(sectionKey));
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            }
        }
        LocationEntity firstLocation = locationEntities.get(0); // 첫 번째 LocationEntity 객체 가져오기
        int geoState = firstLocation.getGeoState(); // geo_state 값을 추출

        System.out.println(geoState);

        if (geoState == -1) {
            List<KakaoDTO> kakaoData = kakaoService.getKakaoDataByOrigin(origin);
            System.out.println(kakaoData);
            // 여기서 JSON 데이터 생성
            if (origin.equals("목동")) {
                int maxSections = Math.min(6, kakaoData.size());

                for (int i = 0; i < 6; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Mokdong_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -1);
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;
                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                System.out.println("JSON 응답 데이터: " + jsonResponse);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("시흥")) {
                int maxSections = Math.min(8, kakaoData.size());
                for (int i = 0; i < 8; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Siheung_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -1);
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("인천")) {
                int maxSections = Math.min(6, kakaoData.size());
                for (int i = 0; i < 6; i++) {
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Incheon_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));

                    sectionObject.put(sectionKey, -1);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + sectionObject.get(sectionKey));
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            }
            // JSON 데이터를 문자열로 변환하여 반환
            String jsonResponse = objectMapper.writeValueAsString(responseJson);

            return ResponseEntity.ok(jsonResponse);
        } else if (geoState == -140 || geoState == -141 || geoState == -142) {
            List<KakaoDTO> kakaoData = kakaoService.getKakaoDataByOrigin(origin);
            System.out.println(kakaoData);
            // 여기서 JSON 데이터 생성
            if (origin.equals("목동")) {
                int maxSections = Math.min(6, kakaoData.size());

                for (int i = 0; i < 6; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Mokdong_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -141);
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;
                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                System.out.println("JSON 응답 데이터: " + jsonResponse);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("시흥")) {
                int maxSections = Math.min(8, kakaoData.size());
                for (int i = 0; i < 8; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Siheung_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    sectionObject.put(sectionKey, -142);
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("인천")) {
                int maxSections = Math.min(6, kakaoData.size());
                for (int i = 0; i < 6; i++) {
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Incheon_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));

                    sectionObject.put(sectionKey, -140);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + sectionObject.get(sectionKey));
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;

                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            }
            // JSON 데이터를 문자열로 변환하여 반환
            String jsonResponse = objectMapper.writeValueAsString(responseJson);

            return ResponseEntity.ok(jsonResponse);
        } else {
            List<KakaoDTO> kakaoData = kakaoService.getKakaoDataByOrigin(origin);

            System.out.println(kakaoData);
            // 여기서 JSON 데이터 생성
            if (origin.equals("목동")) {
                int maxSections = Math.min(6, kakaoData.size());
                int dataShortage = Math.max(0, 6 - maxSections); // 부족한 데이터 개수를 계산
                for (int i = 0; i < 6; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Mokdong_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    if (i < dataShortage) {
                        sectionObject.put(sectionKey, -1); // 데이터가 부족한 경우에는 모두 -1로 설정
                    } else {
                        sectionObject.put(sectionKey, kakaoData.get(i - dataShortage).getSection());
                    }
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;
                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                System.out.println("JSON 응답 데이터: " + jsonResponse);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("시흥")) {
                int maxSections = Math.min(8, kakaoData.size());
                int dataShortage = Math.max(0, 8 - maxSections); // 부족한 데이터 개수를 계산
                for (int i = 0; i < 8; i++) { // 1st부터 6th까지 생성
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Siheung_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));
                    if (i < dataShortage) {
                        sectionObject.put(sectionKey, -1); // 데이터가 부족한 경우에는 모두 -1로 설정
                    } else {
                        sectionObject.put(sectionKey, kakaoData.get(i - dataShortage).getSection());
                    }
                    mokdongSections.add(sectionObject);
                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + (i < maxSections ? kakaoData.get(i).getSection() : -1));
                }
                responseJson = mokdongSections;
                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            } else if (origin.equals("인천")) {
                int maxSections = Math.min(6, kakaoData.size());
                int dataShortage = Math.max(0, 6 - maxSections); // 부족한 데이터 개수를 계산
                for (int i = 0; i < 6; i++) {
                    ObjectNode sectionObject = objectMapper.createObjectNode();
                    String sectionKey = "Incheon_" + (i + 1) +
                            (i == 0 ? "st" : (i == 1 ? "nd" : (i == 2 ? "rd" : "th")));

                    if (i < dataShortage) {
                        sectionObject.put(sectionKey, -1); // 데이터가 부족한 경우에는 모두 -1로 설정
                    } else {
                        sectionObject.put(sectionKey, kakaoData.get(i - dataShortage).getSection());
                    }

                    System.out.println("사용자한테 보내는 값 " + sectionKey + ":" + sectionObject.get(sectionKey));
                    mokdongSections.add(sectionObject);
                }
                responseJson = mokdongSections;
                // JSON 데이터를 문자열로 변환하여 반환
                String jsonResponse = objectMapper.writeValueAsString(responseJson);
                return ResponseEntity.ok(jsonResponse);
            }
            // JSON 데이터를 문자열로 변환하여 반환
            String jsonResponse = objectMapper.writeValueAsString(responseJson);
            return ResponseEntity.ok(jsonResponse);
        }
    }

    @GetMapping("/short")
    public ResponseEntity<String> getUsersService() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseJson = objectMapper.createObjectNode();
        String[] locations = {"안양역", "연성대"};
        for (String location : locations) {
            List<LocationEntity> locationEntities = locationRepository.findByStartings(location);
            if (locationEntities != null && !locationEntities.isEmpty()) {
                for (LocationEntity locationEntity : locationEntities) {
                        System.out.println("locationEntity");
                    // 각 locationEntity에 대한 처리
                    int geo = locationEntity.getGeoState();
                    int geostate = locationEntity.getGeoState();
                    List<KakaoDTO> kakaoData = kakaoService.getKakaoDataByOrigin(location);
                    ArrayNode vehiclesArray = objectMapper.createArrayNode();
                    if (geostate != -1 && kakaoData != null && !kakaoData.isEmpty()) {
                        for (int i = 0; i < kakaoData.size(); i++) {
                            KakaoDTO data = kakaoData.get(i);
                            ObjectNode vehicleInfo = objectMapper.createObjectNode();
                            vehicleInfo.put("bus" + (i + 1), data.getSection());
                            vehiclesArray.add(vehicleInfo);
                        }
                    } else if (geostate == -143 && geostate == -144) {
                        // 데이터가 없는 경우
                        ObjectNode vehicleInfo = objectMapper.createObjectNode();
                        vehicleInfo.put("bus1", geostate);
                        vehiclesArray.add(vehicleInfo);
                    } else {
                        // 데이터가 없는 경우
                        ObjectNode vehicleInfo = objectMapper.createObjectNode();
                        vehicleInfo.put("bus1", -1);
                        vehiclesArray.add(vehicleInfo);
                    }

                    responseJson.set(location, vehiclesArray);
                }
            }else {
                System.out.println("SSSS");
                // locationEntity가 null인 경우에 대한 처리
                ObjectNode vehicleInfo = objectMapper.createObjectNode();
                ArrayNode vehiclesArray = objectMapper.createArrayNode();
                vehicleInfo.put("bus1", -1);
                vehiclesArray.add(vehicleInfo);
                responseJson.set(location, vehiclesArray);
            }
        }

        String jsonResponse = objectMapper.writeValueAsString(responseJson);
        System.out.println(jsonResponse);
        return ResponseEntity.ok(jsonResponse);
    }

    @GetMapping("/beomgye")
    public ResponseEntity<String> getUserservice() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseJson = objectMapper.createObjectNode();

        String locations = "범계역";

        List<LocationEntity> locationEntities = locationRepository.findByStartings(locations);
        if (locationEntities != null && !locationEntities.isEmpty()) {
            for (LocationEntity locationEntity : locationEntities) {
                int geo = locationEntity.getGeoState();
                int geostate = locationEntity.getGeoState();
                List<KakaoDTO> kakaoData = kakaoService.getKakaoDataByOrigin(locations);
                ArrayNode vehiclesArray = objectMapper.createArrayNode();

                if (geostate != -1 && kakaoData != null && !kakaoData.isEmpty()) {
                    for (int i = 0; i < kakaoData.size(); i++) {
                        KakaoDTO data = kakaoData.get(i);
                        ObjectNode vehicleInfo = objectMapper.createObjectNode();
                        vehicleInfo.put("bus" + (i + 1), data.getSection());
                        vehiclesArray.add(vehicleInfo);
                    }
                } else if (geostate == -143) {
                    // 데이터가 없는 경우
                    ObjectNode vehicleInfo = objectMapper.createObjectNode();
                    vehicleInfo.put("bus1", geostate);
                    vehiclesArray.add(vehicleInfo);
                } else {
                    // 데이터가 없는 경우
                    ObjectNode vehicleInfo = objectMapper.createObjectNode();
                    vehicleInfo.put("bus1", -1);
                    vehiclesArray.add(vehicleInfo);
                }

                responseJson.set(locations, vehiclesArray);
            }
        }else {
            // locationEntity가 null인 경우에 대한 처리
            ObjectNode vehicleInfo = objectMapper.createObjectNode();
            ArrayNode vehiclesArray = objectMapper.createArrayNode();
            vehicleInfo.put("bus1", -1);
            vehiclesArray.add(vehicleInfo);
            responseJson.set(locations, vehiclesArray);
        }
        String jsonResponse = objectMapper.writeValueAsString(responseJson);
        System.out.println("asd "+jsonResponse);
        return ResponseEntity.ok(jsonResponse);
    }
}

