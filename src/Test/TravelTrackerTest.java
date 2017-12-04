package Test;

import com.tfl.billing.*;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;


import com.oyster.*;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.*;

import com.oyster.OysterCard;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TravelTrackerTest {

    private OysterCardReader startReader = OysterReaderLocator.atStation(Station.MARYLEBONE);
    private OysterCardReader endReader = OysterReaderLocator.atStation(Station.BAKER_STREET);


    private final OysterCard registeredCard = new OysterCard(CustomerDatabaseAdapter.getInstance().getCustomers().get(0).cardId().toString());

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private Database mockCustomerDatabase = context.mock(Database.class);
    private GeneralPaymentsSystem mockPaymentsSystem = context.mock(GeneralPaymentsSystem.class);
    private EventLog eventLogger;
    private List<JourneyEvent> eventLog;
    private List<JourneyEvent> customerJourneyEventsList;

    @Before
    public void initialize() {
        eventLogger = new EventLog();
        eventLogger.connect(startReader, endReader);
    }

    private List<JourneyEvent> createCustomerJourneyEventsListForTesting() {
        startReader.touch(registeredCard);
        endReader.touch(registeredCard);

        Customer customer = new Customer("Naum Anteski", registeredCard);
        String fakeOysterId = CustomerDatabaseAdapter.getInstance().getCustomers().get(5).cardId().toString();

        OysterCard oysterCard = new OysterCard(fakeOysterId);
        startReader.touch(oysterCard);
        eventLog = eventLogger.getEventLog();
        return new CustomerJourneyEvents(customer, eventLog).createCustomerJourneyEvents();
    }

    @Test
    public void checkTouchingOysterCardAddsJourneyEventInEventLog()
    {
        startReader.touch(registeredCard);
        eventLog = eventLogger.getEventLog();
        assertTrue(eventLog.size() == 1 && eventLog.get(0).cardId().equals(registeredCard.id()));
        endReader.touch(registeredCard);assertTrue(eventLog.size() == 2 && eventLog.get(1).cardId().equals(registeredCard.id()));
    }
    @Test
    public void checkJourneysCorrectlyAddedAndRemovedInCurrentlyTravellingSet(){


        startReader.touch(registeredCard);    //starts journey, JourneyStart should be created and added to the eventLog, currentlyTravelling should contain cardID

        eventLog = eventLogger.getEventLog();
        JourneyEvent expectedStartOfJourney = new JourneyStart(registeredCard.id(), startReader.id());
        UUID cardIdAtStartOfJourneyInEventLog = eventLog.get(0).cardId();
        UUID readerIdAtStartOfJourneyInEventLog = eventLog.get(0).readerId();

        assertTrue(cardIdAtStartOfJourneyInEventLog.equals(expectedStartOfJourney.cardId()));
        assertTrue(readerIdAtStartOfJourneyInEventLog.equals(expectedStartOfJourney.readerId()));

        Set<UUID> currentlyTravelling = eventLogger.getCurrentlyTravelling();
        assertTrue(currentlyTravelling.contains(registeredCard.id()));

        endReader.touch(registeredCard);   //ends journey, JourneyEnd should be created and added to the eventLog, currentlyTravelling should NOT contain cardID.

        JourneyEvent expectedEndOfJourney = new JourneyEnd(registeredCard.id(), endReader.id());
        UUID cardIdAtEndOfJourneyInEventLog = eventLog.get(1).cardId();
        UUID readerIdAtEndOfJourneyInEventLog = eventLog.get(1).readerId();

        assertTrue(cardIdAtEndOfJourneyInEventLog.equals(expectedEndOfJourney.cardId()));
        assertTrue(readerIdAtEndOfJourneyInEventLog.equals(expectedEndOfJourney.readerId()));

        assertFalse(currentlyTravelling.contains(registeredCard.id()));
    }
    @Test
    public void checkCorrectCreationOfCustomerJourneyEventsList()
    {

        customerJourneyEventsList = createCustomerJourneyEventsListForTesting();


        assertTrue(eventLog.get(0).cardId().equals(customerJourneyEventsList.get(0).cardId()));
        assertTrue(eventLog.get(0).readerId().equals(customerJourneyEventsList.get(0).readerId()));

        assertTrue(eventLog.get(1).cardId().equals(customerJourneyEventsList.get(1).cardId()));
        assertTrue(eventLog.get(1).readerId().equals(customerJourneyEventsList.get(1).readerId()));

        assertTrue(eventLog.size() == 3);
        assertTrue(customerJourneyEventsList.size() == 2);
    }
    @Test
    public void checkCorrectCreationOfJourneyListFromCustomerJourneyEventsList()
    {
        customerJourneyEventsList = createCustomerJourneyEventsListForTesting();
        Journeys journeys = new Journeys(customerJourneyEventsList);
        List<Journey> customerJourneys = journeys.createListOfJourneysForCustomer();

        assertTrue(customerJourneyEventsList.get(0) instanceof JourneyStart);
        assertTrue(customerJourneyEventsList.get(1) instanceof JourneyEnd);

        assertTrue(customerJourneys.get(0).originId().equals(customerJourneyEventsList.get(0).readerId()));
        assertTrue(customerJourneys.get(0).destinationId().equals(customerJourneyEventsList.get(1).readerId()));

        assertTrue(customerJourneys.get(0).getStart().cardId().equals(customerJourneyEventsList.get(0).cardId()));
        assertTrue(customerJourneys.get(0).getEnd().cardId().equals(customerJourneyEventsList.get(1).cardId()));
        assertTrue(customerJourneys.size() == 1);
    }


    @Test
    public void checkChargeAccountsForNoTrips() {
        TravelTracker travelTracker = new TravelTracker(mockCustomerDatabase, mockPaymentsSystem, eventLogger);
        context.checking(new Expectations() {{

            Customer customer = new Customer("Naum Anteski", registeredCard);
            BigDecimal customerTotal = new BigDecimal(0);
            List<Customer> myCustomers = new ArrayList<>();
            List<Journey> journeys = new ArrayList<>();

            myCustomers.add(customer);
            exactly(1).of(mockCustomerDatabase).getCustomers();
            will(returnValue(myCustomers));

            customerTotal = customerTotal.setScale(2, BigDecimal.ROUND_HALF_UP);

            exactly(1).of(mockPaymentsSystem).charge(customer, journeys, customerTotal);
        }});

        travelTracker.chargeAccounts();
    }

    @Test
    public void checkUnknownOysterCardExceptionIsThrownForUnregisteredOysterCardID()
    {
        EventLog eventLogger = new EventLog();
        UUID randomOysterId = UUID.randomUUID();
        UUID randomReaderId = UUID.randomUUID();
        try{
            eventLogger.cardScanned(randomOysterId, randomReaderId);
        }
        catch (RuntimeException exception){
            assertThat(exception.getMessage(), is("Oyster Card does not correspond to a known customer. Id: " + randomOysterId));
        }


    }

}