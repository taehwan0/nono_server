package com.nono.deluxe.domain.temprecord;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRecordRepository extends JpaRepository<TempRecord, Long> {

    List<TempRecord> findByDocumentId(long documentId);
}
