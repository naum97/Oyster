package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by User on 11/15/2017.
 */
public class Example {

    private static JourneyStart startOfJourney;
    private static JourneyEnd endOfJourney;
    private static UUID cardID;
    private static UUID originReaderID;
    private static UUID destinationReaderID;

    public static void main(String[] args) throws Exception {
      /*  OysterCard myCard = new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d");
        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);
        TravelTracker travelTracker = new TravelTracker();
        travelTracker.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);
        paddingtonReader.touch(myCard);
        minutesPass(5);
        bakerStreetReader.touch(myCard);
        minutesPass(15);
        bakerStreetReader.touch(myCard);
        minutesPass(10);
        kingsCrossReader.touch(myCard);
        travelTracker.chargeAccounts();*/
            Calendar calendar = Calendar.getInstance();
            DateTime dt = new DateTime().withHourOfDay(18);
            calendar.setTime(dt.toDate());
            startOfJourney = new JourneyStart(cardID, originReaderID);
            System.out.println(startOfJourney.time());
            DateTimeUtils.setCurrentMillisOffset(5 * 1000);
            endOfJourney = new JourneyEnd(cardID, destinationReaderID);
            System.out.println(endOfJourney.time());
            Journey journey = new Journey(startOfJourney,endOfJourney);

            System.out.println(journey.startTime());

            DateTimeUtils.setCurrentMillisSystem();
            System.out.println(calendar.getTime());
            System.out.println(calendar.get(Calendar.HOUR_OF_DAY));

        }



    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n * 60);
    }
}

