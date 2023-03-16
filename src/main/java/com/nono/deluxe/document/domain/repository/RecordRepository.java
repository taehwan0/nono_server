package com.nono.deluxe.document.domain.repository;

import com.nono.deluxe.document.domain.Record;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId "
        + "AND r.document.date > :date "
        + "ORDER BY r.document.date ASC, r.document.createdAt ASC, r.id ASC")
    List<Record> findAllAfterThan(@Param("productId") long productId, @Param("date") LocalDate date);

    @Query(value = "UPDATE record  r "
        + "INNER JOIN document d "
        + "ON r.document_id = d.id "
        + "SET r.stock = r.stock + :updateStock "
        + "WHERE r.product_id = :productId "
        + "AND d.date > :date",
        nativeQuery = true)
    void updateAllStockAfterThan(
        @Param("productId") long productId,
        @Param("date") LocalDate date,
        @Param("updateStock") long updateStock);

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId "
        + "AND r.document.id = :documentId")
    Optional<Record> findAllByProductIdAndDocumentId(
        @Param("productId") long productId,
        @Param("documentId") long documentId);

    List<Record> findByDocumentId(long documentId);

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.product.id = :productId "
        + "AND r.document.date BETWEEN :fromMonth AND :toMonth "
        + "ORDER BY r.document.date DESC, r.document.createdAt DESC, r.id DESC")
    List<Record> findAllByProductBetween(
        @Param("productId") long productId,
        @Param("fromMonth") LocalDate fromMonth,
        @Param("toMonth") LocalDate toMonth);

    @Query(value = "SELECT r.stock "
        + "FROM document d INNER JOIN record r ON d.id = r.document_id "
        + "WHERE r.product_id = :productId "
        + "AND d.date < :date "
        + "ORDER BY d.date DESC, d.created_at DESC, d.id DESC "
        + "LIMIT 1;",
        nativeQuery = true)
    Optional<Long> findRecentStockByProductId(
        @Param("date") LocalDate date,
        @Param("productId") long productId);

    @Query("SELECT r "
        + "FROM Record r "
        + "WHERE r.document.date BETWEEN :fromDate AND :toDate "
        + "AND r.product.id = :productId ")
    List<Record> getRecordsByProductIdBetweenDates(
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("productId") long productId);
}
