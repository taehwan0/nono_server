package com.nono.deluxe.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.document.CreateDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.document.DocumentResponseDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.presentation.dto.document.UpdateDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.record.RecordRequestDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public DocumentResponseDTO createDocument(long userId, CreateDocumentRequestDTO requestDto) {
        User writer = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));
        Company company = companyRepository.findById(requestDto.getCompanyId())
            .orElseThrow(() -> new NotFoundException("Not Found Company"));
        Document document = requestDto.toEntity(writer, company);

        documentRepository.save(document);

        List<RecordRequestDTO> recordRequestDtoList = requestDto.getRecordList();
        List<Record> createdRecordList = new ArrayList<>();

        for (RecordRequestDTO recordRequestDto : recordRequestDtoList) {
            long productId = recordRequestDto.getProductId();
            // update 대상 product
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found"));

            // record response 가 필요하면 여기서 리턴받아 사용 가능
            Record record = createRecord(document, product, recordRequestDto);
            createdRecordList.add(record);
        }

        long[] totalCountAndPrice = getTotalCountAndPrice(document);
        long totalCount = totalCountAndPrice[0];
        long totalPrice = totalCountAndPrice[1];

        return new DocumentResponseDTO(document, totalCount, totalPrice, createdRecordList);
    }

    @Transactional(readOnly = true)
    public DocumentResponseDTO readDocument(long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Not Found Document"));
        List<Record> recordList = recordRepository.findByDocumentId(documentId);

        long[] totalCountAndPrice = getTotalCountAndPrice(document);
        long totalCount = totalCountAndPrice[0];
        long totalPrice = totalCountAndPrice[1];

        return new DocumentResponseDTO(document, totalCount, totalPrice, recordList);
    }

    @Transactional(readOnly = true)
    public ReadDocumentListResponseDTO readDocumentList(String query, String column, String order, int size, int page,
        int year, int month) {
        Pageable limit = PageRequest.of(page, size, Sort.by(
            new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column),
            new Sort.Order(Sort.Direction.ASC, "createdAt")));

        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        int toMonth = month;
        if (month == 0) {
            month = 1;
            toMonth = 12;
        }

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, toMonth, LocalDate.of(year, toMonth, 1).lengthOfMonth());
        // 테스트 해보기

        Page<Document> documentPage = documentRepository.readDocumentList(query, fromDate, toDate, limit);

        return new ReadDocumentListResponseDTO(documentPage);
    }

    @Transactional
    public DocumentResponseDTO updateDocument(long documentId, UpdateDocumentRequestDTO requestDto) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Not Found Document"));

        long companyId = requestDto.getCompanyId();
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Not Found Company"));

        document.updateCompany(company);

        List<RecordRequestDTO> recordList = requestDto.getRecordList();
        List<Long> updateProductIdList = new ArrayList<>(); // 새로이 변경될 record 들의 productId List

        for (RecordRequestDTO recordRequestDto : recordList) {
            // update 목록에 있는 record 가 db 에 존재하면 update
            // 존재하지 않는다면 create
            // db 에는 존재하나 request 목록에 없으면 delete
            long productId = recordRequestDto.getProductId();
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not Found Product"));
            updateProductIdList.add(productId);

            Optional<Record> optionalRecord = recordRepository.findUpdateTargetRecord(productId, documentId);
            if (optionalRecord.isEmpty()) {
                // create 로직
                Record record = createRecord(document, product, recordRequestDto);
            } else {
                // update 로직
                Record record = optionalRecord.get();
                int typeSwitch = getTypeSwitch(document);
                // quantity 변화량을 구해 stock 에 저장할 것
                long changeQuantity = (recordRequestDto.getQuantity() - record.getQuantity()) * typeSwitch;

                // record quantity, price 반영
                record.updateRecord(recordRequestDto);
                // record stock 반영
                record.updateStock(record.getStock() + changeQuantity);

                // product stock 반영
                product.updateStock(product.getStock() + changeQuantity);
                recordRepository.updateStockFutureDateRecord(productId, document.getDate(), changeQuantity);
            }

            // 기존 데이터는 존재하지만, 여기에 속하지 않는 Id 는 삭제처리 해야함
            updateProductIdList.add(productId);
        }

        recordRepository.findByDocumentId(documentId);
        List<Record> documentRecordList = recordRepository.findByDocumentId(documentId);
        for (Record documentRecord : documentRecordList) {
            Product product = documentRecord.getProduct();
            long productId = product.getId();

            // requestDto 에 속하지 않는 record 는 삭제처리
            if (!updateProductIdList.contains(productId)) {
                deleteRecord(documentRecord);
            }
        }

        long[] totalCountAndPrice = getTotalCountAndPrice(document);
        long totalCount = totalCountAndPrice[0];
        long totalPrice = totalCountAndPrice[1];

        List<Record> finalRecordList = recordRepository.findByDocumentId(documentId);

        document.setUpdatedAt(LocalDateTime.now());

        return new DocumentResponseDTO(document, totalCount, totalPrice, finalRecordList);
    }

    @Transactional
    public MessageResponseDTO deleteDocument(long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Not Found Document"));
        List<Record> documentRecordList = recordRepository.findByDocumentId(documentId);

        for (Record record : documentRecordList) {
            deleteRecord(record);
        }

        documentRepository.delete(document);

        return new MessageResponseDTO(true, "deleted");
    }

    /**
     * document 에 해당하는 record 를 모두 찾아, 개수와 총 가격 (quantity * price) 을 연산하여 반환
     *
     * @param document
     * @return long[] // totalRecordCount, totalPrice
     */
    private long[] getTotalCountAndPrice(Document document) {
        List<Record> finalRecord = recordRepository.findByDocumentId(document.getId());
        int totalCount = finalRecord.size();
        long totalPrice = 0;
        for (Record record : finalRecord) {
            totalPrice += (record.getQuantity() * record.getPrice());
        }

        return new long[]{totalCount, totalPrice};
    }

    /**
     * document 의 product 에 해당하는 record 를 생성함, 입력한 document 의 날짜가 최신 날짜일 시 record 생성 및 product stock 에 반영, 최신 날짜가 아닐시 입력
     * 날짜 이후의 record 중 가장 오래된 record 의 quantity, stock 을 통하여 해당 시점의 product stock 을 구하고 record 생성 및 product stock, 입력 날짜
     * 이후의 record stock 에 반영
     *
     * @param document
     * @param product
     * @param recordRequestDto
     * @return record
     */
    private Record createRecord(Document document, Product product, RecordRequestDTO recordRequestDto) {
        List<Record> futureDateRecordList = recordRepository.findFutureDateRecordList(product.getId(),
            document.getDate());

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
            recordRepository.updateStockFutureDateRecord(product.getId(), document.getDate(), changeQuantity);
        }

        return record;
    }

    /**
     * 입력받은 record 를 삭제하고, 삭제된 record 에 해당하는 product stock, 이후 날짜에 해당하는 record stock 에 삭제 내용을 반영 후 해당 record 를 삭제
     *
     * @param record
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
        recordRepository.updateStockFutureDateRecord(productId, document.getDate(), changeQuantity);
        recordRepository.delete(record);
    }

    /**
     * 문서의 타입을 통해 + 또는 - 하기 위한 switch 반환
     *
     * @param document
     * @return
     */
    private int getTypeSwitch(Document document) {
        if (document.getType().equals(DocumentType.INPUT)) {
            return 1;
        }
        return -1;
    }
}
