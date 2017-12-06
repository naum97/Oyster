package Test;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.*;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TravelTrackerTest {

    private final JUnitRuleMockery context = new JUnitRuleMockery();
    private final List<JourneyEvent> eventLogForTesting = new ArrayList<>();
    private final Set<UUID> currentlyTravellingSetForTesting = new HashSet<>();

    private final Database mockCustomerDatabase = context.mock(Database.class);
    private final GeneralPaymentsSystem mockPaymentsSystem  = context.mock(GeneralPaymentsSystem.class);
    private final Fare costCalculator = context.mock(Fare.class);

    private final TravelTracker travelTracker = new TravelTracker(eventLogForTesting,
            currentlyTravellingSetForTesting, mockCustomerDatabase,mockPaymentsSystem,costCalculator);

    @Test
    public void checkScanningOysterCardCreatesProperJourneyEventInEventLog()
    {
        UUID firstCard = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID secondCard = UUID.fromString("609e72ac-8be3-4476-8b45-01db8c7f122b");
        UUID startReader = UUID.randomUUID();
        UUID endReader = UUID.randomUUID();

        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).isRegisteredId(firstCard); will(returnValue(true));
            oneOf(mockCustomerDatabase).isRegisteredId(secondCard);will(returnValue(true));
        }});

        travelTracker.cardScanned(firstCard, startReader);
        assertTrue(eventLogForTesting.size() == 1);
        assertTrue(eventLogForTesting.get(0) instanceof JourneyStart);
        assertTrue(eventLogForTesting.get(0).cardId().equals(firstCard));

        travelTracker.cardScanned(secondCard, startReader);
        assertTrue(eventLogForTesting.size() == 2);
        assertTrue(eventLogForTesting.get(1) instanceof JourneyStart);
        assertTrue(eventLogForTesting.get(1).cardId().equals(secondCard));

        travelTracker.cardScanned(firstCard, endReader);
        assertTrue(eventLogForTesting.size() == 3);
        assertTrue(eventLogForTesting.get(2) instanceof JourneyEnd);
        assertTrue(eventLogForTesting.get(2).cardId().equals(firstCard));

        travelTracker.cardScanned(secondCard, endReader);
        assertTrue(eventLogForTesting.size() == 4);
        assertTrue(eventLogForTesting.get(3) instanceof JourneyEnd);
        assertTrue(eventLogForTesting.get(3).cardId().equals(secondCard));
    }

    @Test
    public void checkOysterIDCorrectlyAddedAndRemovedFromCurrentlyTravellingSet()
    {
        UUID firstCard = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        UUID firstReader = UUID.randomUUID();

        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).isRegisteredId(firstCard);will(returnValue(true));
        }});
        travelTracker.cardScanned(firstCard, firstReader);
        assertTrue(currentlyTravellingSetForTesting.size() == 1);
        assertTrue(currentlyTravellingSetForTesting.contains(firstCard));
        travelTracker.cardScanned(firstCard, firstReader);
        assertTrue(currentlyTravellingSetForTesting.size() == 0);
        assertFalse(currentlyTravellingSetForTesting.contains(firstCard));
    }
    @Test
    public void checkExceptionThrownWithUnregisteredOysterId()
    {
        UUID firstCard = UUID.randomUUID();
        UUID firstReader= UUID.randomUUID();
        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).isRegisteredId(firstCard);will(returnValue(false));
        }});
        try {
            travelTracker.cardScanned(firstCard, firstReader);
        } catch (RuntimeException exception){
            assertThat(exception.getMessage(), is("Oyster Card does not correspond to a known customer. Id: " + firstCard));
        }
    }
    @Test
    public void checkChargeAccountsWithNoJourneys()
    {
        Customer customer = new Customer("Naum Anteski", new OysterCard(UUID.randomUUID().toString()));
        List<Customer> customers = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        List<Journey> journeys = new ArrayList<>();
        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).getCustomers(); will(returnValue(customers));
            oneOf(mockPaymentsSystem).charge(customer, journeys, totalCost);
        }});
        travelTracker.chargeAccounts();
    }
    @Test
    public void checkChargeAccountsWithOneCustomerAndOneJourney()
    {
        List<Customer> customers = new ArrayList<>();
        OysterCard registeredOysterCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        Customer customer = new Customer("Naum Anteski", registeredOysterCard);
        OysterCardReader startReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader endReader = OysterReaderLocator.atStation(Station.MARYLEBONE);

        DateTime dateTime = new DateTime().withHourOfDay(11).withMinuteOfHour(0);  //off-peak short journey
        JourneyEvent testStart = new JourneyStart(registeredOysterCard.id(),startReader.id(),dateTime);
        JourneyEvent testEnd = new JourneyEnd(registeredOysterCard.id(), endReader.id(), dateTime.plusMinutes(15));

        eventLogForTesting.add(testStart);
        eventLogForTesting.add(testEnd);

        BigDecimal totalCost = new BigDecimal(1.60).setScale(2,BigDecimal.ROUND_HALF_UP);
        customers.add(customer);

        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).getCustomers(); will(returnValue(customers));
            oneOf(costCalculator).customerTotalCost(with(aNonNull(List.class))); will(returnValue(totalCost));
            oneOf(mockPaymentsSystem).charge(with(equal(customer)), with(aNonNull(List.class)), with(equal(totalCost)));
        }});
        travelTracker.chargeAccounts();
    }
    @Test
    public void checkChargeAccountsWithTwoCustomers()
    {
        List<Customer> customers = new ArrayList<>();

        OysterCard firstCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        OysterCard secondCard = new OysterCard("609e72ac-8be3-4476-8b45-01db8c7f122b");
        Customer firstCustomer = new Customer("Naum Anteski",firstCard);
        Customer secondCustomer = new Customer("Moiz Hassam", secondCard);
        customers.add(firstCustomer);
        customers.add(secondCustomer);
        OysterCardReader startReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader endReader = OysterReaderLocator.atStation(Station.MARYLEBONE);

        DateTime peakLong = new DateTime().withHourOfDay(17).withMinuteOfHour(0); // peak long journey
        JourneyEvent firstStartOfJourney = new JourneyStart(firstCard.id(), startReader.id(), peakLong);
        JourneyEvent firstEndOfJourney = new JourneyStart(firstCard.id(), endReader.id(), peakLong.plusMinutes(35));
        //first journey is at index 0 and 1
        eventLogForTesting.add(firstStartOfJourney);
        eventLogForTesting.add(firstEndOfJourney);

        DateTime peakShort = new DateTime().withHourOfDay(17).withMinuteOfHour(0); // peak short journey
        JourneyEvent secondStartOfJourney = new JourneyStart(secondCard.id(), startReader.id(), peakShort);
        JourneyEvent secondEndOfJourney = new JourneyStart(secondCard.id(), endReader.id(), peakShort.plusMinutes(20));
        //second journey is at index 2 and 3
        eventLogForTesting.add(secondStartOfJourney);
        eventLogForTesting.add(secondEndOfJourney);

        BigDecimal firstJourneyCost = new BigDecimal(3.80).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal secondJourneyCost = new BigDecimal(2.90).setScale(2, BigDecimal.ROUND_HALF_UP);

        context.checking(new Expectations(){{
            oneOf(mockCustomerDatabase).getCustomers(); will(returnValue(customers));
            oneOf(costCalculator).customerTotalCost(with(aNonNull(List.class))); will(returnValue(firstJourneyCost));
            oneOf(mockPaymentsSystem).charge(with(equal(firstCustomer)), with(aNonNull(List.class)), with(equal(firstJourneyCost)));
            oneOf(costCalculator).customerTotalCost(with(aNonNull(List.class))); will(returnValue(secondJourneyCost));
            oneOf(mockPaymentsSystem).charge(with(equal(secondCustomer)), with(aNonNull(List.class)), with(equal(secondJourneyCost)));
        }});
        travelTracker.chargeAccounts();
    }
}