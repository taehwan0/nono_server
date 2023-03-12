package com.nono.deluxe.document.domain.legacy.repository;

import com.nono.deluxe.document.domain.legacy.LegacyRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyRecordRepository extends JpaRepository<LegacyRecord, Long> {

    List<LegacyRecord> findAllByDocsCode(long docsCode);
}
