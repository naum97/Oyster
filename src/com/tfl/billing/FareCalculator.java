package com.tfl.billing;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public class FareCalculator implements Fare {

    static public final BigDecimal LONG_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.70);
    static public final BigDecimal LONG_PEAK_JOURNEY_PRICE = new BigDecimal(3.80);
    static public final BigDecimal SHORT_OFF_PEAK_JOURNEY_PRICE = new BigDecimal(1.60);
    static public final BigDecimal SHORT_PEAK_JOURNEY_PRICE = new BigDecimal(2.90);

    private boolean isPeak = false;
    static public final BigDecimal PEAK_CAP = new BigDecimal(9.00);
    static public final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.00);

    @Override
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
        return roundToNearestPenny(customerTotal);
    }

    private BigDecimal costOfJourney(Journey journey) {
        BigDecimal journeyPrice;
        if(isLong(journey))
        {
            journeyPrice = LONG_OFF_PEAK_JOURNEY_PRICE;
            if(isPeak(journey))
            {
                isPeak = true;
                journeyPrice = LONG_PEAK_JOURNEY_PRICE;
            }
        } else
        {
            journeyPrice = SHORT_OFF_PEAK_JOURNEY_PRICE;
            if(isPeak(journey))
            {
                isPeak = true;
                journeyPrice = SHORT_PEAK_JOURNEY_PRICE;
            }
        }
        return journeyPrice;
    }
     private boolean isPeak(Journey journey) {
        return isPeak(journey.startTime()) || isPeak(journey.endTime());
    }

     private boolean isPeak(DateTime time) {
        DateTime date = new DateTime(time);
        int hour = date.getHourOfDay();
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }
     private boolean isLong(Journey journey)
    {
        return (journey.durationSeconds() / 60 >= 25);

    }
    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
