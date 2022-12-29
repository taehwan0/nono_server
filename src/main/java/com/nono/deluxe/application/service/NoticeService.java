package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.notice.NoticeRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.notice.CreateNoticeRequestDTO;
import com.nono.deluxe.presentation.dto.notice.NoticeResponseDTO;
import com.nono.deluxe.presentation.dto.notice.ReadNoticeListResponseDTO;
import com.nono.deluxe.presentation.dto.notice.UpdateNoticeRequestDTO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeResponseDTO createNotice(long userId, CreateNoticeRequestDTO requestDto) {
        User writer = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Notice: Not Found User"));
        Notice notice = requestDto.toEntity(writer);

        return new NoticeResponseDTO(noticeRepository.save(notice));
    }

    @Transactional(readOnly = true)
    public ReadNoticeListResponseDTO readNoticeList(
        String query,
        String column,
        String order,
        int size,
        int page,
        boolean focus,
        boolean content) {
        Pageable limit = PageRequest.of(page, size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        if (focus) {
            return new ReadNoticeListResponseDTO(noticeRepository.readNoticeListFocus(query, limit), content);
        }
        return new ReadNoticeListResponseDTO(noticeRepository.readNoticeList(query, limit), content);
    }

    @Transactional(readOnly = true)
    public NoticeResponseDTO readNotice(long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("Not Found Notice"));

        return new NoticeResponseDTO(notice);
    }

    @Transactional(readOnly = true)
    public NoticeResponseDTO readNoticeRecent() {
        Optional<Notice> notice = noticeRepository.readNoticeRecentOne();

        return notice.map(NoticeResponseDTO::new).orElseGet(NoticeResponseDTO::new);
    }

    /*
    save(update query) 이후에 @LastModifiedAt 실행이 되는데 @Transactional 때문에 동시 실행 되기 때문에 수정이 안됐음.
    saveAndFlush 로 명시적인 저장을 하면 @LastModifiedAt 이 정상적으로 동작함
     */
    @Transactional
    public NoticeResponseDTO updateNotice(long noticeId, UpdateNoticeRequestDTO requestDto) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("Not Found Notice"));

        notice.update(
            requestDto.getTitle(),
            requestDto.getContent(),
            requestDto.isFocus()
        );

        noticeRepository.saveAndFlush(notice);

        return new NoticeResponseDTO(notice);
    }

    @Transactional
    public MessageResponseDTO deleteNotice(long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new NotFoundException("Not Found Notice"));

        noticeRepository.delete(notice);

        return new MessageResponseDTO(true, "deleted");
    }
}
