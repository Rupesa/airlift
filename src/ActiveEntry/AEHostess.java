package ActiveEntry;

import DepartureAirport.IDepartureAirport_Hostess;
import DestinationAirport.IDestinationAirport_Hostess;
import Plane.IPlane_Hostess;

public class AEHostess extends Thread {
    // shared regions
    private final IDepartureAirport_Hostess iDepartureAirport;
    private final IDestinationAirport_Hostess iDestinatonAirport;
    private final IPlane_Hostess iPlane;

    public AEHostess(IDepartureAirport_Hostess iDepartureAirport_Hostess, IDestinationAirport_Hostess iDestinationAirport_Hostess, IPlane_Hostess iPlane_Hostess){
        iDepartureAirport = iDepartureAirport_Hostess;
        iDestinatonAirport = iDestinationAirport_Hostess;
        iPlane = iPlane_Hostess;  
        
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
