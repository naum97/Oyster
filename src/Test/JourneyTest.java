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

    private Journey createJourneyForJourneyDurationTest(int durationMinutes){
        DateTime dateTime = new DateTime();
        startOfJourney = new JourneyStart(cardID, originReaderID, dateTime);
        endOfJourney = new JourneyEnd(cardID, destinationReaderID, dateTime.plusMinutes(durationMinutes));
        return journey = new Journey(startOfJourney, endOfJourney);
    }
    @Before
    public void createJourney() {
        createJourneyForJourneyDurationTest(15);
    }

    @Test
    public void assertOriginId(){
        assertEquals(journey.originId(),(startOfJourney.readerId()));
    }
    @Test
    public void assertDestinationId() {
        assertEquals(journey.destinationId(),(endOfJourney.readerId()));
    }
    @Test
    public void assertStartTime()
    {
        assertThat(journey.startTime(), is(new DateTime(startOfJourney.time())));
    }

    @Test
    public void assertEndTimeTest()
    {
        assertThat(journey.endTime(), is(new DateTime(endOfJourney.time())));
    }
    @Test
    public void checkJourneyDurationSeconds()  {
        assertThat(journey.durationSeconds(), is(15*60));

    }
    @Test
    public void checkJourneyDurationMinutes()  {
        assertThat(journey.durationMinutes(), is("15:0"));
    }
    @Test
    public void checkFormattedStartTimeTest()
    {
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(new Date(startOfJourney.time()))));
    }

    @Test
    public void checkFormattedEndTimeTest()  {
        assertThat(journey.formattedEndTime(), is(SimpleDateFormat.getInstance().format(new Date(endOfJourney.time()))));
    }
}