package com.nono.deluxe.domain.record.legacy;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyRecordRepository extends JpaRepository<LegacyRecord, Long> {

    List<LegacyRecord> findAllByDocsCode(long docsCode);
}
