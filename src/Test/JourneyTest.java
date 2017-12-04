package Test;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class JourneyTest {
    private static final UUID cardID = UUID.randomUUID();
    private static final UUID originReaderID = UUID.randomUUID();
    private static final UUID destinationReaderID = UUID.randomUUID();

    private static JourneyEvent startOfJourney;
    private static JourneyEvent endOfJourney;
    private static Journey journey;

    private Journey createJourneyForJourneyDurationTest(int durationMinutes) throws InterruptedException {
        startOfJourney = new JourneyStart(cardID, originReaderID);
        DateTimeUtils.setCurrentMillisOffset(durationMinutes * 60 * 1000);
        endOfJourney = new JourneyEnd(cardID, destinationReaderID);
        DateTimeUtils.setCurrentMillisSystem();
        return journey = new Journey(startOfJourney, endOfJourney);
    }
    @Before
    public void createJourney() throws InterruptedException {
        createJourneyForJourneyDurationTest(15);
    }

    @Test
    public void assertOriginId() throws InterruptedException {
        assertEquals(journey.originId(),(startOfJourney.readerId()));
    }
    @Test
    public void assertDestinationId() throws InterruptedException {
        assertEquals(journey.destinationId(),(endOfJourney.readerId()));
    }
    @Test
    public void assertStartTime() throws InterruptedException
    {
        assertThat(journey.startTime(), is(new DateTime(startOfJourney.time())));
    }

    @Test
    public void assertEndTimeTest() throws InterruptedException
    {
        assertThat(journey.endTime(), is(new DateTime(endOfJourney.time())));
    }
    @Test
    public void checkJourneyDurationSeconds() throws InterruptedException {
        assertThat(journey.durationSeconds(), is(15*60));

    }
    @Test
    public void checkJourneyDurationMinutes() throws InterruptedException {
        assertThat(journey.durationMinutes(), is("15:0"));
    }
    @Test
    public void checkFormattedStartTimeTest() throws InterruptedException
    {
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(new Date(startOfJourney.time()))));
    }

    @Test
    public void checkFormattedEndTimeTest() throws InterruptedException {
        assertThat(journey.formattedEndTime(), is(SimpleDateFormat.getInstance().format(new Date(endOfJourney.time()))));
    }
}