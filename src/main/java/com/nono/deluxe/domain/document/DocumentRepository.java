package com.nono.deluxe.domain.document;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, Long> {

	@Query("SELECT d "
			+ "FROM Document d "
			+ "WHERE d.company.name like concat('%', :query,'%') AND d.date BETWEEN :fromDate AND :toDate")
	Page<Document> readDocumentList(@Param("query") String query,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			Pageable limit);

	@Query("SELECT d FROM Document d WHERE d.company.id = :companyId AND d.date BETWEEN :fromMonth AND :toMonth")
	Page<Document> findByCompanyId(@Param("companyId") long companyId, @Param("fromMonth") LocalDate fromMonth,
			@Param("toMonth") LocalDate toMonth, Pageable limit);
}
