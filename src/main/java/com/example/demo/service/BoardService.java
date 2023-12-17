package com.example.demo.service;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CommDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.entity.CommentEntity.toSaveEntity;
import static com.example.demo.entity.NoticeCommentEntity.toSaveEntitys;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final NoticeCommentRepository noticeCommentRepository;

   /* @Transactional
    public List<Map<String, Object>> getPostsByCid(Long cid) {
        Sort sort = Sort.by(Sort.Direction.DESC, "cid");

        List<BoardEntity> boards;

        Long lastCid;  // 현재 트랜잭션 내에서의 가장 큰 cid 값을 기록하기 위한 변수

        if (cid == null || cid == 0) {
            // 처음 요청 시, 가장 마지막 10개의 데이터를 가져옴
            Pageable pageable = PageRequest.of(0, 7, sort);
            Page<BoardEntity> boardPage = boardRepository.findAll(pageable);
            boards = boardPage.getContent();

        } else {
            // cid 값을 기준으로 그 이전 10개의 데이터를 가져옴\
            // 만약 현재 저장된 데이터 중 cid 값이 cid보다 작은 값이 있는 경우에만 데이터를 가져옴
            boards = boardRepository.findByCidLessThanOrderByCidDesc(cid, sort, PageRequest.of(0, 7));
        }

        List<Map<String, Object>> contentList = new ArrayList<>();

        for (BoardEntity board : boards) {
            List<String> imageUrls = new ArrayList<>();

            for (BoardFileEntity fileEntity : board.getBoardFileEntityList()) {
                String imageUrl = "https://spider-easy-vulture.ngrok-free.app/file/" + fileEntity.getOriginalFileName();
                imageUrls.add(imageUrl);
            }

            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("cid", board.getCid());
            contentMap.put("title", board.getTitle());
            contentMap.put("writer", board.getWriter());
            contentMap.put("body", board.getBody());
            contentMap.put("comment_count", board.getComment_count());
            contentMap.put("write_time", board.getWrite_time());
            contentMap.put("image", imageUrls);

            contentList.add(contentMap);
        }

        return contentList;
    }*/


    @Transactional
    public void save(BoardDTO boardDTO, List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty()) {
            // 이미지가 없는 경우
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 이미지가 있는 경우
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            boardEntity = boardRepository.save(boardEntity); // 글 저장

            List<BoardFileEntity> boardFileEntities = new ArrayList<>();

            for (MultipartFile image : images) {
                String originalFilename = image.getOriginalFilename();
                String storedFileName = "C:/springboot_img/images/" + originalFilename; // 이미지 파일을 저장할 경로
                File file = new File(storedFileName);
                image.transferTo(file);
                // 이미지 정보를 생성하고 글과 연결
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(boardEntity, originalFilename, storedFileName);
                boardFileEntities.add(boardFileEntity);

            }

            boardFileRepository.saveAll(boardFileEntities); // 이미지 파일 정보 저장
            for (BoardFileEntity boardFileEntity : boardFileEntities) {
                System.out.println("Saved Image Path: " + boardFileEntity.getStoredFileName());
            }
        }
    }

    @Transactional
    public void saveNotice(BoardDTO boardDTO, List<MultipartFile> images) throws IOException {
        if (images == null || images.isEmpty()) {
            // 이미지가 없는 경우
            NoticeEntity noticeEntity = NoticeEntity.toSaveEntity(boardDTO);
            noticeRepository.save(noticeEntity);
        } else {
            // 이미지가 있는 경우
            NoticeEntity noticeEntity = NoticeEntity.toSaveFileEntity(boardDTO);
            noticeEntity = noticeRepository.save(noticeEntity); // 글 저장

            List<NoticeFileEntity> noticeFileEntities = new ArrayList<>();

            for (MultipartFile image : images) {
                String originalFilename = image.getOriginalFilename();
                String storedFileName = "C:/springboot_img/images/" + originalFilename; // 이미지 파일을 저장할 경로
                File file = new File(storedFileName);
                image.transferTo(file);
                // 이미지 정보를 생성하고 글과 연결
                NoticeFileEntity noticeFileEntity = NoticeFileEntity.toBoardFileEntity(noticeEntity, originalFilename, storedFileName);
                noticeFileEntities.add(noticeFileEntity);

            }

            noticeFileRepository.saveAll(noticeFileEntities); // 이미지 파일 정보 저장
            for (NoticeFileEntity noticeFileEntity : noticeFileEntities) {
                System.out.println("Saved Image Path: " + noticeFileEntity.getStoredFileName());
            }
        }
    }

    @Transactional
    public boolean updatePost(BoardDTO boardDTO, List<MultipartFile> images) {
        // 게시물을 가져옴
        Optional<BoardEntity> optionalBoard = boardRepository.findById((long) boardDTO.getCid());
        if (optionalBoard.isPresent()) {
            BoardEntity boardEntity = optionalBoard.get();

            // 게시물 내용 수정
            boardEntity.setTitle(boardDTO.getTitle());
            boardEntity.setBody(boardDTO.getBody());
            if (images != null && !images.isEmpty()) {
                // 이미지가 업데이트되어야 하는 경우
                List<BoardFileEntity> newImageEntities = new ArrayList<>();
                for (MultipartFile newImage : images) {
                    String newStoredFileName = "C:/springboot_img/images/" + newImage.getOriginalFilename();
                    File newFile = new File(newStoredFileName);
                    try {
                        newImage.transferTo(newFile);
                        BoardFileEntity newImageEntity = new BoardFileEntity();
                        newImageEntity.setOriginalFileName(newImage.getOriginalFilename());
                        newImageEntity.setStoredFileName(newStoredFileName);
                        newImageEntity.setBoardEntity(boardEntity);
                        newImageEntities.add(newImageEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // 기존 이미지 삭제
                List<BoardFileEntity> existingImages = boardFileRepository.findByBoardEntity(boardEntity);
                for (BoardFileEntity existingImage : existingImages) {
                    if (!newImageEntities.isEmpty()) {
                        // 새 이미지가 있을 때만 삭제
                        boardFileRepository.delete(existingImage);
                        File existingImageFile = new File(existingImage.getStoredFileName());
                        existingImageFile.delete();
                    }
                }

                // 새 이미지 저장
                boardFileRepository.saveAll(newImageEntities);
            }

            return true; // 수정 성공
        }
        return false; // 수정 실패
    }

    @Transactional
    public List<BoardDTO> getCommentsForPost(Long cid) {
        List<CommentEntity> commentEntities = commentRepository.findByBoardEntity_Cid(cid);

        // 내림차순으로 정렬
        Collections.sort(commentEntities, Comparator.comparing(CommentEntity::getWrite_time).reversed());

        List<BoardDTO> commentDTOs = commentEntities.stream()
                .map(commentEntity -> {
                    BoardDTO boardDTO = new BoardDTO();
                    boardDTO.setCid(Math.toIntExact(cid));
                    boardDTO.setComment_id(Math.toIntExact(commentEntity.getId()));
                    boardDTO.setWriter(commentEntity.getWriter());
                    boardDTO.setBody(commentEntity.getBody());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String dateTimeStr = commentEntity.getWrite_time().format(formatter);
                    boardDTO.setWrite_time(dateTimeStr);
                    return boardDTO;
                })
                .collect(Collectors.toList());
        return commentDTOs;
    }
    @Transactional
    public List<BoardDTO> getCommentsForNotice(Long nid) {
        List<NoticeCommentEntity> noticeCommentEntities = noticeCommentRepository.findByNoticeEntity_nid(nid);
        // 내림차순으로 정렬
        Collections.sort(noticeCommentEntities, Comparator.comparing(NoticeCommentEntity::getWrite_time).reversed());
        List<BoardDTO> commentDTOs = noticeCommentEntities.stream()
                .map(noticecommentEntity -> {
                    BoardDTO boardDTO = new BoardDTO();
                    boardDTO.setNid(Math.toIntExact(nid));
                    boardDTO.setComment_id(Math.toIntExact(noticecommentEntity.getId()));
                    boardDTO.setWriter(noticecommentEntity.getWriter());
                    boardDTO.setBody(noticecommentEntity.getBody());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String dateTimeStr = noticecommentEntity.getWrite_time().format(formatter);
                    boardDTO.setWrite_time(dateTimeStr);
                    return boardDTO;
                })
                .collect(Collectors.toList());

        return commentDTOs;
    }

    @Transactional
    public boolean addComment(CommDTO boardDTO) {
        // 먼저 boardEntity를 얻어옵니다. boardEntity를 얻는 방법은 의존성에 따라 다를 수 있습니다.
        // 예를 들어, boardId를 사용하여 boardEntity를 찾아올 수 있습니다.
        // BoardEntity boardEntity = boardRepository.findById(boardDTO.getBoardId()).orElse(null);
        BoardEntity boardEntity = boardRepository.findById((long) boardDTO.getCid()).orElse(null);
        System.out.println("s");
        System.out.println(boardEntity);
        if (boardEntity != null) {
            CommentEntity commentEntity = toSaveEntity(boardDTO, boardEntity);
            CommentEntity savedComment = commentRepository.save(commentEntity);

            boardEntity.setComment_count(boardEntity.getComment_count() + 1);
            boardRepository.save(boardEntity);
            return true;
        }
         else {
            return false;
        }
    }
    @Transactional
    public boolean addNoticeComment(CommDTO boardDTO) {
        NoticeEntity noticeEntity = noticeRepository.findById((long) boardDTO.getNid()).orElse(null);
        System.out.println("s");
        System.out.println(noticeEntity);
        if (noticeEntity != null) {
            NoticeCommentEntity noticeCommentEntity = toSaveEntitys(boardDTO, noticeEntity);
            NoticeCommentEntity savedComment = noticeCommentRepository.save(noticeCommentEntity);

            noticeEntity.setComment_count(noticeEntity.getComment_count() + 1);
            noticeRepository.save(noticeEntity);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateComment(Long comId, String body, String writeTime) {
        Optional<CommentEntity> commentOptional = commentRepository.findById(comId);
        if (commentOptional.isPresent()) {
            CommentEntity commentEntity = commentOptional.get();
            commentEntity.setBody(body);
            String dateTimeStr = writeTime;
            if (dateTimeStr.length() > 16) {
                dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
            }
            LocalDateTime write_Time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            commentEntity.setWrite_time(write_Time);
            commentRepository.save(commentEntity);
            return true;
        }
        return false;
    }
    @Transactional
    public boolean updateNoticeComment(long commentId, String body, String writeTime) {
        Optional<NoticeCommentEntity> noticeCommentOptional = noticeCommentRepository.findById(commentId);
        if (noticeCommentOptional.isPresent()) {
            NoticeCommentEntity noticeCommentEntity = noticeCommentOptional.get();
            noticeCommentEntity.setBody(body);
            String dateTimeStr = writeTime;
            if (dateTimeStr.length() > 16) {
                dateTimeStr = dateTimeStr.substring(0, 16); // 초 및 미리초 부분 제거
            }
            LocalDateTime write_Time = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            noticeCommentEntity.setWrite_time(write_Time);
            noticeCommentRepository.save(noticeCommentEntity);
            return true;
        }
        return false;
    }
    @Transactional
    public boolean deletePost(BoardDTO boardDTO) {

        Optional<BoardEntity> optionalBoard = boardRepository.findById((long) boardDTO.getCid());
        if (optionalBoard.isPresent()) {
            BoardEntity boardEntity = optionalBoard.get();
            if (boardEntity.getWriter_pw().equals(boardDTO.getWriter_pw())) {
                // 댓글 삭제
                commentRepository.deleteByBoardEntity(boardEntity);

                // 게시물과 연결된 이미지 파일들을 삭제
                List<BoardFileEntity> boardFiles = boardFileRepository.findByBoardEntity(boardEntity);
                for (BoardFileEntity boardFile : boardFiles) {
                    String storedFileName = boardFile.getStoredFileName();
                    File file = new File(storedFileName);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                boardFileRepository.deleteAll(boardFiles);

                // 게시물 삭제
                boardRepository.delete(boardEntity);

                return true; // 삭제 성공
            } else {
                return false; // 삭제 실패
            }
            // 게시물을 가져옴
        }
        return false;
    }


    @Transactional
    public boolean deleteComment(Long comId,  String writerPw) {
        Optional<CommentEntity> commentOptional = commentRepository.findById(comId);
        if (commentOptional.isPresent()) {
            CommentEntity commentEntity = commentOptional.get();
            System.out.println(commentEntity);
            // 작성자 이름과 비밀번호가 일치하는지 확인 (비밀번호 확인을 생략하려면 조건을 제거)
            if (commentEntity.getWriter_pw().equals(writerPw)) {
                commentRepository.delete(commentEntity);
                return true;
            }
        }
        return false;
    }
    @Transactional
    public boolean deleteNoticeComment(Long comId, String writerPw) {
        Optional<NoticeCommentEntity> commentOptional = noticeCommentRepository.findById(comId);
        if (commentOptional.isPresent()) {
            NoticeCommentEntity noticeCommentEntity = commentOptional.get();
            System.out.println(noticeCommentEntity);
            // 작성자 이름과 비밀번호가 일치하는지 확인 (비밀번호 확인을 생략하려면 조건을 제거)
            if (noticeCommentEntity.getWriter_pw().equals(writerPw)) {
                noticeCommentRepository.delete(noticeCommentEntity);
                return true;
            }
        }
        return false;
    }
    @Transactional
    public boolean decrementCommentCount(int cid) {
        Optional<BoardEntity> boardOptional = boardRepository.findById((long) cid);
        if (boardOptional.isPresent()) {
            BoardEntity boardEntity = boardOptional.get();
            int commentCount = boardEntity.getComment_count();
            if (commentCount > 0) {
                boardEntity.setComment_count(commentCount - 1);
                boardRepository.save(boardEntity);
                return true;
            }
        }
        return false;
    }
    @Transactional
    public boolean decrementNoticeCommentCount(int nid) {
        Optional<NoticeEntity> NoticeOptional = noticeRepository.findById((long) nid);
        if (NoticeOptional.isPresent()) {
            NoticeEntity noticeEntity = NoticeOptional.get();
            int commentCount = noticeEntity.getComment_count();
            if (commentCount > 0) {
                noticeEntity.setComment_count(commentCount - 1);
                noticeRepository.save(noticeEntity);
                return true;
            }
        }
        return false;
    }
    @Transactional
    public List<Map<String, Object>> searchPostsByCidAndTitle(long cid, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "cid");
        List<BoardEntity> boards;
        Long lastCid;  // 현재 트랜잭션 내에서의 가장 큰 cid 값을 기록하기 위한 변수
        Pageable pageable = PageRequest.of(0, 7, sort);
        Page<BoardEntity> boardPage = boardRepository.findAll(pageable);
        if (cid == 0) {
            // 처음 요청 시, 가장 마지막 10개의 데이터를 가져옴

            if ("all".equalsIgnoreCase(search)) {
                // If the search term is "all", retrieve all data
                boardPage = boardRepository.findAll(pageable);
            } else {
                // If a search term is provided, retrieve data where the title or body contains the search term
                boardPage = boardRepository.findByTitleIgnoreCaseContainingOrBodyIgnoreCaseContaining(search, search, pageable);
            }
            boards = boardPage.getContent();
        } else {
            // cid 값을 기준으로 그 이전 10개의 데이터를 가져옴\
            // 만약 현재 저장된 데이터 중 cid 값이 cid보다 작은 값이 있는 경우에만 데이터를 가져옴

            // 검색어가 주어진 경우, 해당 검색어를 제목 또는 내용에 포함하는 게시물을 추가로 가져옴
            if (!"all".equalsIgnoreCase(search)) {
                boardPage = boardRepository.findByCidLessThanAndTitleContainingOrBodyContainingOrderByCidDesc(cid, search, search, pageable);
            }else {
                boardPage= boardRepository.findByCidLessThanOrderByCidDesc(cid, sort, PageRequest.of(0, 7));
            }
            boards = boardPage.getContent();
        }
        List<Map<String, Object>> contentList = new ArrayList<>();
        for (BoardEntity board : boards) {
            List<String> imageUrls = new ArrayList<>();

            for (BoardFileEntity fileEntity : board.getBoardFileEntityList()) {
                String imageUrl = "https://spider-easy-vulture.ngrok-free.app/file/" + fileEntity.getOriginalFileName();
                imageUrls.add(imageUrl);
            }
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("cid", board.getCid());
            contentMap.put("title", board.getTitle());
            contentMap.put("writer", board.getWriter());
            contentMap.put("body", board.getBody());
            contentMap.put("comment_count", board.getComment_count());
            contentMap.put("write_time", board.getWrite_time());
            contentMap.put("image", imageUrls);
            contentList.add(contentMap);
        }
        return contentList;
    }
    @Transactional
    public List<Map<String, Object>> getAllNoticesWithImages(long nid, String search) {
        Sort sort = Sort.by(Sort.Direction.DESC, "nid"); // write_time 열을 기준으로 내림차순으로 정렬

        List<NoticeEntity> notices;
        Pageable pageable = PageRequest.of(0, 7, sort);
        Page<NoticeEntity> boardPage = noticeRepository.findAll(pageable);
        if (nid == 0) {
            // 처음 요청 시, 가장 마지막 10개의 데이터를 가져옴

            if ("all".equalsIgnoreCase(search)) {
                // If the search term is "all", retrieve all data
                boardPage = noticeRepository.findAll(pageable);
            } else {
                // If a search term is provided, retrieve data where the title or body contains the search term
                boardPage = noticeRepository.findByTitleIgnoreCaseContainingOrBodyIgnoreCaseContaining(search, search, pageable);
            }
            notices = boardPage.getContent();
        } else {
            // cid 값을 기준으로 그 이전 10개의 데이터를 가져옴\
            // 만약 현재 저장된 데이터 중 cid 값이 cid보다 작은 값이 있는 경우에만 데이터를 가져옴
            // 검색어가 주어진 경우, 해당 검색어를 제목 또는 내용에 포함하는 게시물을 추가로 가져옴
            if (!"all".equalsIgnoreCase(search)) {
                boardPage = noticeRepository.findByNidLessThanAndTitleContainingOrBodyContainingOrderByNidDesc(nid, search, search, pageable);
            }else {
                boardPage= noticeRepository.findByNidLessThanOrderByNidDesc(nid, sort, PageRequest.of(0, 7));
            }
            notices = boardPage.getContent();
        }
        List<Map<String, Object>> contentList = new ArrayList<>();
        for (NoticeEntity notice : notices) {
            List<String> imageUrls = new ArrayList<>();
            for (NoticeFileEntity fileEntity : notice.getNoticeFileEntities()) {
                String imageUrl = "https://spider-easy-vulture.ngrok-free.app/file/" + fileEntity.getOriginalFileName();
                imageUrls.add(imageUrl);
            }
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("nid", notice.getNid());
            contentMap.put("title", notice.getTitle());
            contentMap.put("body", notice.getBody());
            contentMap.put("comment_count", notice.getComment_count());
            contentMap.put("write_time", notice.getWrite_time());
            contentMap.put("image", imageUrls);
            contentList.add(contentMap);
        }
        return contentList;
    }
    @Transactional
    public boolean updateNotice(BoardDTO boardDTO, List<MultipartFile> images) {
        Optional<NoticeEntity> optionalBoard = noticeRepository.findById((long) boardDTO.getNid());
        if (optionalBoard.isPresent()) {
            NoticeEntity noticeEntity = optionalBoard.get();

            // 게시물 내용 수정
            noticeEntity.setTitle(boardDTO.getTitle());
            noticeEntity.setBody(boardDTO.getBody());
            if (images != null && !images.isEmpty()) {
                // 이미지가 업데이트되어야 하는 경우
                List<NoticeFileEntity> newImageEntities = new ArrayList<>();
                for (MultipartFile newImage : images) {
                    String newStoredFileName = "C:/springboot_img/images/" + newImage.getOriginalFilename();
                    File newFile = new File(newStoredFileName);
                    try {
                        newImage.transferTo(newFile);
                        NoticeFileEntity newImageEntity = new NoticeFileEntity();
                        newImageEntity.setOriginalFileName(newImage.getOriginalFilename());
                        newImageEntity.setStoredFileName(newStoredFileName);
                        newImageEntity.setNoticeEntity(noticeEntity);
                        newImageEntities.add(newImageEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 기존 이미지 삭제
                List<NoticeFileEntity> existingImages = noticeFileRepository.findByNoticeEntity(noticeEntity);
                for (NoticeFileEntity existingImage : existingImages) {
                    if (!newImageEntities.isEmpty()) {
                        // 새 이미지가 있을 때만 삭제
                        noticeFileRepository.delete(existingImage);
                        File existingImageFile = new File(existingImage.getStoredFileName());
                        existingImageFile.delete();
                    }
                }
                // 새 이미지 저장
                noticeFileRepository.saveAll(newImageEntities);
            }

            return true; // 수정 성공
        }
        return false; // 수정 실패
    }
    @Transactional
    public boolean deleteNotice(BoardDTO boardDTO) {
        Optional<NoticeEntity> optionalBoard = noticeRepository.findById((long) boardDTO.getNid());
        if (optionalBoard.isPresent()) {
            NoticeEntity noticeEntity = optionalBoard.get();
                // 댓글 삭제
                noticeCommentRepository.deleteByNoticeEntity(noticeEntity);
                // 게시물과 연결된 이미지 파일들을 삭제
                List<NoticeFileEntity> noticeFileEntities = noticeFileRepository.findByNoticeEntity(noticeEntity);
                for (NoticeFileEntity noticeFilee : noticeFileEntities) {
                    String storedFileName = noticeFilee.getStoredFileName();
                    File file = new File(storedFileName);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                noticeFileRepository.deleteAll(noticeFileEntities);
                // 게시물 삭제
                noticeRepository.delete(noticeEntity);
                return true; // 삭제 성공
            } else {
                return false; // 삭제 실패
            }
            // 게시물을 가져옴
    }
}




