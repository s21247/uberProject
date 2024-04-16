package com.example.app.GeoIP;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GeoIPRepository extends JpaRepository<GeoIPEntity, Long> {

    @Query("SELECT s FROM GeoIPEntity s WHERE s.id = ?1")
    Optional<GeoIPEntity> findLocationById(Long id);

    @Query("SELECT l FROM GeoIPEntity l JOIN UserEntity s ON l.id = s.id WHERE l.isActive = true AND s.isDriver = false")
    List<GeoIPEntity> findAllActiveClientUsers();

    @Query("SELECT l FROM GeoIPEntity l JOIN UserEntity s ON l.id = s.id WHERE l.isActive AND s.isDriver = true")
    List<GeoIPEntity> findAllActiveDriverUsers();
}
