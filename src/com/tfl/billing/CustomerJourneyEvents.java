package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerJourneyEvents {
    private List<JourneyEvent> customerJourneyEvents;
    private Customer customer;
    private List<JourneyEvent> eventLog;

    public CustomerJourneyEvents(Customer customer,List<JourneyEvent> eventLog){
        customerJourneyEvents = new ArrayList<>();
        this.customer = customer;
        this.eventLog = eventLog;
    }

    public List<JourneyEvent> createCustomerJourneyEvents()
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
