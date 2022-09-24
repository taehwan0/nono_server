package com.nono.deluxe.domain.logincode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginCodeRepository extends JpaRepository<LoginCode, Long> {
    @Query("SELECT lc FROM LoginCode lc WHERE lc.user.id = :userCode")
    Optional<LoginCode> findByUserCode(@Param("userCode") long userCode);
}
