package com.nono.deluxe.controller;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.user.AddUserRequestDTO;
import com.nono.deluxe.controller.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.controller.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.controller.dto.user.UserResponseDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 사용자 생성 - Participant 추가.
    @PostMapping()
    public ResponseEntity<UserResponseDTO> addUser(@
        RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody AddUserRequestDTO userRequestDTO) {
        authService.validateManagerToken(token);

        UserResponseDTO responseDTO = userService.addUser(userRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);

    }

    // 유저 리스트 조회.
    @GetMapping()
    public ResponseEntity<GetUserListResponseDTO> readUserList(
        @RequestHeader(value = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "userName") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page) {
        authService.validateManagerToken(token);

        GetUserListResponseDTO userList = userService.readUserList(query, column, order, size, (page - 1));

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> getUserInfo(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode) {
        authService.validateManagerToken(token);

        UserResponseDTO responseDTO = userService.getUserInfo(userCode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode,
        @Validated @RequestBody UpdateUserRequestDTO userRequestDTO) {
        authService.validateManagerToken(token);

        UserResponseDTO responseDTO = userService.updateUser(userCode, userRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<MessageResponseDTO> deleteUser(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode) {
        authService.validateAdminToken(token);

        MessageResponseDTO responseDto = userService.deleteUser(userCode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
