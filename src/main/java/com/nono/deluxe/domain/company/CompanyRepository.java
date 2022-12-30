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

    Optional<Company> findByName(String name);

    @Query(value = "SELECT c FROM Company c WHERE c.name LIKE concat('%', :query, '%') AND c.deleted = false ")
    Page<Company> getCompanyList(@Param("query") String query,
        Pageable limit);

    @Query(value
        = "SELECT c "
        + "FROM Company c "
        + "WHERE c.name "
        + "LIKE concat('%', :query, '%') and c.active = true AND c.deleted = false")
    Page<Company> getActiveCompanyList(@Param("query") String query,
        Pageable limit);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'INPUT' "
        + "AND c.deleted = false")
    Page<Company> getInputCompanyList(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'INPUT' "
        + "AND c.active = true "
        + "AND c.deleted = false")
    Page<Company> getActiveInputCompanyList(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'OUTPUT' "
        + "AND c.deleted = false")
    Page<Company> getOutputCompanyList(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'OUTPUT' "
        + "AND c.active = true "
        + "AND c.deleted = false")
    Page<Company> getActiveOutputCompanyList(@Param("query") String query, Pageable pageable);
}
