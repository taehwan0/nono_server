package com.nono.deluxe.service;

import com.nono.deluxe.controller.dto.DeleteApiResponseDto;
import com.nono.deluxe.controller.dto.document.CreateDocumentRequestDto;
import com.nono.deluxe.controller.dto.document.DocumentResponseDto;
import com.nono.deluxe.controller.dto.document.UpdateDocumentRequestDto;
import com.nono.deluxe.controller.dto.record.RecordRequestDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public DocumentResponseDto createDocument(long userId, CreateDocumentRequestDto requestDto) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not Found User"));
        Company company = companyRepository.findById(requestDto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Not Found Company"));
        Document document = requestDto.toEntity(writer, company);
        documentRepository.save(document);

//        LocalDate date = document.getDate();
        List<Record> createdRecordList = new ArrayList<>();
        long totalPrice = 0;

        List<RecordRequestDto> recordRequestDtoList = requestDto.getRecordList();
        /**
         * 지금 들어온 날짜가 마지막 날짜라면 product 의 stock 을 가져와서 record 에 추가
         * 지금 들어온 날짜 이이후 데이터가 존재한다면, 최근의 이전 데이터를 가져와서 계산하고 이후의 record 모두 변경
         */
        for(RecordRequestDto recordRequestDto : recordRequestDtoList) {
            long productId = recordRequestDto.getProductId();
            // update 대상 product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));

            Record record = createRecord(document, product, recordRequestDto);

            /* 여기부터
            // update 대상 record 의 list 를 불러옴
            List<Record> futureDateRecordList = recordRepository.findFutureDateRecordList(productId, date);

            Record record;
            if(futureDateRecordList.size() == 0) {
                // 입력된 날짜가 product-record 의 가장 마지막 날짜인 경우 (일반적인 생성)
                // == update 대상 record 가 없음
                long nextStock = (document.getType().equals(DocumentType.INPUT))
                        ? product.getStock() + recordRequestDto.getQuantity()
                        : product.getStock() - recordRequestDto.getQuantity();
                record = recordRequestDto.toEntity(document, product, nextStock);
                recordRepository.save(record);

                product.updateStock(nextStock);
            }  else {
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

                long updateStock = product.getStock() + (recordRequestDto.getQuantity() * typeSwitch);
                product.updateStock(updateStock);
                // 현재 생성된 record 의 date 이후 날짜가 되는 모든 레코드 stock 에 연산
                recordRepository.updateStockFutureDateRecord(productId, date, (recordRequestDto.getQuantity() * typeSwitch));

                여기까지 */

            totalPrice = totalPrice + (record.getQuantity() * record.getPrice());
            createdRecordList.add(record);
        }

        int recordCount = createdRecordList.size();

        return new DocumentResponseDto(document, recordCount, totalPrice);
    }

    @Transactional
    public DocumentResponseDto updateDocument(long documentId, UpdateDocumentRequestDto requestDto) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Not Found Document"));

        long companyId = requestDto.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Not Found Company"));

        document.updateCompany(company);
        // 여기서 company 가 바뀌지 않더라도 record 가 변경됨에 따라 document.createdAt 이 갱신될 방법을 찾아야 함

        List<RecordRequestDto> recordList = requestDto.getRecordList();
        List<Long> updateProductIdList = new ArrayList<>(); // 새로이 변경될 record 들의 productId List

        for(RecordRequestDto recordRequestDto : recordList) {
            // update 목록에 있는 record 가 db 에 존재하면 update
            // 존재하지 않는다면 create
            // db 에는 존재하나 request 목록에 없으면 delete
            long productId = recordRequestDto.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Not Found Product"));
            updateProductIdList.add(productId);

            Optional<Record> optionalRecord = recordRepository.findUpdateTargetRecord(productId, documentId);
            if(optionalRecord.isEmpty()) {
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
        for(Record documentRecord : documentRecordList) {
            Product product = documentRecord.getProduct();
            long productId = product.getId();

            if(!updateProductIdList.contains(productId)) {
                deleteRecord(documentRecord);
            }
        }

        List<Record> finalRecord = recordRepository.findByDocumentId(documentId);
        int totalCount = finalRecord.size();
        long totalPrice = 0;
        for (Record record : finalRecord) {
            totalPrice += (record.getQuantity() * record.getPrice());
        }

        return new DocumentResponseDto(document, totalCount, totalPrice);
    }

    @Transactional
    public DeleteApiResponseDto deleteDocument(long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Not Found Document"));
        List<Record> documentRecordList = recordRepository.findByDocumentId(documentId);

        for (Record record : documentRecordList) {
            deleteRecord(record);
        }

        documentRepository.delete(document);

        return new DeleteApiResponseDto(true, "deleted");
    }

    private Record createRecord(Document document, Product product, RecordRequestDto recordRequestDto) {
        List<Record> futureDateRecordList = recordRepository.findFutureDateRecordList(product.getId(), document.getDate());

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

    private int getTypeSwitch(Document document) {
        if(document.getType().equals(DocumentType.INPUT)) return 1;
        return -1;
    }
}
