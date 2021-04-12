package ActiveEntry;

import DepartureAirport.IDepartureAirport_Pilot;
import DepartureAirport.SRDepartureAirport;
import Plane.IPlane_Pilot;

public class AEPilot extends Thread {
    
    // shared regions
    private final IDepartureAirport_Pilot iDepartureAirport;
    private final IPlane_Pilot iPlane;
    
    // constructor
    public AEPilot(IDepartureAirport_Pilot iDepartureAirport_Pilot, IPlane_Pilot iPlane_Pilot){
        iDepartureAirport = iDepartureAirport_Pilot;
        iPlane = iPlane_Pilot;
    } 
    
    @Override
    public void run(){
        System.out.println("-------- Started Pilot activity --------");
        while(true){
            if(SRDepartureAirport.informPilotToEndActivity()){
                break;
            }
            iDepartureAirport.informPlaneReadyForBoarding();
            iDepartureAirport.waitForAllInBoard();
            airlift_89293_89264.AirLift_89293_89264.sleepTime(5,20);   // flight time
            iPlane.announceArrival();
            iPlane.waitForDeboard();      
            airlift_89293_89264.AirLift_89293_89264.sleepTime(5,20);   // flight time
        }
        System.out.println("--------- Ended Pilot activity ---------"); 
    }
    

}
