//package com.nono.deluxe.controller.notice;
//
//import com.nono.deluxe.domain.notice.Notice;
//import com.nono.deluxe.controller.notice.dto.CreateNoticeRequestDto;
//import com.nono.deluxe.controller.notice.dto.UpdateNoticeRequestDto;
//import com.nono.deluxe.service.NoticeService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@RestController
//public class NoticeController {
//
//    private final NoticeService noticeService;
//
//    @PostMapping("/notice")
//    public ResponseEntity<Notice> createNotice(@RequestBody CreateNoticeRequestDto requestDto) {
//        try {
//            Notice notice = noticeService.createNotice(requestDto);
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(notice);
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .build();
//        }
//    }
//
//    @GetMapping("/notice")
//    public ResponseEntity<List<Notice>> readNoticeList() {
//        List<Notice> noticeList = noticeService.readNoticeList();
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(noticeList);
//    }
//
//    @GetMapping("/notice/{id}")
//    public ResponseEntity<Notice> readNotice(@PathVariable(name = "id")long id) {
//        try {
//            Notice notice = noticeService.readNotice(id);
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(notice);
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .build();
//        }
//    }
//
//    @PutMapping("/notice/{id}")
//    public ResponseEntity<Notice> updateNotice(@PathVariable(name = "id")long id,
//                                               @RequestBody UpdateNoticeRequestDto requestDto) {
//        try {
//            Notice notice = noticeService.updateNotice(id,
//                    requestDto.getTitle(),
//                    requestDto.getContent(),
//                    requestDto.isOnFocus());
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body(notice);
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .build();
//        }
//    }
//
//    @DeleteMapping("/notice/{id}")
//    public ResponseEntity<String> deleteNotice(@PathVariable(name = "id")long id) {
//        try {
//            noticeService.deleteNotice(id);
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .body("Notice Deleted");
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .build();
//        }
//    }
//}
