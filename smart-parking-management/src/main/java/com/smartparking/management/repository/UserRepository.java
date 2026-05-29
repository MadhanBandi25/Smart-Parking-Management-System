package com.smartparking.management.repository;

import com.smartparking.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);
    Optional<User> findByIdAndDeletedFalse(Long id);
    Optional<User> findByEmailAndDeletedFalse(String email);

    List<User> findAllByDeletedFalse();
    List<User> findByEmailContainingIgnoreCaseAndDeletedFalse(String email);
    List<User> findByPhoneNumberContainingAndDeletedFalse(String phoneNumber);

    long countByDeletedFalse();
}
