package com.campusskill.repository;

import com.campusskill.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrderByNameAsc(String name, String email);

    List<User> findAllByOrderByNameAsc();
}
