package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.domain.document.legacy.LegacyDocument;
import com.nono.deluxe.domain.document.legacy.LegacyDocumentRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.domain.record.legacy.LegacyRecord;
import com.nono.deluxe.domain.record.legacy.LegacyRecordRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LegacyDocumentService {

    private final LegacyDocumentRepository legacyDocumentRepository;
    private final LegacyRecordRepository legacyRecordRepository;
    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void importLegacyDocument() {
        List<LegacyDocument> legacyDocuments = legacyDocumentRepository.findAll();

        long index = 0;
        for (LegacyDocument legacyDocument : legacyDocuments) {
            Document document = transLegacyDocument(legacyDocument);
            documentRepository.save(document);

            List<LegacyRecord> legacyRecords = legacyRecordRepository.findAllByDocsCode(legacyDocument.getDocsCode());

            legacyRecords.stream()
                .map(record -> transLegacyRecord(record, document))
                .forEach(recordRepository::save);

            legacyDocumentRepository.delete(legacyDocument);
            legacyRecordRepository.deleteAll(legacyRecords);

            log.info("document: {} of {} record", document, legacyRecords.size());
            log.info("running: {} / {}", ++index, legacyDocuments.size());
        }
    }

    private Document transLegacyDocument(LegacyDocument legacyDocument) {
        Company company = companyRepository.findByName(legacyDocument.getCompanyName())
            .orElse(companyRepository.findById(1).get());

        User writer = userRepository.findByName(legacyDocument.getCompanyName())
            .orElse(userRepository.findByName("taehwan")
                .orElseThrow(() -> new NotFoundException("")));

        return Document.of(legacyDocument, writer, company);
    }

    private Record transLegacyRecord(LegacyRecord legacyRecord, Document document) {
        Product product = productRepository.findByProductCode(legacyRecord.getPrdCode())
            .orElseThrow(() -> new NotFoundException("NotFoundProduct"));

        return Record.of(document, product, legacyRecord);
    }
}
