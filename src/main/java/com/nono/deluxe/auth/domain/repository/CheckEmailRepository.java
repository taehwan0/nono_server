package com.nono.deluxe.auth.domain.repository;

import com.nono.deluxe.auth.domain.CheckEmail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckEmailRepository extends JpaRepository<CheckEmail, Long> {

    Optional<CheckEmail> findByEmail(String email);

    @Query("SELECT c FROM CheckEmail c WHERE c.email LIKE :email")
    List<CheckEmail> findAllByEmail(@Param("email") String email);
}
