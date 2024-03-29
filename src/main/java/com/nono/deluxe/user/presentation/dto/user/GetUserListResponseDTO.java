package com.nono.deluxe.user.presentation.dto.user;

import com.nono.deluxe.common.presentation.dto.Meta;
import com.nono.deluxe.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

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
        userList.forEach(user -> this.userList.add(new UserResponseDTO(user)));
    }
}
