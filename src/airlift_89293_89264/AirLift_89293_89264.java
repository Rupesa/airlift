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
    
    // active entities (threads)
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
        srDepartureAirport = new SRDepartureAirport(MIN_PASSENGER, MAX_PASSENGER);
        srDestinationAirport = new SRDestinationAirport();
        srPlane = new SRPlane();
        
        // active entities (threads) instantiation
        aePilot = new AEPilot((IDepartureAirport_Pilot) srDepartureAirport, (IPlane_Pilot) srPlane);
        aeHostess = new AEHostess((IDepartureAirport_Hostess) srDepartureAirport, TTL_PASSENGER);
        aePassenger = new AEPassenger[TTL_PASSENGER];
        for (int i=0; i < aePassenger.length; i++){
            aePassenger[i] = new AEPassenger(i+1, (IDepartureAirport_Passenger) srDepartureAirport, (IDestinationAirport_Passenger) srDestinationAirport, (IPlane_Passenger) srPlane);
        }
    }
    
    public static void sleepTime(int min, int max){
        int randomTime = (int) ((Math.random() * (max-min)) + min);
        
        try {
            Thread.sleep(randomTime * 1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    } 
    
    private void startSimulation(){
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("                                        Simulation started                                        ");
        System.out.println("--------------------------------------------------------------------------------------------------");

        // start active entities (threads)
        aePilot.start();
        aeHostess.start();
        for (int i=0; i < aePassenger.length; i++){
            aePassenger[i].start();
        }
        
        // wait active entities (threads) to die
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
        
        for (int i=0; i < aePassenger.length; i++){
            try{
                aePassenger[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();                    
            }
        }
        
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("                                        Simulation is over                                        ");
        System.out.println("--------------------------------------------------------------------------------------------------");
    }
    
    public static void main(String args[]){
        new AirLift_89293_89264(args).startSimulation();
    }
}
