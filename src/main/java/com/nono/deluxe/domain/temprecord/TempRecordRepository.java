package com.nono.deluxe.domain.temprecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempRecordRepository extends JpaRepository<TempRecord, Long> {
    List<TempRecord> findByDocumentId(long documentId);
}
