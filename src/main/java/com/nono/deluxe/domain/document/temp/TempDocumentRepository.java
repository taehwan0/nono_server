package com.nono.deluxe.domain.document.temp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempDocumentRepository extends JpaRepository<TempDocument, Long> {

    @Query("SELECT d FROM TempDocument d WHERE d.company.name like concat('%', :query,'%')")
    Page<TempDocument> readTempDocumentList(@Param("query") String query, Pageable limit);
}
