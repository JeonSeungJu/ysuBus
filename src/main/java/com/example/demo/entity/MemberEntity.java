package com.example.demo.entity;


import com.example.demo.dto.MemberDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "member_table")
public class MemberEntity {
    @Id
    @Column(unique = true)
    private String driverEmail;
 // pk 지정
    @Column
    private String driverPw;
    @Column
    private String driverName;
    @Column
    private String driverBirth;
    @Column
    private String driverTel;

    public static MemberEntity toSaveEntity(MemberDTO memberDTO){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setDriverPw(memberDTO.getDriver_pw());
        memberEntity.setDriverName(memberDTO.getDriver_name());
        memberEntity.setDriverBirth(memberDTO.getDriver_birth());
        memberEntity.setDriverTel(memberDTO.getDriver_tel());
        memberEntity.setDriverEmail(memberDTO.getDriver_email());
        return memberEntity;
    }



}