package Test;

import com.tfl.billing.Journey;
import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by User on 11/26/2017.
 */
public class JourneyTest {
    private static final UUID cardID = UUID.randomUUID();
    private static final UUID originReaderID = UUID.randomUUID();
    private static final UUID destinationReaderID = UUID.randomUUID();

    private static JourneyEvent startOfJourney;
    private static JourneyEvent endOfJourney;
    private static Journey journey;

    public static Journey createJourney(int durationMinutes) throws InterruptedException {
        startOfJourney = new JourneyStart(cardID,originReaderID);
        DateTimeUtils.setCurrentMillisOffset(durationMinutes*60*1000);
        endOfJourney = new JourneyEnd(cardID, destinationReaderID);
        DateTimeUtils.setCurrentMillisSystem();
        return journey = new Journey(startOfJourney,endOfJourney);
    }

    @Test
    public void assertOriginId() throws InterruptedException {
        createJourney(0);
        assertEquals(journey.originId(),(startOfJourney.readerId()));
    }
    @Test
    public void assertDestinationId() throws InterruptedException {
        createJourney(0);
        assertEquals(journey.destinationId(),(endOfJourney.readerId()));
    }
    @Test
    public void assertStartTime() throws InterruptedException
    {
        createJourney(0);
        assertThat(journey.startTime(), is(new Date(startOfJourney.time())));
    }

    @Test
    public void assertEndTimeTest() throws InterruptedException
    {
        createJourney(0);
        assertThat(journey.endTime(), is(new Date(endOfJourney.time())));
    }
    @Test
    public void checkJourneyDurationSeconds() throws InterruptedException {
        createJourney(1000);
        assertThat(journey.durationSeconds(), is(1000*60));

    }
    @Test
    public void checkJourneyDurationMinutes() throws InterruptedException {
        createJourney(3);
        assertThat(journey.durationMinutes(), is("3:0"));
    }
    @Test
    public void checkFormattedStartTimeTest() throws InterruptedException
    {
        createJourney(0);
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(new Date(startOfJourney.time()))));
    }

    @Test
    public void checkFormattedEndTimeTest() throws InterruptedException
    {
        createJourney(0);
        assertThat(journey.formattedEndTime(), is(SimpleDateFormat.getInstance().format(new Date(endOfJourney.time()))));
    }
}