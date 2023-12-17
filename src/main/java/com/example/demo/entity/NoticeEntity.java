package com.example.demo.entity;

import com.example.demo.dto.BoardDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Notice_table")
public class NoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long nid;
    @Column
    private String title;
    @Column
    private String body;
    @Column
    private String writer;
    @Column
    private String writer_pw;
    @Column
    private LocalDateTime write_time;
    @Column
    private int comment_count;
    @Column
    private int fileAttached; // 1 or 0


    @OneToMany(mappedBy = "noticeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeFileEntity> noticeFileEntities = new ArrayList<>();

    @OneToMany(mappedBy = "noticeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeCommentEntity> noticeCommentEntities = new ArrayList<>();

    public static NoticeEntity toSaveEntity(BoardDTO boardDTO){
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setTitle(boardDTO.getTitle());
        noticeEntity.setBody(boardDTO.getBody());
        noticeEntity.setWriter(boardDTO.getWriter());
        noticeEntity.setWriter_pw(boardDTO.getWriter_pw());
        String dateTimeStr = boardDTO.getWrite_time();
        if (dateTimeStr.length() > 16) {
            dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
        }
        LocalDateTime writeTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        noticeEntity.setWrite_time(writeTime);
        noticeEntity.setComment_count(0);
        noticeEntity.setFileAttached(0);
        return noticeEntity;
    }
    public static NoticeEntity toSaveFileEntity(BoardDTO boardDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setTitle(boardDTO.getTitle());
        noticeEntity.setBody(boardDTO.getBody());
        String dateTimeStr = boardDTO.getWrite_time();
        if (dateTimeStr.length() > 16) {
            dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
        }
        LocalDateTime writeTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        noticeEntity.setWrite_time(writeTime);
        noticeEntity.setComment_count(0);
        noticeEntity.setFileAttached(1); // 파일 있음.
        return noticeEntity;
    }
    // Getters and setters
}