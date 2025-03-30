package com.example.customerservice.repository;

import com.example.customerservice.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Object> findByNationalId(String nationalId);

    @Query("""
     SELECT c FROM Customer c
     LEFT JOIN FETCH LoanLimit l ON l.customer.id = c.id
     LEFT JOIN FETCH CustomerNotificationPreferences cN ON cN.customer.id = c.id
     WHERE (:phoneNumber IS NULL OR c.phoneNumber = :phoneNumber)
     AND (:nationalId IS NULL OR c.nationalId = :nationalId)
     AND (:customerId IS NULL OR c.id = :customerId)
     AND (:active IS NULL OR c.active = :active)
    """)
    Page<Customer> getCustomers(@Param("phoneNumber") String phoneNumber,
                                @Param("nationalId") String nationalId,
                                @Param("active") Integer active,
                                @Param("customerId") Integer customerId,
                                Pageable pageable);

    boolean existsByNationalIdAndActive(@NotBlank(message = "national Id number cannot be blank") @Pattern(regexp = "^\\d{6,10}$", message = "National ID must be between 6 and 10 digits") String nationalId, int i);
}
