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

	@Query("SELECT u FROM User u WHERE u.id = :userCode AND u.deleted = false")
	Optional<User> findById(@Param("userCode") long userCode);

	@Query("SELECT u "
			+ "FROM User u "
			+ "WHERE u.email LIKE(:email) AND u.password LIKE(:password) AND u.deleted = false AND u.active = true")
	Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

	@Query(value = "SELECT p FROM User p WHERE p.name LIKE CONCAT('%', :query, '%') AND p.deleted = false")
	Page<User> readUserList(@Param("query") String query, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.email LIKE (:email)")
	Optional<User> findByEmail(@Param("email") String email);
}
