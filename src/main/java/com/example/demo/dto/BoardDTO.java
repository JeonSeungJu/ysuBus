package com.example.demo.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardDTO {
    private int cid; // 게시판 아이디
    private int nid; // 공지사항 아이디
    private String title;
    private String writer;
    private String writer_pw;
    private String body;
    private String write_time;
    private int comment_count;
    private List<MultipartFile> images;
    private int mid; // 이미지 아이디
    private int comment_id; // 댓글 아이디
    private String originalFileName; // 원본 파일 이름
    private String storedFileName; // 서버 저장용 파일 이름
}
