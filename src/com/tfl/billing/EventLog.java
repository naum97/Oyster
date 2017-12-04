package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.oyster.ScanListener;
import com.tfl.external.CustomerDatabase;

import java.util.*;

public class EventLog implements ScanListener {
    private Set<UUID> currentlyTravelling;
    private List<JourneyEvent> eventLog;

    public EventLog()
    {
        this.currentlyTravelling = new HashSet<>();
        eventLog = new ArrayList<>();
    }


    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (currentlyTravelling.contains(cardId)) {
            eventLog.add(new JourneyEnd(cardId, readerId));
            currentlyTravelling.remove(cardId);
        } else {
            if (CustomerDatabaseAdapter.getInstance().isRegisteredId(cardId)) {
                currentlyTravelling.add(cardId);
                eventLog.add(new JourneyStart(cardId, readerId));
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }
    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    public List<JourneyEvent> getEventLog() {
        return eventLog;
    }

    public Set<UUID> getCurrentlyTravelling() {
        return currentlyTravelling;
    }
}
