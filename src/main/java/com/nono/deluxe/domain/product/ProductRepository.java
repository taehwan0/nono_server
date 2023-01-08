package com.nono.deluxe.domain.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deleted = false")
    Optional<Product> findById(@Param("productId") long productId);

    @Query("SELECT p FROM Product p WHERE p.barcode LIKE :barcode AND p.deleted = false")
    Optional<Product> findByBarcode(@Param("barcode") String barcode);

    // delete 된 상품 미포함!!
    @Query("SELECT p FROM Product p WHERE p.productCode LIKE (:productCode) AND p.deleted = false")
    Optional<Product> findByProductCode(@Param("productCode") String productCode);

    @Query("SELECT p "
        + "FROM Product p "
        + "WHERE p.name LIKE CONCAT('%', :query, '%') "
        + "AND p.active = true "
        + "AND p.deleted = false")
    Page<Product> findActivePageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE CONCAT('%', :query, '%') AND p.deleted = false")
    Page<Product> findPageByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT p "
        + "FROM Product p "
        + "WHERE p.deleted = false "
        + "ORDER BY p.productCode ASC ")
    List<Product> findAllOrderByProductCodeASC();
}
