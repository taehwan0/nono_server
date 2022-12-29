package com.nono.deluxe.domain.record;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId AND r.document.date > :date "
        + "ORDER BY r.document.date ASC, r.document.createdAt ASC")
    List<Record> findFutureDateRecordList(@Param("productId") long productId, @Param("date") LocalDate date);

    @Query(value = "UPDATE record  r "
        + "INNER JOIN document d ON r.document_id = d.id SET r.stock = r.stock + :updateStock "
        + "WHERE r.product_id = :productId AND d.date > :date", nativeQuery = true)
    void updateStockFutureDateRecord(@Param("productId") long productId, @Param("date") LocalDate date,
        @Param("updateStock") long updateStock);

    /**
     * productId, documentId 로 record 를 특정해서 반환
     *
     * @param productId
     * @param documentId
     * @return Optional<Record>
     */
    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId AND r.document.id = :documentId")
    Optional<Record> findUpdateTargetRecord(
        @Param("productId") long productId,
        @Param("documentId") long documentId);

    List<Record> findByDocumentId(long documentId);

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId AND r.document.date BETWEEN :fromMonth AND :toMonth "
        + "ORDER BY r.document.date DESC")
    List<Record> findByProductId(@Param("productId") long productId, @Param("fromMonth") LocalDate fromMonth,
        @Param("toMonth") LocalDate toMonth);

    @Query("SELECT r"
        + " FROM Record r"
        + " WHERE r.product.id = :productId "
        + "AND r.document.date BETWEEN :fromDate AND :toDate "
        + "ORDER BY r.document.date ASC")
    List<Record> findAllByProductIdAndDocumentDateBetween(@Param("productId") long productId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );
//
//    @Query(value = "SELECT * "
//        + "FROM record r INNER JOIN document d ON r.document_id = d.id", nativeQuery = true)
//    Optional<Record> findRecentStock(@Param("productId") long productId, @Param("date") LocalDate date);

    @Query(value = "SELECT r.stock "
        + "FROM document d INNER JOIN record r ON d.id = r.document_id "
        + "WHERE r.product_id = :productId "
        + "AND d.date < :date "
        + "ORDER BY d.date DESC, d.created_at DESC "
        + "LIMIT 1;", nativeQuery = true)
    Optional<Long> findRecentStock(
        @Param("date") LocalDate date,
        @Param("productId") long productId);

    @Query(value = "SELECT sum(r.quantity) "
        + "FROM record r INNER JOIN document d on r.document_id = d.id "
        + "WHERE r.product_id = :productId "
        + "AND d.type = :type "
        + "AND d.date = :date", nativeQuery = true)
    Optional<Long> sumTotalQuantityOfDate(
        @Param("date") LocalDate date,
        @Param("productId") long productId,
        @Param("type") String type
    );
}
