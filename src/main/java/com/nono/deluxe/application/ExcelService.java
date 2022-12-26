package com.nono.deluxe.application;

import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.utils.LocalDateCreator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExcelService {

    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;

    @Transactional
    public File getProductsRecord(int year, int month) {
        Workbook workbook = new SXSSFWorkbook();

        int rowIndex = 1;

        Sheet sheet = workbook.createSheet(month + "월");

        setHeader(sheet, year, month);

        for (Product product : productRepository.findAll()) {
            Row stockRow = sheet.createRow(rowIndex);
            Row inputRow = sheet.createRow(rowIndex + 1);
            Row outputRow = sheet.createRow(rowIndex + 2);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
            cellStyle.setFillPattern(FillPatternType.BIG_SPOTS);
            stockRow.setRowStyle(cellStyle);

            stockRow.createCell(0).setCellValue(product.getName());
            stockRow.createCell(1).setCellValue(product.getUnit());

            stockRow.createCell(2).setCellValue("재고");
            inputRow.createCell(2).setCellValue("입고");
            outputRow.createCell(2).setCellValue("출고");

            List<Record> records = recordRepository.findAllByProductIdAndDocumentDateBetween(
                product.getId(),
                LocalDateCreator.getFromDate(year, month),
                LocalDateCreator.getToDate(year, month));

            long recentStock = recordRepository.findRecentStock(
                LocalDateCreator.getToDate(year, month),
                product.getId()).orElse(0L);

            stockRow.createCell(3).setCellValue(recentStock);
            inputRow.createCell(3).setCellValue(getTotalQuantityTypeOf(records, DocumentType.INPUT));
            outputRow.createCell(3).setCellValue(getTotalQuantityTypeOf(records, DocumentType.OUTPUT));

            for (int i = 1; i <= LocalDate.of(year, month, 1).lengthOfMonth(); i++) {

                long inputQuantity = recordRepository.sumTotalQuantityOfDate(
                    LocalDate.of(year, month, i),
                    product.getId(),
                    DocumentType.INPUT.toString()
                ).orElse(0L);

                long outputQuantity = recordRepository.sumTotalQuantityOfDate(
                    LocalDate.of(year, month, i),
                    product.getId(),
                    DocumentType.OUTPUT.toString()
                ).orElse(0L);

                recentStock = recentStock + inputQuantity - outputQuantity;

                stockRow.createCell(i + 3).setCellValue(recentStock);

                inputRow.createCell(i + 3).setCellValue(inputQuantity);

                outputRow.createCell(i + 3).setCellValue(outputQuantity);
            }

            rowIndex += 3;
        }
        return exportWorkbook(workbook);
    }

    private long getTotalQuantityTypeOf(List<Record> records, DocumentType documentType) {
        return records.stream()
            .filter(record -> record.getDocument().getType().equals(documentType))
            .mapToLong(Record::getQuantity)
            .sum();
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
