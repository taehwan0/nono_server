package com.nono.deluxe.document.application;

import com.nono.deluxe.common.utils.LocalDateCreator;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.domain.repository.RecordRepository;
import com.nono.deluxe.product.domain.Product;
import com.nono.deluxe.product.domain.repository.ProductRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync
@RequiredArgsConstructor
@Component
public class ExcelClient {

    private static final int INDEX_OF_NAME = 0;
    private static final int INDEX_OF_UNIT = 1;
    private static final int INDEX_OF_TYPE = 2;
    private static final int INDEX_OF_TOTAL = 3;

    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;

    public Optional<File> createMonthlyDocumentFile(int year, int month) {
        try {
            validateMonth(month);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        try (Workbook workbook = new SXSSFWorkbook()) {
            CellStyle style = createCellStyle(workbook);

            Sheet sheet = workbook.createSheet(month + "월");
            setHeader(sheet, year, month);

            int rowIndex = 1; // header 를 제외한 시작 인덱스

            for (Product product : productRepository.findAllOrderByProductCodeASC()) {
                // 해당 상품의 행 생성
                Row stockRow = sheet.createRow(rowIndex);
                Row inputRow = sheet.createRow(rowIndex + 1);
                Row outputRow = sheet.createRow(rowIndex + 2);

                // 행 기본 정보 채우기
                stockRow.createCell(INDEX_OF_NAME).setCellValue(product.getName());
                stockRow.createCell(INDEX_OF_UNIT).setCellValue(product.getUnit());

                stockRow.createCell(INDEX_OF_TYPE).setCellValue("재고");
                inputRow.createCell(INDEX_OF_TYPE).setCellValue("입고");
                outputRow.createCell(INDEX_OF_TYPE).setCellValue("출고");

                // 이전 stock 찾기
                long recentStock = getRecentStock(year, month, product);

                Cell totalStockCell = stockRow.createCell(INDEX_OF_TOTAL);
                totalStockCell.setCellValue(recentStock);
                totalStockCell.setCellStyle(style);

                List<Record> records = recordRepository
                    .getRecordsByProductIdBetweenDates(fromDate, toDate, product.getId());

                // input, output, stock row 의 day(column)에 해당하는 cell 을 채움
                for (int day = 1; day <= LocalDate.of(year, month, 1).lengthOfMonth(); day++) {

                    LocalDate date = LocalDate.of(year, month, day);
                    long inputQuantity = getTotalQuantityOfDate(records, date, DocumentType.INPUT);
                    Cell inputCell = inputRow.createCell(day + 3);
                    inputCell.setCellValue(inputQuantity);

                    long outputQuantity = getTotalQuantityOfDate(records, date, DocumentType.OUTPUT);
                    Cell outputCell = outputRow.createCell(day + 3);
                    outputCell.setCellValue(outputQuantity);

                    recentStock = recentStock + inputQuantity - outputQuantity;
                    Cell stockCell = stockRow.createCell(day + 3);
                    stockCell.setCellValue(recentStock);
                    stockCell.setCellStyle(style);
                }
                // 다음 상품 진행
                rowIndex += 3;
            }
            return Optional.of(exportWorkbook(workbook));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("1~12의 월만 입력 가능합니다.");
        }
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(HSSFColorPredefined.RED.getIndex());
        style.setFont(font);
        return style;
    }

    private long getRecentStock(int year, int month, Product product) {
        return recordRepository.findRecentStockByProductId(
            LocalDateCreator.getDateOfFirstDay(year, month),
            product.getId()).orElse(0L);
    }

    private long getTotalQuantityOfDate(List<Record> records, LocalDate date, DocumentType type) {
        return records.stream().filter(
                record -> record.getDocument().getDate().equals(date) && record.getDocument().getType().equals(type))
            .mapToLong(Record::getQuantity).sum();
    }

    private void setHeader(Sheet sheet, int year, int month) {
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("품목명");
        header.createCell(1).setCellValue("규격");
        header.createCell(2).setCellValue("날짜");
        header.createCell(3).setCellValue("전월");

        for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {
            header.createCell(i + 3).setCellValue(i);
        }
    }

    private File exportWorkbook(Workbook workbook) throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), "xlsx");

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);

            return file;
        }
    }
}
