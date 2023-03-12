package com.nono.deluxe.document.domain.legacy.repository;

import com.nono.deluxe.document.domain.legacy.LegacyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyDocumentRepository extends JpaRepository<LegacyDocument, Long> {

}
