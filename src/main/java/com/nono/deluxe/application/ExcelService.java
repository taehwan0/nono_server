package com.nono.deluxe.application;

import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.RecordRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExcelService {

    private final DocumentRepository documentRepository;
    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;

    @Transactional
    public File getProductsRecord(int year, List<Integer> months) {
        Workbook workbook = new SXSSFWorkbook();

        int rowIndex = 1;

        for (int month : months) {
            Sheet sheet = workbook.createSheet(month + "월");

            setHeader(sheet, year, month);

            for (Product product : productRepository.findAll()) {
                Row stockRow = sheet.createRow(rowIndex);
                Row inputRow = sheet.createRow(rowIndex + 1);
                Row outputRow = sheet.createRow(rowIndex + 2);

                stockRow.createCell(0).setCellValue(product.getName());
                stockRow.createCell(1).setCellValue(product.getUnit());

                stockRow.createCell(2).setCellValue("재고");
                inputRow.createCell(2).setCellValue("입고");
                inputRow.createCell(3).setCellValue(recordRepository.getInputQuantityOf(product.getId(), year, month));
                outputRow.createCell(2).setCellValue("출고");

                rowIndex += 3;
            }
        }

        return exportWorkbook(workbook);
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
