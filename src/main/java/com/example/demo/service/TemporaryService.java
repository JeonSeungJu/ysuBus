package com.example.demo.service;


import com.example.demo.dto.MemberDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.entity.TemporaryEntity;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.TemporaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemporaryService {
    private final TemporaryRepository temporaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public  MemberDTO getUserInfoByEmail(String driver_email) {
        TemporaryEntity user = temporaryRepository.findByDriverEmail(driver_email);
        if (user != null) {
            MemberDTO memberInfo = new MemberDTO();
            memberInfo.setDriver_email(user.getDriverEmail());
            memberInfo.setDriver_name(user.getDriverName());
            memberInfo.setDriver_birth(user.getDriverBirth());
            memberInfo.setDriver_tel(user.getDriverTel());
            // 다른 필드도 필요에 따라 설정합니다.
            return memberInfo;
        }
        return null; // 사용자 정보가 없을 경우 null 반환
    }


    public void signUp(MemberDTO memberDTO) {

        TemporaryEntity temporaryEntity = TemporaryEntity.toSaveEntity(memberDTO);

        String hashedPassword = passwordEncoder.encode(temporaryEntity.getDriverPw());
        temporaryEntity.setDriverPw(hashedPassword);
        // 데이터베이스에 저장
        temporaryRepository.save(temporaryEntity);
    }

    public List<TemporaryEntity> getAllTemporaryData() {
        return temporaryRepository.findAll();
    }

    public boolean approveOrRejectTemporaryUser(String driver_email, boolean b) {
        TemporaryEntity temporaryUser = temporaryRepository.findByDriverEmail(driver_email);

        if (temporaryUser != null) {
            if (b) {
                // 승인 작업: 임시 데이터를 회원 데이터베이스에 저장하고 임시 데이터베이스에서 삭제
                MemberEntity member = new MemberEntity();
                member.setDriverPw(temporaryUser.getDriverPw());
                member.setDriverName(temporaryUser.getDriverName());
                member.setDriverEmail(temporaryUser.getDriverEmail());
                member.setDriverTel(temporaryUser.getDriverTel());
                member.setDriverBirth(temporaryUser.getDriverBirth());
                // 다른 필드들도 설정

                // 회원 데이터베이스에 저장
                memberRepository.save(member);

                // 임시 데이터베이스에서 삭제
                temporaryRepository.delete(temporaryUser);

                return true; // 승인 성공
            } else {
                // 거부 작업: 임시 데이터를 임시 데이터베이스에서 삭제
                temporaryRepository.delete(temporaryUser);
                return true; // 거부 성공
            }
        } else {
            return false; // 해당 temporaryUserId에 대한 임시 데이터가 없음
        }
    }

    public List<String> getAllEmails() {
        List<TemporaryEntity> members = temporaryRepository.findAll();
        List<String> emails = new ArrayList<>();
        for (TemporaryEntity member : members) {
            emails.add(member.getDriverEmail());
        }
        return emails;
    }


}

