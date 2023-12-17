package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Table(name = "manager_table")
public class ManagerEntity {
    @Id
    private String id;
    @Column
    private String pw;
}
