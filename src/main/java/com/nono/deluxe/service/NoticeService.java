package com.nono.deluxe.service;

import com.nono.deluxe.controller.notice.dto.CreateNoticeRequestDto;
import com.nono.deluxe.controller.notice.dto.NoticeResponseDto;
import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.notice.NoticeRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeResponseDto createNotice(String title, String content, boolean onFocused) {

        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .onFocused(onFocused)
                .writer(userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Not Found User")))
                .build();

         return new NoticeResponseDto(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseDto> readNoticeList() {
        List<Notice> noticeList = noticeRepository.findAll();
        List<NoticeResponseDto> responseDtoList = new ArrayList<>();

        for (Notice notice : noticeList) {
            responseDtoList.add(new NoticeResponseDto(notice));
        }

        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public NoticeResponseDto readNotice(long id) {
        Notice notice = noticeRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Not Found Notice"));
        return new NoticeResponseDto(notice);
    }

    @Transactional
    public NoticeResponseDto updateNotice(long id, String title, String content, boolean onFocus) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found Notice"));
        notice.updateNoticeContents(title, content, onFocus);
        return new NoticeResponseDto(notice);
    }

    @Transactional
    public void deleteNotice(long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found Notice"));
        noticeRepository.delete(notice);
    }
}
