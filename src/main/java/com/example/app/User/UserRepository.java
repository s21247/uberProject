package com.example.app.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT s FROM UserEntity s WHERE s.email = ?1")
    Optional<UserEntity> findCustomerByEmail(String email);

    @Query("SELECT s FROM UserEntity s WHERE s.phoneNumber = ?1")
    Optional<UserEntity> findCustomerByPhoneNumber(String phoneNumber);

    UserEntity findByEmail(String email);
}
