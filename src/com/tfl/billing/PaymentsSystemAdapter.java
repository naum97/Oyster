package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.List;


public class PaymentsSystemAdapter implements GeneralPaymentsSystem {
    private final PaymentsSystem paymentsSystem;

    public PaymentsSystemAdapter(PaymentsSystem paymentsSystem) {
        this.paymentsSystem = paymentsSystem;
    }

    @Override
    public void charge(Customer customer, List<Journey> journeys, BigDecimal totalCost) {
        paymentsSystem.charge(customer,journeys,totalCost);
    }
}
