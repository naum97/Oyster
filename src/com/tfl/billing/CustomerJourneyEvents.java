package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.List;


public class CustomerJourneyEvents {
    private List<JourneyEvent> customerJourneyEvents;

    public CustomerJourneyEvents()
    {
        customerJourneyEvents = new ArrayList<>();
    }

    public List<JourneyEvent> createCustomerJourneyEvents(Customer customer, List<JourneyEvent> eventLog)
    {
        for(JourneyEvent event : eventLog)
        {
            if(event.cardId().equals(customer.cardId()))
            {
                customerJourneyEvents.add(event);
            }
        }
        return customerJourneyEvents;
    }
}
