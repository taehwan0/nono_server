package com.nono.deluxe.domain.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT r FROM Record r WHERE r.product.id = :productId AND r.document.date > :date ORDER BY r.document.date ASC, r.document.createdAt ASC")
    List<Record> findFutureDateRecordList(@Param("productId") long productId, @Param("date") LocalDate date);

    @Query(value = "UPDATE record  r INNER JOIN document d ON r.document_id = d.id SET r.stock = r.stock + :updateStock WHERE r.product_id = :productId AND d.date > :date", nativeQuery = true)
    void updateStockFutureDateRecord(@Param("productId") long productId, @Param("date") LocalDate date, @Param("updateStock") long updateStock);

    /**
     * productId, documentId 로 record 를 특정해서 반환
     * @param productId
     * @param documentId
     * @return Optional<Record>
     */
    @Query("SELECT r From Record r WHERE r.product.id = :productId AND r.document.id = :documentId")
    Optional<Record> findUpdateTargetRecord(@Param("productId") long productId, @Param("documentId") long documentId);

    List<Record> findByDocumentId(long documentId);
}
