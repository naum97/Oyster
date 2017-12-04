package Test;

import com.tfl.billing.*;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tfl.billing.Fare.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class FareTest {
    public Fare fareCalculator = new Fare();
    public FareTest() throws InterruptedException {
    }

    private UUID cardID = UUID.randomUUID();
    private UUID originReaderID = UUID.randomUUID();

    private UUID destinationReaderID = UUID.randomUUID();

    private final Journey shortOffPeakJourney = createJourneyForFareTest(21,0,20);
    private final Journey shortPeakJourney = createJourneyForFareTest(17,0,20);
    private final Journey longOffPeakJourney = createJourneyForFareTest(21,0,40);
    private final Journey longPeakJourney = createJourneyForFareTest(17,0,40);
    private final Journey journeyStartingAtOffPeakAndEndingAtPeak = createJourneyForFareTest(16,30,100);



    public Journey createJourneyForFareTest(int startHour, int startMinutes, int durationMinutes) throws InterruptedException {

        DateTime time = new DateTime().withHourOfDay(startHour).withMinuteOfHour(startMinutes);

        JourneyEvent startOfJourney = new JourneyStart(cardID,originReaderID, time);
        JourneyEvent endOfJourney = new JourneyEnd(cardID, destinationReaderID,time.plusMinutes(durationMinutes));

        return new Journey(startOfJourney,endOfJourney);
    }
    @Test
    public void checkCostOfShortOffPeakJourney() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(shortOffPeakJourney);
        assertThat(fareCalculator.customerTotalCost(journeys), is(SHORT_OFF_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfOffPeakLongJourney() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(longOffPeakJourney);
        assertThat(fareCalculator.customerTotalCost(journeys), is(LONG_OFF_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfPeakShortJourney() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(shortPeakJourney);
        assertThat(fareCalculator.customerTotalCost(journeys), is(SHORT_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfPeakLongJourney() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(longPeakJourney);
        assertThat(fareCalculator.customerTotalCost(journeys), is(LONG_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfJourneyStartingAtOffPeakAndEndingAtPeak() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(journeyStartingAtOffPeakAndEndingAtPeak);
        assertThat(fareCalculator.customerTotalCost(journeys), is(LONG_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfMultipleJourneysLessThanCaps() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(shortPeakJourney);
        journeys.add(shortOffPeakJourney);
        assertThat(fareCalculator.customerTotalCost(journeys), is(SHORT_PEAK_JOURNEY_PRICE.add(SHORT_OFF_PEAK_JOURNEY_PRICE)));
    }
    @Test
    public void  checkOffPeakCap() throws InterruptedException {
        //all journeys are in off peak time, so the cap should be set at 7.00

        List<Journey> journeys = new ArrayList<>();
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);

        assertTrue(fareCalculator.customerTotalCost(journeys).doubleValue() == OFF_PEAK_CAP.doubleValue());
    }

    @Test
    public void checkPeakCap()
    { // one journey is peak, the rest of them are off peak, so the cap should be set at 9.00
        List<Journey> journeys = new ArrayList<>();
        journeys.add(longPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        assertTrue(fareCalculator.customerTotalCost(journeys).doubleValue() == PEAK_CAP.doubleValue());
    }


}