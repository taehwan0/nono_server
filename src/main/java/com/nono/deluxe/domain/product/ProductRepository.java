package com.nono.deluxe.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Pageable 적용이 필요 할겁니다. 또는 아래의 방식으로 사용하려면 native query 값을 true 로 하고 로직의 수정이 필요 할 수 있습니다.
 * 아래의 방법으로는 현재 page, total page, count 등을 알 수 없습니다.
 */
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
