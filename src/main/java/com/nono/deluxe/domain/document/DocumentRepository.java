package com.nono.deluxe.domain.document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d WHERE d.company.name like concat('%', :query,'%')")
    Page<Document> readDocumentList(@Param("query") String query, Pageable limit);
}
