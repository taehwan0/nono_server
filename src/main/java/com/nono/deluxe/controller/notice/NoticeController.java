package com.nono.deluxe.controller.notice;

import com.nono.deluxe.controller.notice.dto.NoticeResponseDto;
import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.controller.notice.dto.CreateNoticeRequestDto;
import com.nono.deluxe.controller.notice.dto.UpdateNoticeRequestDto;
import com.nono.deluxe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/notice")
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestBody CreateNoticeRequestDto requestDto) {
        try {
            NoticeResponseDto responseDto = noticeService.createNotice(
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isOnFocused());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> readNoticeList() {
        List<NoticeResponseDto> responseDtoList = noticeService.readNoticeList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDtoList);
    }

    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> readNotice(@PathVariable(name = "id")long id) {
        try {
            NoticeResponseDto responseDto = noticeService.readNotice(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PutMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable(name = "id")long id,
                                               @RequestBody UpdateNoticeRequestDto requestDto) {
        try {
            NoticeResponseDto responseDto = noticeService.updateNotice(
                    id,
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.isOnFocused());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable(name = "id")long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Notice Deleted");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
