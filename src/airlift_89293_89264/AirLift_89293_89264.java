package airlift_89293_89264;

import ActiveEntry.AEHostess;
import ActiveEntry.AEPassenger;
import ActiveEntry.AEPilot;
import DepartureAirport.IDepartureAirport_Hostess;
import DepartureAirport.IDepartureAirport_Passenger;
import DepartureAirport.IDepartureAirport_Pilot;
import DepartureAirport.SRDepartureAirport;
import DestinationAirport.IDestinationAirport_Passenger;
import DestinationAirport.SRDestinationAirport;
import Plane.IPlane_Passenger;
import Plane.IPlane_Pilot;
import Plane.SRPlane;

public class AirLift_89293_89264 {
    
    // shared regions
    private final SRDepartureAirport srDepartureAirport;
    private final SRDestinationAirport srDestinationAirport;
    private final SRPlane srPlane;
    
    // active entities
    private final AEHostess aeHostess;
    private final AEPassenger[] aePassenger;
    private final AEPilot aePilot;
    
    // configuration
    private final Integer TTL_PASSENGER;
    private final Integer MAX_PASSENGER;
    private final Integer MIN_PASSENGER;

    public AirLift_89293_89264 (String args[]){
        TTL_PASSENGER = 21;
        MAX_PASSENGER = 10;
        MIN_PASSENGER = 5;
        
        // shared regions instantiation
        srDepartureAirport = new SRDepartureAirport();
//        srDepartureAirport = new SRDepartureAirport(MIN_PASSENGER, MAX_PASSENGER);
        srDestinationAirport = new SRDestinationAirport();
        srPlane = new SRPlane();
        
        // active entities instantiation: Threads
        aePilot = new AEPilot((IDepartureAirport_Pilot) srDepartureAirport, (IPlane_Pilot) srPlane);
        aeHostess = new AEHostess((IDepartureAirport_Hostess) srDepartureAirport, TTL_PASSENGER);
        aePassenger = new AEPassenger[MAX_PASSENGER];
        for (Integer i=0; i < TTL_PASSENGER; i++){
            aePassenger[i] = new AEPassenger(i+1, (IDepartureAirport_Passenger) srDepartureAirport, (IDestinationAirport_Passenger) srDestinationAirport, (IPlane_Passenger) srPlane);
        }

    }
    
    private void startSimulation(){
        System.out.println("Simulation started");
        
        // start active entities : Threads
        aePilot.start();
        aeHostess.start();
        for (Integer i=0; i < MAX_PASSENGER; i++){
            aePassenger[i].start();
        }
        
        // wait active entities to die
        try {
            aePilot.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            aeHostess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (Integer i=0; i < MAX_PASSENGER; i++){
            try{
                aePassenger[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();                    
            }
        }
        System.out.println("End of Simulation");
    }
    
    public static void main(String args[]){
        new AirLift_89293_89264(args).startSimulation();

    }
}
