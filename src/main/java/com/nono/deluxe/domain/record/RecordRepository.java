package com.nono.deluxe.domain.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * intellij 에서 잡아주는 에러로는 아래와 같이 u.product.id 로 사용한다고 합니다.
 * 데이터베이스에서 사용하는 컬럼의 이름을 직접적으로 사용하지 않고 객체의 프로퍼티들과 매칭되기 때문인 것 같습니다.
 */
public interface RecordRepository extends JpaRepository<Record, Long> {
    @Query("SELECT u FROM Record u WHERE u.product.id = :productId ")
    List<Record> findRecordList(@Param("productId") long productId);
}
