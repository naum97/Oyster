package com.tfl.billing;
import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Fare {
    private final List<JourneyEvent> eventLog;

    static final BigDecimal LONG_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.70);
    static final BigDecimal LONG_PEAK_JOURNEY_PRICE = new BigDecimal(3.80);
    static final BigDecimal SHORT_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(1.60);
    static final BigDecimal SHORT_PEAK_JOURNEY_PRICE = new BigDecimal(2.90);

    public Fare(TravelTracker travelTracker){
        eventLog = travelTracker.getEventLog();
    }
    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }
    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<JourneyEvent>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }

        List<Journey> journeys = new ArrayList<Journey>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }

        BigDecimal customerTotalCost = customerTotalCost(journeys);

        chargeIndividualCustomer(customer, journeys, customerTotalCost);
    }
    private BigDecimal customerTotalCost(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = costOfJourney(journey);

            customerTotal = customerTotal.add(journeyPrice);
        }
        return customerTotal;
    }
    private BigDecimal costOfJourney(Journey journey) {
        BigDecimal journeyPrice;
        if(longJourney(journey))
        {
            journeyPrice = LONG_OFF_PEAK_JOURNEY_PRICE;
            if(peak(journey))
            {
                journeyPrice = LONG_PEAK_JOURNEY_PRICE;
            }
        } else
        {
            journeyPrice = SHORT_OFF_PEAK_JOURNEY_PRICE;
            if(peak(journey))
            {
                journeyPrice = SHORT_PEAK_JOURNEY_PRICE;
            }
        }
        return journeyPrice;
    }
    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
    private boolean longJourney(Journey journey)
    {
        return (journey.durationSeconds() / 60 >= 25);

    }
    private void chargeIndividualCustomer(Customer customer, List<Journey> journeys, BigDecimal customerTotal) {
        PaymentsSystem.getInstance().charge(customer, journeys, roundToNearestPenny(customerTotal));
    }

    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
