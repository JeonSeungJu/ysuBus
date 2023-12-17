package com.example.demo.entity;


import com.example.demo.dto.MemberDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Table(name = "temporary_table")
public class TemporaryEntity {
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
    public static TemporaryEntity toSaveEntity(MemberDTO memberDTO){
        TemporaryEntity memberEntity = new TemporaryEntity();
        memberEntity.setDriverPw(memberDTO.getDriver_pw());
        memberEntity.setDriverName(memberDTO.getDriver_name());
        memberEntity.setDriverBirth(memberDTO.getDriver_birth());
        memberEntity.setDriverTel(memberDTO.getDriver_tel());
        memberEntity.setDriverEmail(memberDTO.getDriver_email());
        return memberEntity;
    }

}