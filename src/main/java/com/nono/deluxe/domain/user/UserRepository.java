package com.nono.deluxe.domain.user;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.id = :userId AND u.deleted = false")
    Optional<User> findById(@Param("userId") long userId);

    @Query("SELECT u FROM User u WHERE u.name LIKE (:name) AND u.deleted = false")
    Optional<User> findByName(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.email LIKE (:email) AND u.deleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT p "
        + "FROM User p "
        + "WHERE p.name LIKE CONCAT('%', :query, '%') "
        + "AND p.deleted = false")
    Page<User> findPageByName(@Param("query") String query, Pageable pageable);
}
