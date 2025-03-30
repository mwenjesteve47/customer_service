package com.example.customerservice.repository;

import com.example.customerservice.model.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductRepository extends JpaRepository<LoanProduct,Long> {
}
