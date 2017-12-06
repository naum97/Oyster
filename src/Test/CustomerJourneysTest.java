package Test;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
This test will check whether the helper classes CustomerJourneyEvents and JourneyListCreator
                      properly create a list of JourneyListCreator for a single customer
*/
public class CustomerJourneysTest {
    @Test
    public void checkProperCreationOfCustomerJourneysEventList()
    {
        OysterCard myCard = new OysterCard(new CustomerDatabaseAdapter(CustomerDatabase.getInstance()).getCustomers().get(0).cardId().toString());
        OysterCard myCard2 = new OysterCard(new CustomerDatabaseAdapter(CustomerDatabase.getInstance()).getCustomers().get(1).cardId().toString());
        Customer customer = new Customer("Naum", myCard);
        OysterCardReader startReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader endReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        List<JourneyEvent> testEventLog = new ArrayList<>();

        JourneyEvent firstStartJourney = new JourneyStart(myCard.id(), startReader.id());
        JourneyEvent firstEndJourney = new JourneyEnd(myCard.id(), endReader.id());

        testEventLog.add(firstStartJourney);
        testEventLog.add(firstEndJourney);

        JourneyEvent secondStartJourney = new JourneyStart(myCard2.id(), startReader.id()); // this should not be in our customer journey list
        testEventLog.add(secondStartJourney);

        assertTrue(testEventLog.size() == 3);

        List<JourneyEvent> customerJourneyEventsList = new CustomerJourneyEvents().createCustomerJourneyEvents(customer, testEventLog);

        assertTrue(testEventLog.get(0).cardId() == customerJourneyEventsList.get(0).cardId());
        assertTrue(testEventLog.get(1).cardId() == customerJourneyEventsList.get(1).cardId());
        assertFalse(customerJourneyEventsList.contains(secondStartJourney));
        assertTrue(customerJourneyEventsList.size() == 2);
    }
    @Test
    public void checkProperCreationOfListOfJourneysFromCustomerJourneyEventList()
    {
        OysterCard myCard = new OysterCard(new CustomerDatabaseAdapter(CustomerDatabase.getInstance()).getCustomers().get(0).cardId().toString());
        Customer customer = new Customer("Naum", myCard);
        OysterCardReader startReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader endReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        List<JourneyEvent> testEventLog = new ArrayList<>();

        JourneyEvent firstStartJourney = new JourneyStart(myCard.id(), startReader.id());
        JourneyEvent firstEndJourney = new JourneyEnd(myCard.id(), endReader.id());

        testEventLog.add(firstStartJourney);
        testEventLog.add(firstEndJourney);

        List<JourneyEvent> customerJourneyEventsList = new CustomerJourneyEvents().createCustomerJourneyEvents(customer, testEventLog);
        List<Journey> customerJourneys = new JourneyListCreator(customerJourneyEventsList).createListOfJourneysForCustomer();
/*
        List of journeys should have one journey, with the same originID and destinationID as the instances of JourneyStart and JourneyEnd
        The cardID should be the same as well
*/
        assertTrue(customerJourneys.get(0).originId().equals(customerJourneyEventsList.get(0).readerId()));
        assertTrue(customerJourneys.get(0).destinationId().equals(customerJourneyEventsList.get(1).readerId()));

        assertTrue(customerJourneys.get(0).getStart().cardId().equals(customerJourneyEventsList.get(0).cardId()));
        assertTrue(customerJourneys.get(0).getEnd().cardId().equals(customerJourneyEventsList.get(1).cardId()));
        assertTrue(customerJourneys.size() == 1);
    }
}
