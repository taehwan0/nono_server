package com.nono.deluxe.domain.authcode;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode, Long> {

    @Query("SELECT a FROM AuthCode a WHERE a.user.id = :userCode")
    List<AuthCode> findByUserCode(@Param("userCode") long userCode);

    @Query("SELECT a FROM AuthCode a WHERE a.code = :authCode")
    Optional<AuthCode> findByAuthCode(@Param("authCode") String authCode);
}
