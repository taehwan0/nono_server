package com.nono.deluxe.service;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.controller.dto.tempDocument.CreateTempDocumentRequestDTO;
import com.nono.deluxe.controller.dto.tempDocument.ReadTempDocumentListResponseDTO;
import com.nono.deluxe.controller.dto.tempDocument.TempDocumentResponseDTO;
import com.nono.deluxe.controller.dto.tempDocument.UpdateTempDocumentRequestDTO;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.document.temp.TempDocumentRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.temprecord.TempRecord;
import com.nono.deluxe.domain.temprecord.TempRecordRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TempDocumentService {

    private final TempDocumentRepository tempDocumentRepository;
    private final TempRecordRepository tempRecordRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public TempDocumentResponseDTO createDocument(long userId, CreateTempDocumentRequestDTO requestDto) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not Found User"));
        Company company = companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Not Found Company"));
        TempDocument document = requestDto.toEntity(writer, company);
        tempDocumentRepository.save(document);

        List<RecordRequestDTO> recordRequestDtoList = requestDto.getRecordList();
        List<TempRecord> createdRecordList = new ArrayList<>();

        for (RecordRequestDTO recordRequestDto : recordRequestDtoList) {
            Product recordProductInfo = productRepository.findById(recordRequestDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Not Found product Info"));
            TempRecord tempRecord = recordRequestDto.toTempEntity(document, recordProductInfo);
            // temp Record 저장.

            long recordPrice = tempRecord.getPrice();
            if (recordPrice == 0) {
                long productPrice = recordProductInfo.getPrice();
                tempRecord.updatePrice(productPrice);
            }
            tempRecordRepository.save(tempRecord);
            createdRecordList.add(tempRecord);
        }

        long totalPrice = getTotalPrice(createdRecordList);
        long totalCount = createdRecordList.size();
        return new TempDocumentResponseDTO(document,
                totalCount,
                totalPrice,
                createdRecordList);
    }



    @Transactional(readOnly = true)
    public TempDocumentResponseDTO readDocument(long documentId) {
        TempDocument document = tempDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Not Found Document"));
        List<TempRecord> recordList = tempRecordRepository.findByDocumentId(documentId);

        long totalPrice = getTotalPrice(recordList);
        long totalCount = recordList.size();

        return new TempDocumentResponseDTO(document,
                totalCount,
                totalPrice,
                recordList);
    }

    @Transactional(readOnly = true)
    public ReadTempDocumentListResponseDTO readDocumentList(String query,
                                                        String column,
                                                        String order,
                                                        int size,
                                                        int page) {
        Pageable limit = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column),
                new Sort.Order(Sort.Direction.ASC, "createdAt")));

        Page<TempDocument> documentPage;
        if (query.isEmpty()) {
            documentPage = tempDocumentRepository.findAll(limit);
        } else {
             documentPage = tempDocumentRepository.readTempDocumentList(query, limit);
        }


        return new ReadTempDocumentListResponseDTO(documentPage);
    }

    @Transactional
    public TempDocumentResponseDTO updateDocument(long documentId,
                                              long userId,
                                              UpdateTempDocumentRequestDTO requestDto) {
        TempDocument document = tempDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Not Found Document"));

        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not Found User"));

        long companyId = requestDto.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Not Found Company"));

        TempDocument updatedDocument = updateDocumentInfo(document,
                writer,
                company,
                requestDto);

        List<TempRecord> updatedRecordList = updatedDocument.getDocumentItemList();
        long recordCount = updatedRecordList.size();
        long totalPrice = getTotalPrice(updatedRecordList);

        return new TempDocumentResponseDTO(updatedDocument,
                recordCount,
                totalPrice,
                updatedRecordList
                );


    }

    private TempDocument updateDocumentInfo(TempDocument tempDocument,
                                            User writer,
                                            Company company,
                                            UpdateTempDocumentRequestDTO requestDto) {
        // 기존 tempRecord 삭제
        List<TempRecord> originRecordList = tempDocument.getDocumentItemList();
        originRecordList.forEach(tempRecordRepository::delete);

        // 새로운 Record 데이터 등록
        List<TempRecord> updatedRecordList = new ArrayList<>();
        for (RecordRequestDTO recordRequestDTO : requestDto.getRecordList()) {
            // TODO: 해당 레코드 내역이 잘 들어가는지 확인 필요.
            Product recordProductInfo = productRepository.findById(recordRequestDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Not found product Info."));
            TempRecord tempRecord = recordRequestDTO.toTempEntity(tempDocument, recordProductInfo);
            updatedRecordList.add(tempRecord);
        }

        // 데이터 업데이트
        tempDocument.updateDocumentInfo(requestDto, writer, company, updatedRecordList);
        tempDocumentRepository.save(tempDocument);

        // 업데잍트한 데이터 return.
        return tempDocument;
    }

    @Transactional
    public MessageResponseDTO deleteDocument(long documentId) {
        TempDocument document = tempDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Not Found Document"));
        List<TempRecord> documentRecordList = tempRecordRepository.findByDocumentId(documentId);

        for (TempRecord record : documentRecordList) {
            tempRecordRepository.delete(record);
        }

        tempDocumentRepository.delete(document);

        return new MessageResponseDTO(true, "deleted");
    }

    /**
     * 레코드 리스트의 전체 가격을 구함.
     * @param createdRecordList
     * @return
     */
    private long getTotalPrice(List<TempRecord> createdRecordList) {
        long totalPrice = 0;
        for (TempRecord record: createdRecordList) {
            long recordTotalPrice = record.getPrice() * record.getQuantity();
            totalPrice += recordTotalPrice;
        }

        return totalPrice;
    }
}
