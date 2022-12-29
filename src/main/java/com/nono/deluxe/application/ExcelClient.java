package com.nono.deluxe.application;

import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.utils.LocalDateCreator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
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
import org.springframework.stereotype.Service;

@EnableAsync
@RequiredArgsConstructor
@Service
public class ExcelClient {

    private static final int INDEX_OF_NAME = 0;
    private static final int INDEX_OF_UNIT = 1;
    private static final int INDEX_OF_TYPE = 2;
    private static final int INDEX_OF_TOTAL = 3;

    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;

    public File createMonthlyDocumentFile(int year, int month) {
        Workbook workbook = new SXSSFWorkbook();

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

            // input, output, stock row 의 day(column)에 해당하는 cell 을 채움
            for (int day = 1; day <= LocalDate.of(year, month, 1).lengthOfMonth(); day++) {
                long inputQuantity = getTotalQuantityOfDate(year, month, day, product, DocumentType.INPUT);
                Cell inputCell = inputRow.createCell(day + 3);
                inputCell.setCellValue(inputQuantity);

                long outputQuantity = getTotalQuantityOfDate(year, month, day, product, DocumentType.OUTPUT);
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
        return exportWorkbook(workbook);
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(HSSFColorPredefined.RED.getIndex());
        style.setFont(font);
        return style;
    }

    private long getRecentStock(int year, int month, Product product) {
        return recordRepository.findRecentStock(
            LocalDateCreator.getDateOfFirstDay(year, month),
            product.getId()).orElse(0L);
    }

    private long getTotalQuantityOfDate(int year, int month, int day, Product product, DocumentType type) {
        return recordRepository.sumTotalQuantityOfDate(
            LocalDate.of(year, month, day),
            product.getId(),
            type.toString()
        ).orElse(0L);
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

    private File exportWorkbook(Workbook workbook) {
        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), "xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);

            workbook.close();

            return file;
        } catch (IOException exception) {
            throw new RuntimeException("파일 생성 실패");
        }
    }
}
