package com.tfl.billing;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.util.UUID;

public abstract class JourneyEvent {

    private final UUID cardId;
    private final UUID readerId;
    private long time;

    public JourneyEvent(UUID cardId, UUID readerId, DateTime dateTime)
    {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = dateTime.getMillis();
    }
    public JourneyEvent(UUID cardId, UUID readerId) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = DateTimeUtils.currentTimeMillis();
    }


    public UUID cardId() {
        return cardId;
    }

    public UUID readerId() {
        return readerId;
    }

    public long time() {
        return time;
    }
    //for testing
    public void setTime(DateTime dateTime){this.time = dateTime.getMillis();}
}
