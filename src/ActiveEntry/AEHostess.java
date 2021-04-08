package ActiveEntry;

import DepartureAirport.IDepartureAirport_Hostess;

public class AEHostess extends Thread {
    
    // shared regions
    private final IDepartureAirport_Hostess iDepartureAirport;

    // configuration
    private int numberOfPassengers;
    private int numberOfAttendedPassengers;

    // Constructor
    public AEHostess(IDepartureAirport_Hostess iDepartureAirport_Hostess, int numberOfPassengers){
        iDepartureAirport = iDepartureAirport_Hostess;
        this.numberOfPassengers = numberOfPassengers;
        this.numberOfAttendedPassengers = 0;
    }   
    
    @Override
    public void run(){
        while(numberOfAttendedPassengers != numberOfPassengers){
            iDepartureAirport.waitForNextFlight();
            iDepartureAirport.waitForNextPassenger();
            iDepartureAirport.askForDocuments();
            iDepartureAirport.waitForNextPassenger();
            numberOfAttendedPassengers++;
            iDepartureAirport.informPlaneReadyToFly();
        }
        System.out.println("Hostess ended activity");
    }   
}
