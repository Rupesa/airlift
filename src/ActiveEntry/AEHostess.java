package ActiveEntry;

import DepartureAirport.IDepartureAirport_Hostess;

/**
 *  Hostess thread.
 *
 *  It simulates the hostess life cycle.
 */
public class AEHostess extends Thread {
    
    /**
    *   Reference to the shared regions.
    */
    private final IDepartureAirport_Hostess iDepartureAirport;

    /**
    *   Maximum number of passengers.
    */
    private int maxNumberOfPassengers;
    
    /**
    *   Nnumber of attended passengers.
    */
    private int numberOfAttendedPassengers;
    
    /**
    *   Hostess state.
    */
    private int hostessState;

    /**
    *   Instantiation of a hostess thread.
    *   
    *   @param name thread name
    *   @param max maximum number of passengers
    *   @param iDepartureAirport_Passenger reference to the Departure Airport
    */
    public AEHostess(String name, IDepartureAirport_Hostess iDepartureAirport_Hostess, int max){
        super(name);
        iDepartureAirport = iDepartureAirport_Hostess;
        this.maxNumberOfPassengers = max;
        this.numberOfAttendedPassengers = 0;
        this.hostessState = AEHostessStates.WTFL;
    }   
    
    /**
    *   Get hostess state.
    *
    *   @return hostess state
    */
    public int getHostessState(){
        return hostessState;
    }
    
    /**
    *   Set hostess state.
    *
    *   @param state hostess state
    */
    public void setHostessState(int state){
        this.hostessState = state;
    }
    
    /**
    *   Life cycle of the hostess.
    */
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
