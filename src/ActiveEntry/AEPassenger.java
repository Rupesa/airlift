package ActiveEntry;

import DepartureAirport.IDepartureAirport_Passenger;
import DestinationAirport.IDestinationAirport_Passenger;
import Plane.IPlane_Passenger;

public class AEPassenger extends Thread {
    // shared regions
    private final IDepartureAirport_Passenger iDepartureAirport;
    private final IDestinationAirport_Passenger iDestinatonAirport;
    private final IPlane_Passenger iPlane;

    public AEPassenger(IDepartureAirport_Passenger iDepartureAirport_Passenger, IDestinationAirport_Passenger iDestinationAirport_Passenger, IPlane_Passenger iPlane_Passenger){
        iDepartureAirport = iDepartureAirport_Passenger;
        iDestinatonAirport = iDestinationAirport_Passenger;
        iPlane = iPlane_Passenger;  
        
        // code
    }   
    
    public void run(){
        Boolean notEnd = true;
        while(notEnd){
            // code
//            iPlane.method();
        }
    }   
}
