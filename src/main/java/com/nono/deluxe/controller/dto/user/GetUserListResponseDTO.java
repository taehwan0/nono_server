package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.user.User;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
@Data
public class GetUserListResponseDTO {
    private Meta meta;
    List<UserResponseDTO> userList = new ArrayList();

    public GetUserListResponseDTO(Page<User> userPage) {
        this.meta = new Meta(
                userPage.getNumber() + 1,
                userPage.getNumberOfElements(),
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.isLast()
        );

        List<User> userList = userPage.getContent();
        userList.forEach(user -> {
            this.userList.add(new UserResponseDTO(user));
        });
    }
}
