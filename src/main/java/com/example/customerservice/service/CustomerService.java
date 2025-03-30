package com.example.customerservice.service;

import com.example.customerservice.config.AppConfig;
import com.example.customerservice.dto.*;
import com.example.customerservice.exception.ResourceNotFoundException;
import com.example.customerservice.helper.LoanLimitHelper;
import com.example.customerservice.model.Customer;
import com.example.customerservice.model.CustomerNotificationPreferences;
import com.example.customerservice.model.LoanLimit;
import com.example.customerservice.repository.CustomerNotificationPreferenceRepository;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.repository.LoanLimitRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final LoanLimitRepository loanLimitRepository;
    private final AppConfig appConfig;
    private final CustomerNotificationPreferenceRepository customerPreferencesRepository;
    public static final String SORT_BY_DATE_CREATED = "dateCreated";


    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequest request) {
        log.info("Create customer request: {}", request);
        if (customerRepository.existsByNationalIdAndActive(request.getNationalId(),1)) {
            throw new IllegalArgumentException("Customer with this National ID already exists.");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNationalId(request.getNationalId());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setDateCreated(LocalDateTime.now());
        customer.setActive(1);
        customer.setIncome(request.getIncome());

        customerRepository.save(customer);

        // Assign an initial loan limit
        LoanLimit loanLimit = LoanLimit.builder()
                .customer(customer)
                .creditLimit(appConfig.getDefaultLoanLimit())  // Assign default loan limit
                .availableLimit(BigDecimal.ZERO)
                .currency(request.getCurrency())
                .build();

        // Create and associate preferences
        CustomerNotificationPreferences preferences = new CustomerNotificationPreferences();
        preferences.setCustomer(customer);
        preferences.setSmsEnabled(request.getCustomerNotificationPreferencesDto().isSmsEnabled());
        preferences.setEmailEnabled(request.getCustomerNotificationPreferencesDto().isEmailEnabled());
        preferences.setPushNotificationEnabled(request.getCustomerNotificationPreferencesDto().isPushNotificationEnabled());

        // Save preferences
        var customerPreference = customerPreferencesRepository.save(preferences);

        var loanLimits = loanLimitRepository.save(loanLimit);
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .nationalId(customer.getNationalId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .income(customer.getIncome())
                .loanLimits(LoanLimitsDto.builder()
                        .creditLimit(loanLimits.getCreditLimit())
                        .availableLimit(loanLimits.getAvailableLimit())
                        .currency(loanLimits.getCurrency())
                        .build())
                .customerPreferences(CustomerNotificationPreferencesDto.builder()
                        .emailEnabled(customerPreference.isEmailEnabled())
                        .smsEnabled(customerPreference.isSmsEnabled())
                        .pushNotificationEnabled(customerPreference.isPushNotificationEnabled())
                        .build()).build();
    }


    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id:  " + customerId + " not found"));
    }

    public ApiResponse<?>  deactivateCustomer(Long customerId) {
        var customer = getCustomerById(customerId);
        if (customer.getActive() == 0) {
            return new ApiResponse<>(false, "Customer is already inactive.");
        }

        customer.setDateCreated(LocalDateTime.now());
        customer.setActive(0);
        customer.setDateModified(LocalDateTime.now());

        customerRepository.save(customer);
        return new ApiResponse<>(true, "successfully deactivated loan product: "+customer.getLastName());

    }

    public void setLoanLimit(Long customerId, LoanLimitsDto loanLimitsDto) {
        LoanLimit loanLimit = loanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit not found for customer"));
        var customer = getCustomerById(customerId);
        loanLimit.setAvailableLimit(loanLimitsDto.getAvailableLimit());
        loanLimit.setCreditLimit(loanLimitsDto.getCreditLimit());
        loanLimit.setCurrency(customer.getCurrency());
        loanLimitRepository.save(loanLimit);
    }

    public ApiResponse<?> getCustomers(@Valid CustomerFilterRequest request) {
        Sort sort = Sort.by(SORT_BY_DATE_CREATED).descending();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        // Log query parameters
        log.info("Fetching loan products with filters - phoneNumber: {}, nationalID: {}, Active: {}, ID: {}, Page: {}, Size: {}",
                request.getPhoneNumber(),
                request.getNationalId(),
                request.getActive(),
                request.getId(),
                request.getPage(),
                request.getSize()
        );

        // Filter
        Page<Customer> customerPage = customerRepository.getCustomers(
                request.getPhoneNumber(),
                request.getNationalId(),
                1,
                request.getId(),
                pageable
        );

        // Transform Customer entities into DTOs
        List<CustomerResponseDto> customerResponses = customerPage.getContent().stream()
                .map(customer -> {
                    // Fetch loan limits
                    LoanLimit loanLimits = loanLimitRepository.findByCustomerId(customer.getId())
                            .orElse(null);
                    var loanLimitsDto = LoanLimitsDto.builder().availableLimit(loanLimits.getAvailableLimit())
                            .creditLimit(loanLimits.getCreditLimit()).currency(loanLimits.getCurrency()).build();

                    // Fetch customer preferences
                    var preferences = customerPreferencesRepository
                            .findByCustomerId(customer.getId()).orElse(null);
                    var customerNotificationPreference = CustomerNotificationPreferencesDto.builder()
                            .pushNotificationEnabled(preferences.isPushNotificationEnabled())
                            .emailEnabled(preferences.isEmailEnabled())
                            .smsEnabled(preferences.isSmsEnabled()).build();

                    return new CustomerResponseDto(
                            customer.getId(),
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getEmail(),
                            customer.getPhoneNumber(),
                            customer.getNationalId(),
                            customer.getIncome(),
                            loanLimitsDto,
                            customerNotificationPreference
                    );
                })
                .collect(Collectors.toList());

        return new ApiResponse<>(true, "Customers retrieved successfully", new PaginateObjectResponse<>(
                customerResponses,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isLast()));
    }
}
