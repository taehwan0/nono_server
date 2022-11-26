package com.nono.deluxe.domain.notice;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query(value = "SELECT n FROM Notice n WHERE n.title LIKE concat('%', :query, '%')")
    Page<Notice> readNoticeList(@Param("query") String query,
        Pageable limit);

    @Query(value = "SELECT n FROM Notice n WHERE n.title LIKE concat('%', :query, '%') and n.focus = true")
    Page<Notice> readNoticeListFocus(@Param("query") String query,
        Pageable limit);

    @Query(value = "SELECT * FROM notice as n ORDER BY n.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Notice> readNoticeRecentOne();
}
