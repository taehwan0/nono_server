package com.nono.deluxe.domain.company;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE c.id = :companyId AND c.deleted = false")
    Optional<Company> findById(@Param("companyId") long companyId);

    @Query(value = "SELECT c FROM Company c WHERE c.name LIKE concat('%', :query, '%') AND c.deleted = false ")
    Page<Company> readCompanyList(@Param("query") String query,
        Pageable limit);

    @Query(value
        = "SELECT c "
        + "FROM Company c "
        + "WHERE c.name "
        + "LIKE concat('%', :query, '%') and c.active = true AND c.deleted = false")
    Page<Company> readActiveCompanyList(@Param("query") String query,
        Pageable limit);
}
