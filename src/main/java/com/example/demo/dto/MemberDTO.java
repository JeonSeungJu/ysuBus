package com.example.demo.dto;
import com.example.demo.entity.MemberEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {
    private String driver_email;
    private String driver_pw;
    private String driver_name;
    private String driver_birth;
    private String driver_tel;


    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setDriver_pw(memberEntity.getDriverPw());
        memberDTO.setDriver_name(memberEntity.getDriverName());
        memberDTO.setDriver_birth(memberEntity.getDriverBirth());
        memberDTO.setDriver_tel(memberEntity.getDriverTel());
        memberDTO.setDriver_email(memberEntity.getDriverEmail());
        return memberDTO;
    }

}