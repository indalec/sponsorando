package com.sponsorando.app.repositories;

import com.sponsorando.app.models.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);

    @Query("SELECT u FROM UserAccount u WHERE " +
            "LOWER(CAST(u.id AS string)) LIKE %:search% OR " +
            "LOWER(u.name) LIKE %:search% OR " +
            "LOWER(u.email) LIKE %:search% OR " +
            "LOWER(CAST(u.role AS string)) LIKE %:search% OR " +
            "LOWER(CASE WHEN u.enabled = true THEN 'active' ELSE 'inactive' END) LIKE %:search%")
    Page<UserAccount> searchUsers(@Param("search") String search, Pageable pageable);
}
