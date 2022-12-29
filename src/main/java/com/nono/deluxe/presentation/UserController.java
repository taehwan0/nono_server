package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.UserService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.user.CreateParticipantRequestDTO;
import com.nono.deluxe.presentation.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.presentation.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.presentation.dto.user.UserResponseDTO;
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
    public ResponseEntity<UserResponseDTO> addUser(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody CreateParticipantRequestDTO userRequestDTO) {
        authService.validateTokenOverManagerRole(token);

        UserResponseDTO responseDTO = userService.createParticipant(userRequestDTO);

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
        authService.validateTokenOverManagerRole(token);

        GetUserListResponseDTO userList = userService.getUserList(query, column, order, size, (page - 1));

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> getUserInfo(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode) {
        authService.validateTokenOverManagerRole(token);

        UserResponseDTO responseDTO = userService.getUserInfo(userCode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode,
        @Validated @RequestBody UpdateUserRequestDTO userRequestDTO) {
        authService.validateTokenOverManagerRole(token);

        UserResponseDTO responseDTO = userService.updateUser(userCode, userRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<MessageResponseDTO> deleteUser(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode) {
        authService.validateTokenOverAdminRole(token);

        MessageResponseDTO responseDto = userService.deleteUser(userCode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
