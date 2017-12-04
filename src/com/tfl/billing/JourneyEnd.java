package com.tfl.billing;

import org.joda.time.DateTime;

import java.util.UUID;

public class JourneyEnd extends JourneyEvent {
    public JourneyEnd(UUID cardID, UUID readerID, DateTime dateTime)
    {
        super(cardID,readerID, dateTime);
    }

    public JourneyEnd(UUID cardId, UUID readerId) {
        super(cardId, readerId);
    }
}
