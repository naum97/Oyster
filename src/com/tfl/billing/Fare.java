package com.tfl.billing;
import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Fare {

    static public final BigDecimal LONG_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.70);
    static public final BigDecimal LONG_PEAK_JOURNEY_PRICE = new BigDecimal(3.80);
    static public final BigDecimal SHORT_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(1.60);
    static public final BigDecimal SHORT_PEAK_JOURNEY_PRICE = new BigDecimal(2.90);


    static public BigDecimal customerTotalCost(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = costOfJourney(journey);

            customerTotal = customerTotal.add(journeyPrice);
        }
        return customerTotal;
    }

    static private BigDecimal costOfJourney(Journey journey) {
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
    static private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    static private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
    static private boolean longJourney(Journey journey)
    {
        return (journey.durationSeconds() / 60 >= 25);

    }
}
