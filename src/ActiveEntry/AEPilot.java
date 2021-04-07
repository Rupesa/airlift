package ActiveEntry;

import DepartureAirport.IDepartureAirport_Pilot;
import DestinationAirport.IDestinationAirport_Pilot;
import Plane.IPlane_Pilot;

public class AEPilot extends Thread {
    
    // shared regions
    private final IDepartureAirport_Pilot iDepartureAirport;
    private final IDestinationAirport_Pilot iDestinatonAirport;
    private final IPlane_Pilot iPlane;
    
    public AEPilot(IDepartureAirport_Pilot iDepartureAirport_Pilot, IDestinationAirport_Pilot iDestinationAirport_Pilot, IPlane_Pilot iPlane_Pilot){
        iDepartureAirport = iDepartureAirport_Pilot;
        iDestinatonAirport = iDestinationAirport_Pilot;
        iPlane = iPlane_Pilot;  
        
        // code
    } 
    
    public void run(){
        // code
    } 
}
