package com.tfl.billing;
import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
public class Main {
    public static void main(String[] args) throws Exception {
        OysterCard myCard = new OysterCard(CustomerDatabaseAdapter.getInstance().getCustomers().get(0).cardId().toString());
        OysterCardReader paddingtonReader = OysterReaderLocator.atStation(Station.PADDINGTON);
        OysterCardReader bakerStreetReader = OysterReaderLocator.atStation(Station.BAKER_STREET);
        OysterCardReader kingsCrossReader = OysterReaderLocator.atStation(Station.KINGS_CROSS);
        EventLog eventLogger = new EventLog();
        TravelTracker travelTracker = new TravelTracker(eventLogger);
        eventLogger.connect(paddingtonReader, bakerStreetReader, kingsCrossReader);


        paddingtonReader.touch(myCard);
        minutesPass(5);
        bakerStreetReader.touch(myCard);
        minutesPass(15);
        bakerStreetReader.touch(myCard);
        minutesPass(10);
        kingsCrossReader.touch(myCard);
        travelTracker.chargeAccounts();
    }
    private static void minutesPass(int n) throws InterruptedException {
        Thread.sleep(n*60*1000 );
    }
}