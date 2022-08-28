package com.nono.deluxe.domain.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT u FROM Record u WHERE u.product_id = :productId ")
    List<Record> findRecordList(@Param("productId") long productId);
}
