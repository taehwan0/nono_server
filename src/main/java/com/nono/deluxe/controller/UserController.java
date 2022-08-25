package com.nono.deluxe.controller;

import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<User>> readUserList() {
        try {
            List<User> userList = userService.readUserList();
            return ResponseEntity.status(HttpStatus.OK).body(userList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody String temp) {
        User user = userService.createUser(temp);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
