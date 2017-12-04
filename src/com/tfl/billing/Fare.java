package com.tfl.billing;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class Fare {

    static public final BigDecimal LONG_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.70);
    static public final BigDecimal LONG_PEAK_JOURNEY_PRICE = new BigDecimal(3.80);
    static public final BigDecimal SHORT_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(1.60);
    static public final BigDecimal SHORT_PEAK_JOURNEY_PRICE = new BigDecimal(2.90);

    private boolean isPeak = false;
    static public final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    static public final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);

    public BigDecimal customerTotalCost(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);
        for (Journey journey : journeys) {
            BigDecimal journeyPrice = costOfJourney(journey);

            customerTotal = customerTotal.add(journeyPrice);

            if(isPeak && customerTotal.compareTo(PEAK_CAP) == 1)
            {
                customerTotal = PEAK_CAP;
            }
            else if(!isPeak && customerTotal.compareTo(OFF_PEAK_CAP) == 1)
            {
                customerTotal = OFF_PEAK_CAP;
            }
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
                isPeak = true;
                journeyPrice = LONG_PEAK_JOURNEY_PRICE;
            }
        } else
        {
            journeyPrice = SHORT_OFF_PEAK_JOURNEY_PRICE;
            if(peak(journey))
            {
                isPeak = true;
                journeyPrice = SHORT_PEAK_JOURNEY_PRICE;
            }
        }
        return journeyPrice;
    }
     private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

     private boolean peak(DateTime time) {
        DateTime date = new DateTime(time);
        int hour = date.getHourOfDay();
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
     private boolean longJourney(Journey journey)
    {
        return (journey.durationSeconds() / 60 >= 25);

    }
}
