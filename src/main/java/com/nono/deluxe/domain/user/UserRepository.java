package com.nono.deluxe.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :userCode AND u.deleted = false")
    Optional<User> findById(@Param("userCode") long userCode);

    @Query("SELECT u FROM User u WHERE u.email LIKE(:email) AND u.password LIKE(:password) AND u.deleted = false")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}
