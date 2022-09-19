package com.nono.deluxe.domain.checkemail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckEmailRepository extends JpaRepository<CheckEmail, Long> {
    Optional<CheckEmail> findByEmail(String email);
}
