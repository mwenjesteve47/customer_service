package com.example.customerservice.factories;


import com.example.customerservice.enums.TenureType;
import com.example.customerservice.enums.TenureUnit;
import com.example.customerservice.model.LoanProduct;
import com.example.customerservice.repository.LoanProductRepository;
import com.example.customerservice.testHelpers.SpringContext;

public class LoanProductFactory {
    public static LoanProduct create() {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }
    public static LoanProduct create(Integer active) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanProductRepository.class).deleteAll();
    }
}
