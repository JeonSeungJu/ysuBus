package com.example.demo.service;


import com.example.demo.dto.MemberDTO;
import com.example.demo.entity.MemberEntity;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public MemberDTO login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberEmail = memberRepository.findById(memberDTO.getDriver_email());
        if (byMemberEmail.isPresent()) {
            MemberEntity memberEntity = byMemberEmail.get();

            // 비밀번호 비교
            if (passwordEncoder.matches(memberDTO.getDriver_pw(), memberEntity.getDriverPw())) {
                MemberDTO dto = MemberDTO.toMemberDTO(memberEntity);
                return dto;
            } else {
                // 비밀번호 불일치(로그인 실패)
                return null;
            }
        } else {
            // 해당 이메일을 가진 회원이 없음
            return null;
        }
    }
    public void signUp(MemberDTO memberDTO) {
        // UserDTO를 UserEntity로 변환

        MemberEntity memberEntity = MemberEntity.toSaveEntity(memberDTO);
        // 데이터베이스에 저장
        memberRepository.save(memberEntity);
    }
    @Transactional
    public boolean isEmailDuplicate(String driver_email) {
        Optional<MemberEntity> member = memberRepository.findByDriverEmail(driver_email);
        return !member.isPresent();
    }

    public MemberDTO getMemberInfoById(String driver_Email) {
        Optional<MemberEntity> memberEntityOptional = memberRepository.findByDriverEmail(driver_Email);

        if (memberEntityOptional != null && memberEntityOptional.isPresent()) {
            MemberEntity memberEntity = memberEntityOptional.get();

            // MemberEntity를 MemberDTO로 변환하여 반환
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setDriver_name(memberEntity.getDriverName());
            memberDTO.setDriver_birth(memberEntity.getDriverBirth());
            memberDTO.setDriver_tel(memberEntity.getDriverTel());
            memberDTO.setDriver_email(memberEntity.getDriverEmail());

            return memberDTO;
        } else {
            return null;
        }
    }

    public boolean updateDriverInfo(MemberDTO memberDTO) {
        String driver_Email = memberDTO.getDriver_email();
        String driverPw = memberDTO.getDriver_pw();
        // 데이터베이스에서 해당 회원 정보를 조회합니다.
        Optional<MemberEntity> optionalMember = memberRepository.findByDriverEmail(driver_Email);

        if (optionalMember.isPresent()) {
            MemberEntity memberEntity = optionalMember.get();
            // 비밀번호를 비교하여 일치할 경우에만 업데이트 수행
            if (memberEntity.getDriverPw().equals(driverPw)) {
                // 업데이트할 필드들을 설정합니다.
                memberEntity.setDriverName(memberDTO.getDriver_name());
                memberEntity.setDriverBirth(memberDTO.getDriver_birth());
                memberEntity.setDriverTel(memberDTO.getDriver_tel());
                memberEntity.setDriverEmail(memberDTO.getDriver_email());

                // 변경된 정보를 저장합니다.
                memberRepository.save(memberEntity);

                return true; // 업데이트 성공
            }
        }

        return false; // 해당 회원이 존재하지 않거나 비밀번호가 일치하지 않음
    }

    public boolean deleteMember(String driver_Email, String driverPw) {
        Optional<MemberEntity> member = memberRepository.findByDriverEmail(driver_Email);
        if (member.isPresent()) {
                System.out.println(member + "asd");
                if (member.get().getDriverPw().equals(driverPw)) {
                    memberRepository.delete(member.get());
                    return true;
                } else {
                    return false;
                }
            }
        return false;
    }




    public String checkEmail(MemberDTO memberDTO) {
        String driverName = memberDTO.getDriver_name();
        String driverTel = memberDTO.getDriver_tel();
        String driverBirth = memberDTO.getDriver_birth();

        // 입력된 데이터와 일치하는 사용자를 조회
        Optional<MemberEntity> memberEntity = memberRepository.findByDriverNameAndDriverTelAndDriverBirth(driverName, driverTel, driverBirth);

        if (memberEntity.isPresent()) {
            // 사용자가 존재하면 이메일 반환
            return memberEntity.get().getDriverEmail();
        } else {
            // 사용자가 존재하지 않는 경우
            return null;
        }
    }

    public boolean updatePassword(MemberDTO memberDTO) {
        String driverId = memberDTO.getDriver_email();
        String newPassword = memberDTO.getDriver_pw();

        // 사용자가 존재하는지 확인
        Optional<MemberEntity> optionalMember = memberRepository.findByDriverEmail(driverId);

        if (optionalMember.isPresent()) {
            MemberEntity member = optionalMember.get();

            // 새로운 비밀번호를 해시화
            String hashedPassword = passwordEncoder.encode(newPassword);
            member.setDriverPw(hashedPassword);

            memberRepository.save(member); // 비밀번호 업데이트
            return true;
        } else {
            return false; // 사용자가 존재하지 않는 경우
        }
    }
    public List<String> getAllEmails() {
        List<MemberEntity> members = memberRepository.findAll();
        List<String> emails = new ArrayList<>();
        for (MemberEntity member : members) {
            emails.add(member.getDriverEmail());
        }
        return emails;
    }

    public boolean deleteMemberByEmail(String driver_email) {
        Optional<MemberEntity> member = memberRepository.findByDriverEmail(driver_email);
        if (member.isPresent()) {
            memberRepository.delete(member.get());
            return true;
        } else {
            return false; // 해당 이메일을 가진 회원이 없는 경우
        }
    }


}

