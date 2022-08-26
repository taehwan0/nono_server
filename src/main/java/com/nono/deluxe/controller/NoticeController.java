package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.notice.NoticeResponseDto;
import com.nono.deluxe.controller.dto.notice.CreateNoticeRequestDto;
import com.nono.deluxe.controller.dto.notice.UpdateNoticeRequestDto;
import com.nono.deluxe.service.AuthService;
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
    private final AuthService authService;

    /**
     * 필요 권한: admin
     * @param token
     * @param requestDto
     * @return
     */
    @PostMapping("/notice")
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestHeader(value = "Authorization") String token,
                                                          @RequestBody CreateNoticeRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                NoticeResponseDto responseDto = noticeService.createNotice(
                        requestDto.getTitle(),
                        requestDto.getContent(),
                        requestDto.isOnFocused());

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDto);
            } else {
                log.error("Notice: forbidden create notice {}", jwt.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @return
     */
    @GetMapping("/notice")
    public ResponseEntity<List<NoticeResponseDto>> readNoticeList(@RequestHeader(value = "Authorization") String token) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                List<NoticeResponseDto> responseDtoList = noticeService.readNoticeList();

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDtoList);
            } else {
                log.error("Notice: forbidden read notice {}", jwt.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @param id
     * @return
     */
    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> readNotice(@RequestHeader(value = "Authorization") String token,
                                                        @PathVariable(name = "id")long id) {
        try{
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                NoticeResponseDto responseDto = noticeService.readNotice(id);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDto);
            } else {
                log.error("Notice: forbidden read notice {}", jwt.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param id
     * @param requestDto
     * @return
     */
    @PutMapping("/notice/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@RequestHeader(value = "Authorization") String token,
                                                          @PathVariable(name = "id")long id,
                                                          @RequestBody UpdateNoticeRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                NoticeResponseDto responseDto = noticeService.updateNotice(
                        id,
                        requestDto.getTitle(),
                        requestDto.getContent(),
                        requestDto.isOnFocused());

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDto);
            } else {
                log.error("Notice: forbidden update notice {}", jwt.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param id
     * @return
     */
    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> deleteNotice(@RequestHeader(value = "Authorization") String token,
                                               @PathVariable(name = "id")long id) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                noticeService.deleteNotice(id);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Notice Deleted");
            } else {
                log.error("Notice: forbidden delete notice {}", jwt.getId());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
