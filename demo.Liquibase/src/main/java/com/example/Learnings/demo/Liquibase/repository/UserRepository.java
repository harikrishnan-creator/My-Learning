package com.example.Learnings.demo.Liquibase.repository;

import com.example.Learnings.demo.Liquibase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 * 
 * Spring Data JPA repository for User entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists by username
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists by email
     */
    boolean existsByEmail(String email);
}
