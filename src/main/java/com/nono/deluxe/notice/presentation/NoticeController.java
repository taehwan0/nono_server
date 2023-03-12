package com.nono.deluxe.notice.presentation;

import com.nono.deluxe.auth.application.TokenClient;
import com.nono.deluxe.common.configuration.annotation.Auth;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.notice.application.NoticeService;
import com.nono.deluxe.notice.presentation.dto.notice.CreateNoticeRequestDTO;
import com.nono.deluxe.notice.presentation.dto.notice.NoticeResponseDTO;
import com.nono.deluxe.notice.presentation.dto.notice.ReadNoticeListResponseDTO;
import com.nono.deluxe.notice.presentation.dto.notice.UpdateNoticeRequestDTO;
import com.nono.deluxe.user.domain.Role;
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
    private final TokenClient tokenClient;

    @Auth(role = Role.ROLE_ADMIN)
    @PostMapping("")
    public ResponseEntity<NoticeResponseDTO> createNotice(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody CreateNoticeRequestDTO createNoticeRequestDTO) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.createNotice(userId, createNoticeRequestDTO));
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<ReadNoticeListResponseDTO> getNoticeList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "createdAt") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean focus,
        @RequestParam(required = false, defaultValue = "false") boolean content) {
        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getNoticeList(pageRequest, query, focus, content));
    }

    @Auth
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeById(
        @PathVariable(name = "noticeId") long noticeId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getNoticeById(noticeId));
    }

    @Auth
    @GetMapping("/recent")
    public ResponseEntity<NoticeResponseDTO> getRecentNotice() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.getRecentNotice());
    }

    @Auth(role = Role.ROLE_ADMIN)
    @PutMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> updateNotice(
        @PathVariable(name = "noticeId") long noticeId,
        @Validated @RequestBody UpdateNoticeRequestDTO updateNoticeRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.updateNotice(noticeId, updateNoticeRequestDTO));
    }

    @Auth(role = Role.ROLE_ADMIN)
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<MessageResponseDTO> deleteNotice(
        @PathVariable(name = "noticeId") long noticeId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(noticeService.deleteNotice(noticeId));
    }
}
