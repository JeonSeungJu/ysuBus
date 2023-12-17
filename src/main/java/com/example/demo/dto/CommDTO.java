package com.example.demo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommDTO {
    private int cid;
    private int nid;
    private String writer;
    private String writer_pw;
    private String body;
    private String write_time;
    private int comment_id;
}
