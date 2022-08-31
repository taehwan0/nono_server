package com.nono.deluxe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/document")
@RestController
public class DocumentController {

    @PostMapping("")
    public void createDocument() {

    }

    @GetMapping("/{documentId}")
    public void readDocument() {

    }

    @GetMapping("")
    public void readDocumentList() {

    }

    @PutMapping("/{documentId}")
    public void updateDocument() {

    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument() {

    }

    /**
     * 어떤 값들을 입력 받아서 처리할지 기준 필요.
     * full Data, 월별 데이터, 일별 데이터, 상품별 데이터 등
     * document 에서는 full, 월별, 일별 데이터를 처리하게 될 수 있음.
     */
    @GetMapping("/{documentId}/xls")
    public void exportDocumentToExcel() {

    }
}
