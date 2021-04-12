package ActiveEntry;

import DepartureAirport.IDepartureAirport_Hostess;

public class AEHostess extends Thread {
    
    // shared regions
    private final IDepartureAirport_Hostess iDepartureAirport;

    // configuration
    private int maxNumberOfPassengers;
    private int numberOfAttendedPassengers;

    // constructor
    public AEHostess(IDepartureAirport_Hostess iDepartureAirport_Hostess, int max){
        iDepartureAirport = iDepartureAirport_Hostess;
        this.maxNumberOfPassengers = max;
        this.numberOfAttendedPassengers = 0;
    }   
    
    @Override
    public void run(){
        System.out.println("------- Started Hostess activity -------");
        while(!checkIfAllPassengersAreAttended()){
            iDepartureAirport.waitForNextFlight();
            iDepartureAirport.waitForNextPassenger();
            iDepartureAirport.askForDocuments();
            iDepartureAirport.waitToCheckPassenger();
            numberOfAttendedPassengers++;
            iDepartureAirport.informPlaneReadyToFly();
        }
        System.out.println("-------- Ended Hostess activity --------");
    }
    
    public boolean checkIfAllPassengersAreAttended(){
        if (numberOfAttendedPassengers != maxNumberOfPassengers){
            return false;
        } else {
            return true;
        }
    }
}
