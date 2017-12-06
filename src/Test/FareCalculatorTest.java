package Test;

import com.tfl.billing.*;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tfl.billing.FareCalculator.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class FareCalculatorTest {
    public FareCalculator fareCalculatorCalculator = new FareCalculator();
    private List<Journey> journeys;

    public FareCalculatorTest()  {
    }

    private UUID cardID = UUID.randomUUID();
    private UUID originReaderID = UUID.randomUUID();

    private UUID destinationReaderID = UUID.randomUUID();

    private final Journey shortOffPeakJourney = createJourneyForFareTest(21,0,20);
    private final Journey shortPeakJourney = createJourneyForFareTest(17,0,20);
    private final Journey longOffPeakJourney = createJourneyForFareTest(21,0,40);
    private final Journey longPeakJourney = createJourneyForFareTest(17,0,40);
    private final Journey journeyStartingAtOffPeakAndEndingAtPeak = createJourneyForFareTest(16,30,100);



    private Journey createJourneyForFareTest(int startHour, int startMinutes, int durationMinutes)  {

        DateTime time = new DateTime().withHourOfDay(startHour).withMinuteOfHour(startMinutes);

        JourneyEvent startOfJourney = new JourneyStart(cardID,originReaderID, time);
        JourneyEvent endOfJourney = new JourneyEnd(cardID, destinationReaderID,time.plusMinutes(durationMinutes));

        return new Journey(startOfJourney,endOfJourney);
    }

    @Before
    public void setUp() throws Exception {
        journeys = new ArrayList<>();
    }

    @Test
    public void checkCostOfShortOffPeakJourney()  {
        journeys.add(shortOffPeakJourney);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(SHORT_OFF_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfOffPeakLongJourney()  {
        journeys.add(longOffPeakJourney);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(LONG_OFF_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfPeakShortJourney()  {
        journeys.add(shortPeakJourney);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(SHORT_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfPeakLongJourney()  {
        journeys.add(longPeakJourney);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(LONG_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfJourneyStartingAtOffPeakAndEndingAtPeak()  {
        journeys.add(journeyStartingAtOffPeakAndEndingAtPeak);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(LONG_PEAK_JOURNEY_PRICE));
    }
    @Test
    public void checkCostOfMultipleJourneysLessThanCaps()  {
        journeys.add(shortPeakJourney);
        journeys.add(shortOffPeakJourney);
        assertThat(fareCalculatorCalculator.customerTotalCost(journeys), is(SHORT_PEAK_JOURNEY_PRICE.add(SHORT_OFF_PEAK_JOURNEY_PRICE)));
    }
    @Test
    public void  checkOffPeakCap()  {
        //all journeys are in off peak time, so the cap should be set at 7.00

        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(shortOffPeakJourney);

        assertTrue(fareCalculatorCalculator.customerTotalCost(journeys).doubleValue() == OFF_PEAK_CAP.doubleValue());
    }

    @Test
    public void checkPeakCap()
    { // one journey is peak, the rest of them are off peak, so the cap should be set at 9.00
        journeys.add(longPeakJourney);
        journeys.add(shortOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        journeys.add(longOffPeakJourney);
        assertTrue(fareCalculatorCalculator.customerTotalCost(journeys).doubleValue() == PEAK_CAP.doubleValue());
    }


}