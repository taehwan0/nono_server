package com.nono.deluxe.presentation;

import com.nono.deluxe.application.client.TokenClient;
import com.nono.deluxe.application.service.UserService;
import com.nono.deluxe.configuration.annotation.Auth;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.user.CreateParticipantRequestDTO;
import com.nono.deluxe.presentation.dto.user.DeleteMeRequestDTO;
import com.nono.deluxe.presentation.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.presentation.dto.user.UpdatePasswordRequestDTO;
import com.nono.deluxe.presentation.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.presentation.dto.user.UserResponseDTO;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final TokenClient tokenClient;

    @Auth(role = Role.ROLE_MANAGER)
    @PostMapping()
    public ResponseEntity<UserResponseDTO> createParticipant(
        @Validated @RequestBody CreateParticipantRequestDTO createParticipantRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.createParticipant(createParticipantRequestDTO));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @GetMapping()
    public ResponseEntity<GetUserListResponseDTO> getUserList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page) {
        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserList(pageRequest, query));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable(name = "userId") long userID) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserById(userID));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable(name = "userId") long userId,
        @Validated @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.updateUser(userId, updateUserRequestDTO));
    }

    @Auth(role = Role.ROLE_ADMIN)
    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponseDTO> deleteParticipant(@PathVariable(name = "userId") long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.deleteParticipant(userId));
    }

    @Auth
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(
        @RequestHeader(value = "Authorization") String token) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserById(userId));
    }

    @Auth
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.updateMe(userId, updateUserRequestDTO));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @PutMapping("/me/password")
    public ResponseEntity<MessageResponseDTO> updatePassword(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.updatePassword(userId, updatePasswordRequestDTO));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @DeleteMapping("/me")
    public ResponseEntity<MessageResponseDTO> deleteMe(
        @RequestHeader(value = "Authorization") String token,
        @RequestBody DeleteMeRequestDTO deleteMeRequestDTO) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.deleteMe(userId, deleteMeRequestDTO));
    }
}
