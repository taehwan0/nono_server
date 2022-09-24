package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.notice.*;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;
    private final AuthService authService;

    /**
     * 필요 권한: admin
     * @param token
     * @param requestDto
     * @return
     */
    @PostMapping("/notice")
    public ResponseEntity<NoticeResponseDTO> createNotice(@RequestHeader(value = "Authorization") String token,
                                                                @RequestBody CreateNoticeRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                NoticeResponseDTO responseDto = noticeService.createNotice(userId, requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden create notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @return
     */
    @GetMapping("/notice")
    public ResponseEntity<ReadNoticeListResponseDTO> readNoticeList(@RequestHeader(value = "Authorization") String token,
                                                                    @RequestParam(required = false, defaultValue = "") String query,
                                                                    @RequestParam(required = false, defaultValue = "createdAt") String column,
                                                                    @RequestParam(required = false, defaultValue = "DESC") String order,
                                                                    @RequestParam(required = false, defaultValue = "10") int size,
                                                                    @RequestParam(required = false, defaultValue = "1") int page,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean focus,
                                                                    @RequestParam(required = false, defaultValue = "false") boolean content) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ReadNoticeListResponseDTO responseDto  = noticeService.readNoticeList(query, column, order, size, (page - 1), focus, content);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden read notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @param noticeId
     * @return
     */
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> readNotice(@RequestHeader(value = "Authorization") String token,
                                                        @PathVariable(name = "noticeId") long noticeId) {
        try{
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                NoticeResponseDTO responseDto = noticeService.readNotice(noticeId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden read notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @return
     */
    @GetMapping("/notice/recent")
    public ResponseEntity<NoticeResponseDTO> readNoticeRecent(@RequestHeader(value = "Authorization") String token) {
        try{
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                NoticeResponseDTO responseDto = noticeService.readNoticeRecent();

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden read notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param noticeId
     * @param requestDto
     * @return
     */
    @PutMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> updateNotice(@RequestHeader(value = "Authorization") String token,
                                                                @PathVariable(name = "noticeId") long noticeId,
                                                                @RequestBody UpdateNoticeRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isAdmin(jwt)) {
                NoticeResponseDTO responseDto = noticeService.updateNotice(noticeId, requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden update notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param noticeId
     * @return
     */
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<MessageResponseDTO> deleteNotice(@RequestHeader(value = "Authorization") String token,
                                                           @PathVariable(name = "noticeId") long noticeId) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isAdmin(jwt)) {
                MessageResponseDTO responseDto = noticeService.deleteNotice(noticeId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Notice: forbidden delete notice {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
