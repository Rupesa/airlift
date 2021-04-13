package ActiveEntry;

import DepartureAirport.IDepartureAirport_Pilot;
import DepartureAirport.SRDepartureAirport;
import Plane.IPlane_Pilot;

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
    *   @param iDepartureAirport_Passenger reference to the Departure Airport
    *   @param iPlane_Passenger reference to the Plane 
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
        System.out.println("-------- Started Pilot activity --------");
        while(true){
            if(SRDepartureAirport.informPilotToEndActivity()){
                break;
            }
            // estado inicial  : ATRG
            iDepartureAirport.informPlaneReadyForBoarding();
            // mudar de estado : ATRG -> RDFB
            iDepartureAirport.waitForAllInBoard();
            // mudar de estado : RDFB -> WTFB          
            flyToDestinationPoint();
            // mudar de estado : WTFB -> FLFW           
            iPlane.announceArrival();
            // mudar de estado : FLFW -> DRPP
            iPlane.waitForDeboard();   // nao esta no diagrama   
            flyToDeparturePoint();
            // mudar de estado : DRPP -> FLBK
            // falta o parkAtTransferGate
            // mudar de estado : FLBK -> ATRG
        }
        System.out.println("--------- Ended Pilot activity ---------"); 
    }
    
    /**
    *  The pilot flies to destination airport.
    *
    *  Internal operation.
    */
    private void flyToDestinationPoint (){
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
    *  The pilot flies to departure airport.
    *
    *  Internal operation.
    */
    private void flyToDeparturePoint (){
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
