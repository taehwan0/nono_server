package com.nono.deluxe.document.application;

import com.nono.deluxe.common.application.MailClient;
import com.nono.deluxe.common.exception.NotFoundException;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.common.utils.LocalDateCreator;
import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.repository.CompanyRepository;
import com.nono.deluxe.document.domain.Document;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.domain.repository.DocumentRepository;
import com.nono.deluxe.document.domain.repository.RecordRepository;
import com.nono.deluxe.document.presentation.dto.document.CreateDocumentRequestDTO;
import com.nono.deluxe.document.presentation.dto.document.DocumentResponseDTO;
import com.nono.deluxe.document.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.document.presentation.dto.document.UpdateDocumentRequestDTO;
import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import com.nono.deluxe.product.domain.Product;
import com.nono.deluxe.product.domain.repository.ProductRepository;
import com.nono.deluxe.user.domain.User;
import com.nono.deluxe.user.domain.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@EnableAsync
@RequiredArgsConstructor
@Service
public class DocumentService {

    private final MailClient mailClient;
    private final ExcelClient excelClient;

    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public DocumentResponseDTO createDocument(long userId, CreateDocumentRequestDTO createDocumentRequestDTO) {
        User writer = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));
        Company company = companyRepository.findById(createDocumentRequestDTO.getCompanyId())
            .orElseThrow(() -> new NotFoundException("Not Found Company"));
        Document document = createDocumentRequestDTO.toEntity(writer, company);

        documentRepository.save(document);

        List<RecordRequestDTO> recordRequestDtoList = createDocumentRequestDTO.getRecordList();

        List<Record> records = new ArrayList<>();

        for (RecordRequestDTO recordRequestDto : recordRequestDtoList) {
            long productId = recordRequestDto.getProductId();
            // update 대상 product
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

            // record response 가 필요하면 여기서 리턴받아 사용 가능
            records.add(createRecord(document, product, recordRequestDto));
        }

        return new DocumentResponseDTO(document, records);
    }

    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocument(long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Not Found Document"));

        return new DocumentResponseDTO(document, true);
    }

    @Transactional(readOnly = true)
    public ReadDocumentListResponseDTO getDocumentList(
        PageRequest pageRequest, String query, int year, int month, boolean withRecord) {
        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        if (query.equals("")) {
            return new ReadDocumentListResponseDTO(
                documentRepository.findPageBetween(fromDate, toDate, pageRequest), withRecord);
        }
        return new ReadDocumentListResponseDTO(
            documentRepository.findPageByCompanyName(query, fromDate, toDate, pageRequest), withRecord);
    }

    // TODO: 로직 확인 필요!
    @Transactional
    public DocumentResponseDTO updateDocument(long documentId, UpdateDocumentRequestDTO updateDocumentRequestDTO) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Not Found Document"));

        long companyId = updateDocumentRequestDTO.getCompanyId();
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Not Found Company"));

        document.updateCompany(company);

        List<RecordRequestDTO> recordList = updateDocumentRequestDTO.getRecordList();
        List<Long> updateProductIdList = new ArrayList<>(); // 새로이 변경될 record 들의 productId List
        for (RecordRequestDTO recordRequestDto : recordList) {
            // update 목록에 있는 record 가 db 에 존재하면 update
            // 존재하지 않는다면 create
            // db 에는 존재하나 request 목록에 없으면 delete
            long productId = recordRequestDto.getProductId();
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not Found Product"));
            updateProductIdList.add(productId);

            Optional<Record> optionalRecord = recordRepository.findAllByProductIdAndDocumentId(productId, documentId);
            Record record;
            if (optionalRecord.isEmpty()) {
                // create 로직
                record = createRecord(document, product, recordRequestDto);
                recordRepository.save(record);
            } else {
                // update 로직
                record = optionalRecord.get();
                int typeSwitch = getTypeSwitch(document);
                // quantity 변화량을 구해 stock 에 저장할 것
                long changeQuantity = (recordRequestDto.getQuantity() - record.getQuantity()) * typeSwitch;

                // record quantity, price 반영
                record.updateRecord(recordRequestDto);
                // record stock 반영
                record.updateStock(record.getStock() + changeQuantity);

                // product stock 반영
                product.updateStock(product.getStock() + changeQuantity);
                recordRepository.updateAllStockAfterThan(productId, document.getDate(), changeQuantity);
            }

            // 기존 데이터는 존재하지만, 여기에 속하지 않는 Id 는 삭제처리 해야함
            updateProductIdList.add(productId);
        }

        recordRepository.findByDocumentId(documentId);
        List<Record> documentRecordList = recordRepository.findByDocumentId(documentId);
        for (Record documentRecord : documentRecordList) {
            Product product = documentRecord.getProduct();
            long productId = product.getId();

            // updateDocumentRequestDTO 에 속하지 않는 record 는 삭제처리
            if (!updateProductIdList.contains(productId)) {
                deleteRecord(documentRecord);
            }
        }

        document.setUpdatedAt(LocalDateTime.now());

        return new DocumentResponseDTO(document, true);
    }

    @Transactional
    public MessageResponseDTO deleteDocument(long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Not Found Document"));

        recordRepository.findByDocumentId(documentId)
            .forEach(this::deleteRecord);

        documentRepository.delete(document);

        return MessageResponseDTO.ofSuccess("deleted");
    }

    /**
     * document 의 product 에 해당하는 record 를 생성함, 입력한 document 의 날짜가 최신 날짜일 시 record 생성 및 product stock 에 반영, 최신 날짜가 아닐시 입력
     * 날짜 이후의 record 중 가장 오래된 record 의 quantity, stock 을 통하여 해당 시점의 product stock 을 구하고 record 생성 및 product stock, 입력 날짜
     * 이후의 record stock 에 반영
     */
    private Record createRecord(Document document, Product product, RecordRequestDTO recordRequestDto) {
        List<Record> futureDateRecordList
            = recordRepository.findAllAfterThan(product.getId(), document.getDate());

        Record record;
        if (futureDateRecordList.size() == 0) {
            // 입력된 날짜가 product-record 의 가장 마지막 날짜인 경우 (일반적인 생성)
            // == update 대상 record 가 없음
            long nextStock = (document.getType().equals(DocumentType.INPUT))
                ? product.getStock() + recordRequestDto.getQuantity()
                : product.getStock() - recordRequestDto.getQuantity();
            record = recordRequestDto.toEntity(document, product, nextStock);
            recordRepository.save(record);

            product.updateStock(nextStock);
        } else {
            // 입력된 날짜가 마지막 날짜가 아닌 경우 (이후 날짜의 데이터들이 업데이트가 필요한 경우)
            // 입력된 것 보다 미래의 날짜를 가진 레코드 중 가장 앞의 데이터 가져오기
            Record oldRecord = futureDateRecordList.get(0);
            long tempStock = (oldRecord.getDocument().getType().equals(DocumentType.INPUT))
                ? oldRecord.getStock() - oldRecord.getQuantity()
                : oldRecord.getStock() + oldRecord.getQuantity();

            // 입고 출고에 따라 + 또는 - 연산을 스위칭 하기 위함
            int typeSwitch = getTypeSwitch(document);
            long nextStock = tempStock + (recordRequestDto.getQuantity() * typeSwitch);
            record = recordRequestDto.toEntity(document, product, nextStock);
            // 추가되는 날짜의 이후 레코드중 가장 오래된 것을 가져와 stock 을 계산하여 추가할 record 의 stock 을 결정
            recordRepository.save(record);

            long changeQuantity = recordRequestDto.getQuantity() * typeSwitch;
            product.updateStock(product.getStock() + changeQuantity);
            // 현재 생성된 record 의 date 이후 날짜가 되는 모든 레코드 stock 에 연산
            recordRepository.updateAllStockAfterThan(product.getId(), document.getDate(), changeQuantity);
        }
        return record;
    }

    /**
     * 입력받은 record 를 삭제하고, 삭제된 record 에 해당하는 product stock, 이후 날짜에 해당하는 record stock 에 삭제 내용을 반영 후 해당 record 를 삭제
     */
    private void deleteRecord(Record record) {
        Document document = record.getDocument();
        Product product = record.getProduct();
        long productId = product.getId();

        // update 된 productId List 에 없는 record 의 경우 실행(삭제로직)
        int deleteTypeSwitch = getTypeSwitch(document) * -1;
        // stock 에 다시 보충되어야 하는 개수
        long changeQuantity = record.getQuantity() * deleteTypeSwitch;

        product.updateStock(product.getStock() + changeQuantity);
        recordRepository.updateAllStockAfterThan(productId, document.getDate(), changeQuantity);
        recordRepository.delete(record);
    }

    /**
     * 문서의 타입을 통해 + 또는 - 하기 위한 switch 반환
     **/
    private int getTypeSwitch(Document document) {
        if (document.getType().equals(DocumentType.INPUT)) {
            return 1;
        }
        return -1;
    }

    @Async
    @Transactional(readOnly = true)
    public void postMonthDocument(long userId, int year, int month)
        throws MessagingException, IOException {
        Optional<File> excelFile = excelClient.createMonthlyDocumentFile(year, month);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("NotFountUser"));

        mailClient.postMonthlyDocumentMail(user.getEmail(), year, month, excelFile);
    }

    private String createSubject(int year, int month) {
        return year + "년 " + month + "월 노노유통 월간 문서";
    }
}
