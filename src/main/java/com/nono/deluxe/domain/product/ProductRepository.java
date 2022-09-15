package com.nono.deluxe.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Pageable 적용이 필요 할겁니다. 또는 아래의 방식으로 사용하려면 native query 값을 true 로 하고 로직의 수정이 필요 할 수 있습니다.
 * 아래의 방법으로는 현재 page, total page, count 등을 알 수 없습니다.
 */

/**
 * from. hj.yang
 * 해당 부분 수정 완료입니다.
 * 다만 모든 데이터 추출을 쿼리만으로 뽑는거랑 데이터를 추출해서 메모리로 처리하는거랑 속도 및 정확성 등에서 얼마나 차이가 있는지 궁금합니다.ㅎ
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deleted = false")
    Optional<Product> findById(@Param("productId") long productId);

    @Query(value = "SELECT p FROM Product p WHERE p.name LIKE CONCAT('%', :query, '%') AND p.active = true AND p.deleted = false")
    Page<Product> getActiveProductList(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT p FROM Product p WHERE p.name LIKE CONCAT('%', :query, '%') AND p.deleted = false")
    Page<Product> getProductList(@Param("query") String query, Pageable pageable);
}
