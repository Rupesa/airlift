package ActiveEntry;

import DepartureAirport.IDepartureAirport_Passenger;
import DestinationAirport.IDestinationAirport_Passenger;
import Plane.IPlane_Passenger;

public class AEPassenger extends Thread {
    
    // shared regions
    private final IDepartureAirport_Passenger iDepartureAirport;
    private final IDestinationAirport_Passenger iDestinatonAirport;
    private final IPlane_Passenger iPlane;

    // configurations
    private int id;
    
    // constructor
    public AEPassenger(int id, IDepartureAirport_Passenger iDepartureAirport_Passenger, IDestinationAirport_Passenger iDestinationAirport_Passenger, IPlane_Passenger iPlane_Passenger){
        iDepartureAirport = iDepartureAirport_Passenger;
        iDestinatonAirport = iDestinationAirport_Passenger;
        iPlane = iPlane_Passenger;  
        this.id = id;
    }   
    
    @Override
    public void run(){
        System.out.println("----- Started Passenger " + getPassengerId() + " activity -----");
        airlift_89293_89264.AirLift_89293_89264.sleepTime(5,20);   // time to go to the airport
        iDepartureAirport.travelToAirport();
        iDepartureAirport.waitInQueue();
        iDepartureAirport.showDocuments();
        iDepartureAirport.waitToBeCheckedDocuments();
        iPlane.boardThePlane();
        iPlane.waitForEndOfFlight();
        iPlane.leaveThePlane();
        iDestinatonAirport.leaveAirport();
        System.out.println("------ Ended Passenger " + getPassengerId() + " activity ------");
    }   
        
    public int getPassengerId(){
        return id;
    }
    
    public void setPassengerId(int id){
        this.id = id;
    }
}
