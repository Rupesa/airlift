package ActiveEntry;

import DepartureAirport.IDepartureAirport_Pilot;
import DepartureAirport.SRDepartureAirport;
import Plane.IPlane_Pilot;
import genclass.GenericIO;

/**
 *  Pilot thread.
 *
 *  It simulates the pilot life cycle.
 */
public class AEPilot extends Thread {
    
    /**
    *   Reference to the shared regions.
    */
    private final IDepartureAirport_Pilot iDepartureAirport;
    private final IPlane_Pilot iPlane;
    
    /**
    *   Pilot state.
    */
    private int pilotState;
    
    /**
    *   Instantiation of a pilot thread.
    *   
    *   @param name thread name
    *   @param iDepartureAirport_Pilot reference to the Departure Airport
    *   @param iPlane_Pilot reference to the Plane 
    */
    public AEPilot(String name, IDepartureAirport_Pilot iDepartureAirport_Pilot, IPlane_Pilot iPlane_Pilot){
        super(name);
        iDepartureAirport = iDepartureAirport_Pilot;
        iPlane = iPlane_Pilot;
        this.pilotState = AEPilotStates.ATGR;
    } 
    
    /**
    *   Get pilot state.
    *
    *   @return pilot state
    */
    public int getPilotState(){
        return pilotState;
    }
    
    /**
    *   Set pilot state.
    *
    *   @param state pilot state
    */
    public void setPilotState(int state){
        this.pilotState = state;
    }
    
    /**
    *   Life cycle of the pilot.
    */
    @Override
    public void run(){
        GenericIO.writelnString("Started Pilot activity");
        while(!iDepartureAirport.informPilotToEndActivity()){
            iDepartureAirport.informPlaneReadyForBoarding();
            iDepartureAirport.waitForAllInBoard();      
            iPlane.flyToDestinationPoint();
            iPlane.announceArrival();
            iPlane.flyToDeparturePoint();
            iPlane.parkAtTransferGate();
        }
        GenericIO.writelnString("Ended Pilot activity");
    }
}
