package com.privetmedved.repository;

import com.privetmedved.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT p.user.id FROM Profile p WHERE p.displayName IS NOT NULL) " +
           "ORDER BY u.createdAt DESC")
    List<User> findUsersWithDisplayName();

    @Query("SELECT u FROM User u WHERE SIZE(u.rooms) >= :minRooms")
    List<User> findActiveUsers(@Param("minRooms") int minRooms);
}