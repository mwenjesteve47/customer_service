package com.example.customerservice.repository;

import com.example.customerservice.model.Customer;
import com.example.customerservice.model.LoanLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LoanLimitRepository extends JpaRepository<LoanLimit, Integer> {
   Optional<LoanLimit> findByCustomerId(Long id);

}
