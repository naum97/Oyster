package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import java.math.BigDecimal;
import java.util.*;


public class TravelTracker implements ScanListener {
    private final List<JourneyEvent> eventLog;
    private final Set<UUID> currentlyTravelling;
    private final Database customerDatabase;
    private final GeneralPaymentsSystem paymentSystem;
    private final Fare costCalculator;


    public TravelTracker() {
        this.eventLog = new ArrayList<>();
        this.currentlyTravelling = new HashSet<>();
        this.customerDatabase = new CustomerDatabaseAdapter(CustomerDatabase.getInstance());
        this.paymentSystem = new PaymentsSystemAdapter(PaymentsSystem.getInstance());
        this.costCalculator = new FareCalculator();
    }
    /*alternate constructor for testing*/
    public TravelTracker(List<JourneyEvent> eventLog,
                         Set<UUID> currentlyTravelling,
                         Database customerDatabase,
                         GeneralPaymentsSystem paymentSystem,
                         Fare journeyCost) {
        this.eventLog = eventLog;
        this.currentlyTravelling = currentlyTravelling;
        this.customerDatabase = customerDatabase;
        this.paymentSystem = paymentSystem;
        this.costCalculator = journeyCost;
    }

    public void chargeAccounts() {
        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            chargeIndividualCustomer(customer);
        }
    }

    private void chargeIndividualCustomer(Customer customer) {
        List<JourneyEvent> customerJourneyEventsList = new CustomerJourneyEvents().createCustomerJourneyEvents(customer, eventLog);
        List<Journey> journeys = new JourneyListCreator(customerJourneyEventsList).createListOfJourneysForCustomer();
        BigDecimal customerTotal = costCalculator.customerTotalCost(journeys);
        paymentSystem.charge(customer, journeys, customerTotal);
    }
    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            // if the person is not travelling, make a JourneyStart
            if (customerDatabase.isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }
}