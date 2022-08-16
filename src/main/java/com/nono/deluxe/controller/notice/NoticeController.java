package com.nono.deluxe.controller.notice;

import com.nono.deluxe.auth.AuthManager;
import com.nono.deluxe.controller.notice.dto.NoticeResponseDto;
import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.controller.notice.dto.CreateNoticeRequestDto;
import com.nono.deluxe.controller.notice.dto.UpdateNoticeRequestDto;
import com.nono.deluxe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthManager authManager;

    @PostMapping("/notice")
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestHeader(value = "Authorization") String token,
                                                          @RequestBody CreateNoticeRequestDto requestDto) {
        try {
            authManager.isUser(token);
            NoticeResponseDto responseDto = noticeService.createNotice(
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isOnFocused());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> readNoticeList(@RequestHeader(value = "Authorization") String token) {
        try {
            authManager.isUser(token);
            List<NoticeResponseDto> responseDtoList = noticeService.readNoticeList();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDtoList);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> readNotice(@RequestHeader(value = "Authorization") String token,
                                                        @PathVariable(name = "id")long id) {
        try{
            authManager.isUser(token);
            NoticeResponseDto responseDto = noticeService.readNotice(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PutMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@RequestHeader(value = "Authorization") String token,
                                                          @PathVariable(name = "id")long id,
                                                          @RequestBody UpdateNoticeRequestDto requestDto) {
        try {
            authManager.isUser(token);
            NoticeResponseDto responseDto = noticeService.updateNotice(
                    id,
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isOnFocused());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> deleteNotice(@RequestHeader(value = "Authorization") String token,
                                               @PathVariable(name = "id")long id) {
        try {
            authManager.isUser(token);
            noticeService.deleteNotice(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Notice Deleted");
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
