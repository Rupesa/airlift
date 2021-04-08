package ActiveEntry;

import DepartureAirport.IDepartureAirport_Pilot;
import Plane.IPlane_Pilot;

public class AEPilot extends Thread {
    
    // shared regions
    private final IDepartureAirport_Pilot iDepartureAirport;
    private final IPlane_Pilot iPlane;
    
    public AEPilot(IDepartureAirport_Pilot iDepartureAirport_Pilot, IPlane_Pilot iPlane_Pilot){
        iDepartureAirport = iDepartureAirport_Pilot;
        iPlane = iPlane_Pilot;  
        
    } 
    
    @Override
    public void run(){
        while(true){
            // check if pilot ended activity
            if(true){
                break;
            }
            iDepartureAirport.informPlaneReadyForBoarding();
            iDepartureAirport.waitForAllInBoard();
            // fly to destination
            iPlane.announceArrival();
            iPlane.waitForDeboard();      
            // flyt to departure
        }
        System.out.println("Hostess ended activity");     
    } 
}
