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

    @Query("SELECT n "
        + "FROM Notice n "
        + "WHERE n.title LIKE concat('%', :query, '%')")
    Page<Notice> findPageByTitle(@Param("query") String query, Pageable pageable);

    @Query("SELECT n "
        + "FROM Notice n "
        + "WHERE n.title LIKE CONCAT('%', :query, '%') "
        + "AND n.focus = true")
    Page<Notice> findFocusPageByTitle(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT * FROM notice n ORDER BY n.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Notice> findRecent();
}
