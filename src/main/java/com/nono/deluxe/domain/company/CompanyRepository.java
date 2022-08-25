package com.nono.deluxe.domain.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query(value = "SELECT c FROM Company c WHERE c.name LIKE concat('%', :query, '%')")
    Page<Company> readCompanyList(@Param("query") String query,
                           Pageable limit);

    @Query(value = "SELECT c FROM Company c WHERE c.name LIKE concat('%', :query, '%') and c.activate = true")
    Page<Company> readActiveCompanyList(@Param("query") String query,
                                  Pageable limit);
}
