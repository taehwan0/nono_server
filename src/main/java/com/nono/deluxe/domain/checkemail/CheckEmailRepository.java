package com.nono.deluxe.domain.checkemail;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckEmailRepository extends JpaRepository<CheckEmail, Long> {

    Optional<CheckEmail> findByEmail(String email);

    @Query("SELECT c FROM CheckEmail c WHERE c.email like :email")
    List<CheckEmail> findAllByEmail(@Param("email") String email);
}
