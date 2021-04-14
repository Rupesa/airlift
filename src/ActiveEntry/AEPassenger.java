package ActiveEntry;

import DepartureAirport.IDepartureAirport_Passenger;
import DestinationAirport.IDestinationAirport_Passenger;
import Plane.IPlane_Passenger;

/**
 *  Passenger thread.
 *
 *  It simulates the passenger life cycle.
 */
public class AEPassenger extends Thread {
    
    /**
    *   Reference to the shared regions.
    */
    private final IDepartureAirport_Passenger iDepartureAirport;
    private final IDestinationAirport_Passenger iDestinatonAirport;
    private final IPlane_Passenger iPlane;

    /**
    *   Passenger identification.
    */
    private int id;
    
    /**
    *   Passenger state.
    */
    private int passengerState;
    
    /**
    *   Instantiation of a passenger thread.
    *
    *   @param name thread name
    *   @param id passenger id
    *   @param iDepartureAirport_Passenger reference to the Departure Airport
    *   @param iDestinationAirport_Passenger reference to the Destination Airport 
    *   @param iPlane_Passenger reference to the Plane 
    */
    public AEPassenger(String name, int id, IDepartureAirport_Passenger iDepartureAirport_Passenger, IDestinationAirport_Passenger iDestinationAirport_Passenger, IPlane_Passenger iPlane_Passenger){
        super(name);
        iDepartureAirport = iDepartureAirport_Passenger;
        iDestinatonAirport = iDestinationAirport_Passenger;
        iPlane = iPlane_Passenger;
        this.id = id;
        passengerState = AEPassengerStates.GTAP;
    }   
        
    /**
    *   Get passenger id.
    *
    *   @return passenger id
    */
    public int getPassengerId(){
        return id;
    }
    
    /**
    *   Set passenger id.
    *
    *   @param id passenger id
    */
    public void setPassengerId(int id){
        this.id = id;
    }
    
    /**
    *   Get passenger state.
    *
    *   @return passenger state
    */
    public int getPassengerState(){
        return passengerState;
    }
    
    /**
    *   Set passenger state.
    *
    *   @param state passenger state
    */
    public void setPassengerState(int state){
        this.passengerState = state;
    }
    
    /**
    *   Life cycle of the passenger.
    */
    @Override
    public void run(){
        System.out.println("----- Started Passenger " + getPassengerId() + " activity -----");
        
        goingToAirport();
        iDepartureAirport.travelToAirport();
        iDepartureAirport.waitInQueue();
        iDepartureAirport.showDocuments();
        iPlane.boardThePlane();
        iPlane.waitForEndOfFlight();
        iPlane.leaveThePlane();
        iDestinatonAirport.leaveAirport();
        System.out.println("------ Ended Passenger " + getPassengerId() + " activity ------");
    }
    
    /**
    *  The passenger goes to the airport.
    *
    *  Internal operation.
    */
    private void goingToAirport (){
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
