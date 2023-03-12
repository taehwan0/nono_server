package com.nono.deluxe.document.domain.repository;

import com.nono.deluxe.document.domain.TempRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRecordRepository extends JpaRepository<TempRecord, Long> {

    List<TempRecord> findByDocumentId(long documentId);
}
