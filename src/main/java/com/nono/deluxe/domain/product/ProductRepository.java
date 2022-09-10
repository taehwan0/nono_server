package com.nono.deluxe.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT u FROM Product u WHERE u.name LIKE :productName and u.activate = :active ORDER BY :columnName :order")
    List<Product> findProductList(@Param("productName") String productName,
                                  @Param("active") boolean active,
                                  @Param("columnName") String order,
                                  @Param("order") String orderWay);

    @Query("SELECT u FROM Product u WHERE u.name LIKE :productName ORDER BY :columnName :order")
    List<Product> findProductList(@Param("productName") String productName,
                                  @Param("columnName") String order,
                                  @Param("order") String orderWay);

}