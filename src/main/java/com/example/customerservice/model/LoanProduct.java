package com.example.customerservice.model;


import com.example.customerservice.enums.TenureType;
import com.example.customerservice.enums.TenureUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loanProducts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private TenureType tenureType;

    private Integer tenureValue;

    @Enumerated(EnumType.STRING)
    private TenureUnit tenureUnit;

}
