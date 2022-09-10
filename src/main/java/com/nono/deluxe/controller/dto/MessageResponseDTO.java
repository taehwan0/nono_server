package com.nono.deluxe.controller.dto;

import lombok.Data;

/**
 * hj.yang
 * 해당 함수가 DeleteAPI에서만 사용되는지 확인 바랍니다.
 * 만약 result / message 데이터만 들어가는 부분이 DeleteAPI 가 아니라 하더라도 사용될 수 있는 여지가 있다면
 * 클래스명은 변경해도 좋지 않을까 싶네요.
 */
@Data
public class MessageResponseDTO {

    private boolean result;
    private String message;

    public MessageResponseDTO(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
