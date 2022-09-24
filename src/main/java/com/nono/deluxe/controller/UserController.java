package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.user.AddUserRequestDTO;
import com.nono.deluxe.controller.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.controller.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.controller.dto.user.UserResponseDTO;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@RequestBody String temp) {
        User user = userService.createAdmin(temp);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/participant")
    public ResponseEntity<User> createParticipant(@RequestBody String temp) {
        User user = userService.createParticipant(temp);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // 사용자 생성 - Participant 추가.
    @PostMapping()
    public ResponseEntity<UserResponseDTO> addUser(@RequestHeader(value = "Authorization") String token,
                                                   @RequestBody AddUserRequestDTO userRequestDTO) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if (authService.isAdmin(jwt) || authService.isManager(jwt)) {
                UserResponseDTO responseDTO = userService.addUser(userRequestDTO);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(responseDTO);
            } else {
                log.error("User: Forbidden");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 유저 리스트 조회.
    @GetMapping()
    public ResponseEntity<GetUserListResponseDTO> readUserList(@RequestHeader(value = "Authorization") String token,
                                                               @RequestParam(value = "query", defaultValue = "") String query,
                                                               @RequestParam(required = false, defaultValue = "userName") String column,
                                                               @RequestParam(required = false, defaultValue = "ASC") String order,
                                                               @RequestParam(required = false, defaultValue = "10") int size,
                                                               @RequestParam(required = false, defaultValue = "1") int page) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                GetUserListResponseDTO userList = userService.readUserList(query, column, order, size, (page - 1));
                return ResponseEntity.status(HttpStatus.OK).body(userList);
            } else {
                log.error("User: forbidden");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> getUserInfo(@RequestHeader(value = "Authorization") String token,
                                                       @PathVariable(name = "userCode") long userCode) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                UserResponseDTO responseDTO = userService.getUserInfo(userCode);
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            }else {
                log.error("User: forbidden");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestHeader(value = "Authorization") String token,
                                                      @PathVariable(name = "userCode") long userCode,
                                                      @RequestBody UpdateUserRequestDTO userRequestDTO) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isManager(jwt) || authService.isAdmin(jwt)) {
                UserResponseDTO responseDTO = userService.updateUser(userCode, userRequestDTO);
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                log.error("User: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<MessageResponseDTO> deleteUser(@RequestHeader(name = "Authorization") String token,
                                                         @PathVariable(name = "userCode") long userCode) {
        try {
            DecodedJWT jwt = authService.decodeAccessToken(token);
            if(authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                log.info("User: {} user is deleted By {}", userCode, userId);
                MessageResponseDTO responseDto = userService.deleteUser(userCode);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("User: forbidden");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
