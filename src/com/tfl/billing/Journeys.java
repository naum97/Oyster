package com.tfl.billing;

import java.util.ArrayList;
import java.util.List;


public class Journeys {
    private List<Journey> journeys = new ArrayList<>();
    private List<JourneyEvent> customerJourneyEvents;

    public Journeys(List<JourneyEvent> customerJourneyEvents) {
        this.customerJourneyEvents = customerJourneyEvents;
    }

    public List<Journey> createListOfJourneysForCustomer()
    {
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
        return journeys;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }
}
