package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAllByRole(User.Role role, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = :active WHERE u.id = :userId")
    void setActiveStatus(@Param("userId") Long userId, @Param("active") boolean active);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :userId AND u.role = :role")
    boolean existsByIdAndRole(@Param("userId") Long userId, @Param("role") User.Role role);
}
