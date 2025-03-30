package com.example.customerservice.repository;

import com.example.customerservice.model.CustomerNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerNotificationPreferenceRepository extends JpaRepository<CustomerNotificationPreferences,Long> {
    Optional<CustomerNotificationPreferences> findByCustomerId(Long id);
}
