package com.tfl.billing;


import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Journey {

    private final JourneyEvent start;
    private final JourneyEvent end;

    public Journey(JourneyEvent start, JourneyEvent end) {
        this.start = start;
        this.end = end;
    }

    public UUID originId() {
        return start.readerId();
    }


    public UUID destinationId() {
        return end.readerId();
    }


    public String formattedStartTime() {
        return format(start.time());
    }


    public String formattedEndTime() {
        return format(end.time());
    }


    public DateTime startTime() {
        return new DateTime(start.time());
    }


    public DateTime endTime() {
        return new DateTime(end.time());
    }


    public int durationSeconds() {
        return (int) ((end.time() - start.time()) / 1000);
    }


    public String durationMinutes() {
        return "" + durationSeconds() / 60 + ":" + durationSeconds() % 60;
    }

    private String format(long time) {
        return SimpleDateFormat.getInstance().format(new Date(time));
    }

    public JourneyEvent getStart() {
        return start;
    }

    public JourneyEvent getEnd() {
        return end;
    }
}
