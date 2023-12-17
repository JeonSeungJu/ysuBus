package com.example.demo.entity;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CommDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@Table(name = "Notice_Comment_table")
public class NoticeCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String body;

    @Column(length = 20)
    private String writer;

    @Column
    private String writer_pw;

    @Column
    private LocalDateTime write_time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Notice_id")
    private NoticeEntity noticeEntity;


    public static NoticeCommentEntity toSaveEntitys(CommDTO boardDTO, NoticeEntity noticeEntity) {
        NoticeCommentEntity commentEntity = new NoticeCommentEntity();
        commentEntity.setWriter(boardDTO.getWriter());
        commentEntity.setWriter_pw(boardDTO.getWriter_pw());
        commentEntity.setBody(boardDTO.getBody());
        String dateTimeStr = boardDTO.getWrite_time();
        if (dateTimeStr.length() > 16) {
            dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
        }
        LocalDateTime writeTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        commentEntity.setWrite_time(writeTime);
        commentEntity.setNoticeEntity(noticeEntity);
        return commentEntity;
    }

}