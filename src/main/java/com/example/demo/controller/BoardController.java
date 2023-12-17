package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CommDTO;
import com.example.demo.dto.ManagerDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ManagerRepository;
import com.example.demo.repository.NoticeRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class BoardController {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ManagerRepository managerRepository;
    private final BoardService boardService;
    private final NoticeRepository noticeRepository;
    @GetMapping(value = "/get-post", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getAllPosts(  @RequestParam(required = false, defaultValue = "0") int cid,
                                                                                @RequestParam(required = false, defaultValue = "all") String search) {
        List<Map<String, Object>> contentList = boardService.searchPostsByCidAndTitle((long) cid,search);
        contentList = boardService.searchPostsByCidAndTitle((int) cid, search);
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("contents", contentList);
        return ResponseEntity.ok(response);
    }
    @GetMapping(value = "/notice", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, List<Map<String, Object>>>>getAllnotice( @RequestParam(required = false, defaultValue = "0") int nid,
                                                                               @RequestParam(required = false, defaultValue = "all") String search) {
        List<Map<String, Object>> contentList = boardService.getAllNoticesWithImages((int) nid, search);
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("contents", contentList);
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = "/write-post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> createPost(
            @RequestParam(value = "image0", required = false) MultipartFile image0,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3,
            @ModelAttribute BoardDTO boardDTO) throws IOException {
        List<MultipartFile> images = new ArrayList<>();
        if (image0 != null) images.add(image0);
        if (image1 != null) images.add(image1);
        if (image2 != null) images.add(image2);
        if (image3 != null) images.add(image3);
        try {
            boardService.save(boardDTO, images);
            ApiResponse response = new ApiResponse("success");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/manager-write-post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> createnotice(
            @RequestParam(value = "image0", required = false) MultipartFile image0,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3,
            @ModelAttribute BoardDTO boardDTO) throws IOException {
        List<MultipartFile> images = new ArrayList<>();
        if (image0 != null) images.add(image0);
        if (image1 != null) images.add(image1);
        if (image2 != null) images.add(image2);
        if (image3 != null) images.add(image3);
        try {
            boardService.saveNotice(boardDTO, images);
            ApiResponse response = new ApiResponse("success");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/update-post")
    public ResponseEntity<ApiResponse> updatePost(
            @RequestParam(value = "image0", required = false) MultipartFile image0,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3,
            @ModelAttribute BoardDTO boardDTO) throws IOException {
        List<MultipartFile> images = new ArrayList<>();
        if (image0 != null) images.add(image0);
        if (image1 != null) images.add(image1);
        if (image2 != null) images.add(image2);
        if (image3 != null) images.add(image3);
        try {
            boolean isUpdated = boardService.updatePost(boardDTO, images);
            if (isUpdated) {
                return ResponseEntity.ok(new ApiResponse("success"));
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }

    @PostMapping("/delete-post")
    public ResponseEntity<ApiResponse> deletePost(@RequestBody BoardDTO boardDTO) {
        // cid와 writer_pw를 사용하여 게시물 삭제 작업을 수행
        boolean isDeleted = boardService.deletePost(boardDTO);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse("success"));
        } else {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }
    @PostMapping("/update-notice")
    public ResponseEntity<ApiResponse> updateNotice(
            @RequestParam(value = "image0", required = false) MultipartFile image0,
            @RequestParam(value = "image1", required = false) MultipartFile image1,
            @RequestParam(value = "image2", required = false) MultipartFile image2,
            @RequestParam(value = "image3", required = false) MultipartFile image3,
            @ModelAttribute BoardDTO boardDTO) throws IOException {
        List<MultipartFile> images = new ArrayList<>();
        if (image0 != null) images.add(image0);
        if (image1 != null) images.add(image1);
        if (image2 != null) images.add(image2);
        if (image3 != null) images.add(image3);
        try {
            boolean isUpdated = boardService.updateNotice(boardDTO, images);
            if (isUpdated) {
                return ResponseEntity.ok(new ApiResponse("success"));
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }
    @PostMapping("/delete-notice")
    public ResponseEntity<ApiResponse> deleteNotice(@RequestBody BoardDTO boardDTO) {
        // nid와 writer_pw를 사용하여 게시물 삭제 작업을 수행
        boolean isDeleted = boardService.deleteNotice(boardDTO);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse("success"));
        } else {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }


    @GetMapping(value = "/get-comments", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<BoardDTO>> getCommentsForPost(
            @RequestParam(name = "cid", required = false) Long cid,
            @RequestParam(name = "nid", required = false) Long nid) {
        if (cid != null) {
            // cid 값이 존재하는 경우, 해당 cid 값에 따라 처리를 분기
            List<BoardDTO> comments = boardService.getCommentsForPost(cid);
            return ResponseEntity.ok(comments);
        } else if (nid != null) {
            // nid 값이 존재하는 경우, 해당 nid 값에 따라 처리를 분기 (예: 공지사항 댓글)
            List<BoardDTO> comments = boardService.getCommentsForNotice(nid);
            return ResponseEntity.ok(comments);
        }
        return null;
    }
    @PostMapping("/addComment")
    public ResponseEntity<ApiResponse> addComment(@RequestBody CommDTO boardDTO) {
        long cid = boardDTO.getCid();
        long nid = boardDTO.getNid();
        if(cid != 0){
            boolean isUpdated = boardService.addComment(boardDTO);
            if (isUpdated) {
                ApiResponse response = new ApiResponse("success");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse response = new ApiResponse("false");
                return ResponseEntity.badRequest().body(response);
            }
        } else if(nid != 0){
            boolean isUpdated = boardService.addNoticeComment(boardDTO);
            if (isUpdated) {
                ApiResponse response = new ApiResponse("success");
                return ResponseEntity.ok(response);
            } else {
                ApiResponse response = new ApiResponse("false");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            ApiResponse response = new ApiResponse("false");
            return ResponseEntity.badRequest().body(response);
        }
    }

        @PatchMapping("/update-comm")
    public ResponseEntity<ApiResponse> updateComment(@RequestBody CommDTO boardDTO) {
        long cid = boardDTO.getCid();
        long nid = boardDTO.getNid();
        if(cid != 0) {
            boolean isUpdated = boardService.updateComment((long) boardDTO.getComment_id(), boardDTO.getBody(), boardDTO.getWrite_time());
            if (isUpdated) {
                return ResponseEntity.ok(new ApiResponse("success"));
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        }else if(nid != 0){
            boolean isUpdated = boardService.updateNoticeComment((long) boardDTO.getComment_id(), boardDTO.getBody(), boardDTO.getWrite_time());
            if (isUpdated) {
                return ResponseEntity.ok(new ApiResponse("success"));
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } else {
            return ResponseEntity.ok(new ApiResponse("false"));
        }
    }

    @PostMapping("/delete-comm")
    public ResponseEntity<ApiResponse> deleteComment(@RequestBody CommDTO boardDTO) {
        long cid = boardDTO.getCid();
        long nid = boardDTO.getNid();
        System.out.println(boardDTO);
        if(cid != 0) {
            boolean isDeleted = boardService.deleteComment((long) boardDTO.getComment_id(), boardDTO.getWriter_pw());
            if (isDeleted) {
                // 댓글 삭제 성공 시, 해당 게시글의 comment_count를 감소
                boolean isDecremented = boardService.decrementCommentCount(boardDTO.getCid());
                System.out.println(isDecremented);
                if (isDecremented) {
                    return ResponseEntity.ok(new ApiResponse("success"));
                } else {
                    return ResponseEntity.ok(new ApiResponse("false"));
                }
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } else if(nid != 0){
            boolean isDeleted = boardService.deleteNoticeComment((long) boardDTO.getComment_id(), boardDTO.getWriter_pw());
            if (isDeleted) {
                System.out.println((long) boardDTO.getNid());
                // 댓글 삭제 성공 시, 해당 게시글의 comment_count를 감소
                boolean isDecremented = boardService.decrementNoticeCommentCount(boardDTO.getNid());
                if (isDecremented) {
                    return ResponseEntity.ok(new ApiResponse("success"));
                } else {
                    return ResponseEntity.ok(new ApiResponse("false"));
                }
            } else {
                return ResponseEntity.ok(new ApiResponse("false"));
            }
        } else {
            return ResponseEntity.ok(new ApiResponse("false"));
        }

    }

    @PostMapping("/check-post-id")
    public ResponseEntity<ApiResponse> checkAuthor(@RequestBody BoardDTO boardDTO) {
        // 사용자로부터 전달받은 작성자와 작성자 비밀번호
        Long cid = (long) boardDTO.getCid();
        String writerPw = boardDTO.getWriter_pw();
        // 데이터베이스에서 해당 작성자의 정보 가져오기
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(cid);
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            // 값이 존재할 때 처리
            if (writerPw.equals(boardEntity.getWriter_pw())) {
                return ResponseEntity.ok(new ApiResponse("success"));
                }
            }return ResponseEntity.ok(new ApiResponse("false"));
    }


    @PostMapping("/check-manager-id")
    public ResponseEntity<ApiResponse> checkManager(@RequestBody ManagerDTO managerDTO) {
        // 데이터베이스에서 해당 매니저의 정보 가져오기
        String id = managerDTO.getId();
        String writerPw = managerDTO.getPw();
        ManagerEntity managerEntity  = managerRepository.findById(id).get();
        if(managerEntity != null) {
            if (writerPw.equals(managerEntity.getPw())) {
                return ResponseEntity.ok(new ApiResponse("success"));
            }
        }return ResponseEntity.ok(new ApiResponse("false"));
    }

    @PostMapping("/check-comment-id")
    public ResponseEntity<ApiResponse> checkComment(@RequestBody CommDTO commDTO) {
        // 사용자로부터 전달받은 댓글 아이디, 작성자 아이디, 작성자 비밀번호
        Long commentId = (long) commDTO.getComment_id();
        String writerPw = commDTO.getWriter_pw();

        // 데이터베이스에서 해당 댓글 아이디를 가진 레코드 가져오기
        CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);

        if (commentEntity != null) {
        // 댓글이 존재하는 경우 해당 댓글의 작성자 아이디 가져오기
            // 작성자 아이디가 일치하는 경우, 해당 작성자의 비밀번호 가져오기
            String dbWriterPw = commentEntity.getWriter_pw();
            // 사용자로부터 전달받은 비밀번호와 데이터베이스의 비밀번호 비교
            if (writerPw.equals(dbWriterPw)) {
                return ResponseEntity.ok(new ApiResponse("success"));
            }

        }
        return ResponseEntity.ok(new ApiResponse("false"));// 사용자가 입력한 정보와 데이터베이스 정보 불일치
    }
}
