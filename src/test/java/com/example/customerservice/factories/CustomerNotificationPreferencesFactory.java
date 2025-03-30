package com.example.customerservice.factories;

import com.example.customerservice.model.Customer;
import com.example.customerservice.model.CustomerNotificationPreferences;
import com.example.customerservice.model.LoanLimit;
import com.example.customerservice.repository.CustomerNotificationPreferenceRepository;
import com.example.customerservice.repository.LoanLimitRepository;
import com.example.customerservice.testHelpers.SpringContext;

import java.math.BigDecimal;

public class CustomerNotificationPreferencesFactory {

    public static CustomerNotificationPreferences create(Customer customer) {
        CustomerNotificationPreferences customerNotificationPreferences = new CustomerNotificationPreferences();
        customerNotificationPreferences.setCustomer(customer);
        customerNotificationPreferences.setEmailEnabled(Boolean.TRUE);
        customerNotificationPreferences.setPushNotificationEnabled(Boolean.TRUE);
        customerNotificationPreferences.setSmsEnabled(Boolean.TRUE);
        return SpringContext.getBean(CustomerNotificationPreferenceRepository.class).save(customerNotificationPreferences);
    }

    public static void deleteAll() {
        SpringContext.getBean(CustomerNotificationPreferenceRepository.class).deleteAll();
    }
}
