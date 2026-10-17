package com.shopwave.shopwave.repository;

import com.shopwave.shopwave.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserName(String userName);

    Optional<Users> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);
}