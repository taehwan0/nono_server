package com.nono.deluxe.service;

import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public Notice createNotice(String writer, String title, String content, boolean onFocus) {
        Notice newNotice = Notice.builder()
                .writer(writer)
                .title(title)
                .content(content)
                .onFocus(onFocus)
                .build();

        return noticeRepository.save(newNotice);
    }

    @Transactional(readOnly = true)
    public List<Notice> readNoticeList() {
        return noticeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Notice readNotice(long id) {
        return noticeRepository.findById(id).
                orElseThrow(() -> new RuntimeException("Not Found Notice"));
    }

    @Transactional
    public Notice updateNotice(long id, String title, String content, boolean onFocus) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found Notice"));
        notice.updateNoticeContents(title, content, onFocus);
        return notice;
    }

    @Transactional
    public void deleteNotice(long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not Found Notice"));
        noticeRepository.delete(notice);
    }
}
