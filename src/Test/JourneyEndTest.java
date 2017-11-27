package Test;

import com.tfl.billing.JourneyEnd;
import com.tfl.billing.JourneyEvent;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

/**
 * Created by User on 11/24/2017.
 */
public class JourneyEndTest {
    private final UUID cardID = UUID.randomUUID();
    private final UUID readerID = UUID.randomUUID();

    private JourneyEvent journey = new JourneyEnd(cardID,readerID);

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
        long time = System.currentTimeMillis();
        assertThat((double) time, is(closeTo((double) journey.time(), 0)));
    }

}
