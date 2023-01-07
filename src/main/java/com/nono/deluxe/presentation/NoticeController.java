package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.NoticeService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.notice.CreateNoticeRequestDTO;
import com.nono.deluxe.presentation.dto.notice.NoticeResponseDTO;
import com.nono.deluxe.presentation.dto.notice.ReadNoticeListResponseDTO;
import com.nono.deluxe.presentation.dto.notice.UpdateNoticeRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<NoticeResponseDTO> createNotice(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody CreateNoticeRequestDTO createNoticeRequestDTO) {
        long userId = authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.createNotice(userId, createNoticeRequestDTO));
    }

    @GetMapping("")
    public ResponseEntity<ReadNoticeListResponseDTO> getNoticeList(
        @RequestHeader(value = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "createdAt") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean focus,
        @RequestParam(required = false, defaultValue = "false") boolean content) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getNoticeList(pageRequest, query, focus, content));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeById(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "noticeId") long noticeId) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getNoticeById(noticeId));
    }

    @GetMapping("/recent")
    public ResponseEntity<NoticeResponseDTO> getRecentNotice(
        @RequestHeader(value = "Authorization") String token) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getRecentNotice());
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> updateNotice(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "noticeId") long noticeId,
        @Validated @RequestBody UpdateNoticeRequestDTO updateNoticeRequestDTO) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.updateNotice(noticeId, updateNoticeRequestDTO));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<MessageResponseDTO> deleteNotice(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "noticeId") long noticeId) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.deleteNotice(noticeId));
    }
}
