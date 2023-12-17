package com.example.demo.controller;

import com.example.demo.dto.KakaoDTO;
import com.example.demo.dto.LocationDTO;
import com.example.demo.dto.MemberDTO;
import com.example.demo.entity.LocationEntity;
import com.example.demo.entity.MemberEntity;
import com.example.demo.entity.TemporaryEntity;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.TemporaryRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.KakaoService;
import com.example.demo.service.LocationService;
import com.example.demo.service.MemberService;
import com.example.demo.service.TemporaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.jdbc.SuspendableXAConnection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class ManagerController {
    private final MemberRepository memberRepository;
    private final TemporaryService temporaryService;
    private final TemporaryRepository temporaryRepository;

    private final MemberService memberService;
    @PostMapping("/temporary")
    public ResponseEntity<List<TemporaryEntity>> submitTemporarySignUp(@RequestBody MemberDTO memberDTO) {
        List<TemporaryEntity> temporaryUsers = temporaryService.getAllTemporaryData();
        return ResponseEntity.ok(temporaryUsers);
    }
    /*@PostMapping("/process-temporary")
    public ResponseEntity<String> processTemporaryUser(
            @RequestParam String temporaryUserId,
            @RequestParam String action) {
        if ("success".equals(action)) {
            // 관리자가 승인을 선택한 경우
            boolean success = temporaryService.approveOrRejectTemporaryUser(temporaryUserId, true);
            if (success) {
                return ResponseEntity.ok("Temporary user approved successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to approve temporary user");
            }
        } else if ("false".equals(action)) {
            // 관리자가 거부를 선택한 경우
            boolean success = temporaryService.approveOrRejectTemporaryUser(temporaryUserId, false);
            if (success) {
                return ResponseEntity.ok("Temporary user rejected successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to reject temporary user");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid action");
        }
    }*/
    @PostMapping("/accept-user")
    public ResponseEntity<ApiResponse> acceptUser(@RequestBody MemberDTO memberDTO) {
        // 이메일을 기반으로 임시 데이터를 찾아옵니다.
        boolean success = temporaryService.approveOrRejectTemporaryUser(memberDTO.getDriver_email(), true);
        if (success) {
            ApiResponse response = new ApiResponse("success");
            return ResponseEntity.ok(response);
        } else {
            // 임시 데이터가 없는 경우 처리
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @PostMapping("/reject-user")
    public ResponseEntity<ApiResponse> rejectUser(@RequestBody MemberDTO memberDTO) {
        // 이메일을 기반으로 임시 데이터를 찾아 삭제합니다.
        TemporaryEntity temporaryEntity = temporaryRepository.findByDriverEmail(memberDTO.getDriver_email());

        if (temporaryEntity != null) {
            // 임시 데이터를 삭제합니다.
            temporaryRepository.delete(temporaryEntity);

            // 거부 성공 시
            ApiResponse response = new ApiResponse("success");
            return ResponseEntity.ok(response);
        } else {
            // 임시 데이터가 없는 경우 처리
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @PostMapping("/get-temp-user-list")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getAllEmails() {
        List<String> emails = temporaryService.getAllEmails();
        System.out.println(emails + " asdasdasd");
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
    @PostMapping("/get-user-info")
    public ResponseEntity<ObjectNode> getUserInfoByEmail(@RequestBody MemberDTO memberDTO) {
        // 이메일을 기반으로 사용자 정보를 조회합니다.
        String driverEmail = memberDTO.getDriver_email();

        if (driverEmail != null && !driverEmail.isEmpty()) {
            MemberDTO temporaryinfo = temporaryService.getUserInfoByEmail(driverEmail);
            MemberDTO memberinfo = memberService.getMemberInfoById(driverEmail);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode info = objectMapper.createObjectNode();

            if (temporaryinfo != null) {
                info.put("result", "success");
                info.put("driver_email", driverEmail);
                info.put("driver_name", temporaryinfo.getDriver_name());
                info.put("driver_birth", temporaryinfo.getDriver_birth());
                info.put("driver_tel", temporaryinfo.getDriver_tel());
            } else if (memberinfo != null) {
                info.put("result", "success");
                info.put("driver_email", driverEmail);
                info.put("driver_name", memberinfo.getDriver_name());
                info.put("driver_birth", memberinfo.getDriver_birth());
                info.put("driver_tel", memberinfo.getDriver_tel());
            } else {
                info.put("result", "false");
            }

            return ResponseEntity.ok(info);
        } else {
            // 이메일 값이 없는 경우에 대한 처리
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode info = objectMapper.createObjectNode();
            info.put("result", "false");
            return ResponseEntity.ok(info);
        }
    }

}

