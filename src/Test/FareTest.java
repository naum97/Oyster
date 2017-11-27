package Test;

import com.tfl.billing.Fare;
import com.tfl.billing.Journey;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by User on 11/26/2017.
 */
public class FareTest {

    //need to work out how to test peak and off peak journeys

    @Test
    public void checkOffPeakShortJourney() throws InterruptedException {
        List<Journey> journeys = new ArrayList<>();
        journeys.add(JourneyTest.createJourney(20));
        assertThat(Fare.customerTotalCost(journeys), is(Fare.SHORT_OFF_PEAK_JOURNEY_PRICE));
    }

}