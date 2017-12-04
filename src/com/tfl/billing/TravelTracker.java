package com.tfl.billing;

import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

public class TravelTracker {


    private Database customerDatabase;
    private GeneralPaymentsSystem paymentsSystem;
    private Fare fareCalculator = new Fare();
    private EventLog eventLogger = new EventLog();

    public TravelTracker(EventLog eventLogger) {
        this.customerDatabase = CustomerDatabaseAdapter.getInstance();
        this.paymentsSystem = PaymentsSystemAdapter.getInstance();
        this.eventLogger = eventLogger;
    }


    public TravelTracker(Database custDatabase, GeneralPaymentsSystem generalPaymentsSystem, EventLog eventLogger) {
        this.customerDatabase = custDatabase;
        this.paymentsSystem = generalPaymentsSystem;
        this.eventLogger = eventLogger;
    }

    public void chargeAccounts() {

        List<JourneyEvent> eventLog = eventLogger.getEventLog();
        for (Customer customer : customerDatabase.getCustomers()) {
            List<JourneyEvent> customerJourneyEvents = new CustomerJourneyEvents(customer, eventLog).createCustomerJourneyEvents();
            List<Journey> journeys = new Journeys(customerJourneyEvents).createListOfJourneysForCustomer();
            BigDecimal customerTotalCost = fareCalculator.customerTotalCost(journeys);
            paymentsSystem.charge(customer, journeys, roundToNearestPenny(customerTotalCost));
        }
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}