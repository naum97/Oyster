package com.tfl.billing;

import org.joda.time.DateTime;

import java.util.UUID;

public class JourneyStart extends JourneyEvent {
    public JourneyStart(UUID cardID, UUID readerID, DateTime dateTime)
    {
        super(cardID,readerID, dateTime);
    }
    public JourneyStart(UUID cardId, UUID readerId) {

        super(cardId, readerId);
    }
}
