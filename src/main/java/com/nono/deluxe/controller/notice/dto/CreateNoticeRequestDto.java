package com.nono.deluxe.controller.notice.dto;

import com.nono.deluxe.domain.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDto {
    private String writer;
    private String title;
    private String content;
    private boolean onFocus;

    public Notice toEntity() {
        return Notice.builder()
                .title(title)
                .writer(writer)
                .content(content)
                .build();
    }
}
