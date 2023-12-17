package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "Notice_file_table")
public class NoticeFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Notice_id")
    private NoticeEntity noticeEntity;

    public static NoticeFileEntity toBoardFileEntity(NoticeEntity noticeEntity, String originalFileName, String storedFileName) {
        NoticeFileEntity noticeFileEntity = new NoticeFileEntity();
        noticeFileEntity.setOriginalFileName(originalFileName);
        noticeFileEntity.setStoredFileName(storedFileName);
        noticeFileEntity.setNoticeEntity(noticeEntity);
        return noticeFileEntity;
    }
}
