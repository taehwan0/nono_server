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

    @Query("SELECT c FROM Company c WHERE c.name LIKE(:name) AND c.deleted = false")
    Optional<Company> findByName(@Param("name") String name);

    @Query("SELECT c FROM Company c WHERE c.name LIKE CONCAT('%', :query, '%') AND c.deleted = false")
    Page<Company> findPageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.active = true "
        + "AND c.deleted = false")
    Page<Company> findActivePageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'INPUT' "
        + "AND c.deleted = false")
    Page<Company> findInputPageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'INPUT' "
        + "AND c.active = true "
        + "AND c.deleted = false")
    Page<Company> findActiveInputPageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'OUTPUT' "
        + "AND c.deleted = false")
    Page<Company> findOutputPageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT c "
        + "FROM Company c "
        + "WHERE c.name LIKE CONCAT('%', :query, '%') "
        + "AND c.type = 'OUTPUT' "
        + "AND c.active = true "
        + "AND c.deleted = false")
    Page<Company> findActiveOutputPageName(@Param("query") String query, Pageable pageable);
}
