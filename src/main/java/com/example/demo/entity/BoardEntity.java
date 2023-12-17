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
@Table(name = "Board_table")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long cid;
    @Column
    private String title;
    @Column
    private String writer;
    @Column
    private String writer_pw;
    @Column
    private String body;
    @Column
    private LocalDateTime write_time;
    @Column
    private int comment_count;
    @Column
    private int fileAttached; // 1 or 0


    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardFileEntity> boardFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    public static BoardEntity toSaveEntity(BoardDTO boardDTO){
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setWriter(boardDTO.getWriter());
        boardEntity.setWriter_pw(boardDTO.getWriter_pw());
        boardEntity.setBody(boardDTO.getBody());
        String dateTimeStr = boardDTO.getWrite_time();
        if (dateTimeStr.length() > 16) {
            dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
        }
        LocalDateTime writeTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        boardEntity.setWrite_time(writeTime);
        boardEntity.setComment_count(0);
        boardEntity.setFileAttached(0);
        return boardEntity;
    }
    public static BoardEntity toSaveFileEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setTitle(boardDTO.getTitle());
        boardEntity.setWriter(boardDTO.getWriter());
        boardEntity.setWriter_pw(boardDTO.getWriter_pw());
        boardEntity.setBody(boardDTO.getBody());
        String dateTimeStr = boardDTO.getWrite_time();
        if (dateTimeStr.length() > 16) {
            dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
        }
        LocalDateTime writeTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        boardEntity.setWrite_time(writeTime);
        boardEntity.setComment_count(0);
        boardEntity.setFileAttached(1); // 파일 있음.
        return boardEntity;
    }
    // Getters and setters
}