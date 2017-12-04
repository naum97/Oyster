package Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.*;

import com.tfl.billing.JourneyEvent;
import com.tfl.billing.JourneyStart;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

import java.util.UUID;


public class JourneyStartTest {
    private final UUID cardID = UUID.randomUUID();
    private final UUID readerID = UUID.randomUUID();

    private JourneyEvent journey = new JourneyStart(cardID,readerID);

    @Test
    public void assertReturnOfCardID()
    {
        assertThat(journey.cardId(), is(cardID));
    }

    @Test
    public void assertReturnOfReaderID()
    {
        assertThat(journey.readerId(), is(readerID));
    }

    @Test
    public void assertReturnOfTime()
    {
        long time = DateTimeUtils.currentTimeMillis();
        assertThat((double) time, is(closeTo((double)journey.time(),20)));
    }

}
