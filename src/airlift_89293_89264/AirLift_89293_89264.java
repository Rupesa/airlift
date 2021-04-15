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
import GeneralRepository.GeneralRepos;
import Plane.IPlane_Passenger;
import Plane.IPlane_Pilot;
import Plane.SRPlane;
import genclass.GenericIO;
import genclass.FileOp;

/**
 *  Simulation of the AirLift problem.
 * 
 *  @author Rui Santos (89293)
 *  @author Rita Amante (89264)
 */
public class AirLift_89293_89264 {
    
    /**
    *   Main method.
    *
    *   @param args runtime arguments
    */
    public static void main(String args[]){
        /* references to the shared regions */
        GeneralRepos repos;
        SRDepartureAirport srDepartureAirport;
        SRDestinationAirport srDestinationAirport;
        SRPlane srPlane;
        
        /* references to the active entities */
        AEHostess aeHostess;
        AEPilot aePilot;
        AEPassenger[] aePassenger;
        
        /* file options  */
        String fileName;
        char opt;
        boolean success; 
        
        /* problem initialization */
        GenericIO.writelnString ("\n" + "      Problem of the Air Lift\n");
        do {
            GenericIO.writeString ("Logging file name? ");
            fileName = GenericIO.readlnString ();
            if (FileOp.exists (".", fileName)){
                do {
                    GenericIO.writeString ("There is already a file with this name. Delete it (y - yes; n - no)? ");
                    opt = GenericIO.readlnChar ();
                } while ((opt != 'y') && (opt != 'n'));
                if (opt == 'y')
                    success = true;
                else 
                    success = false;
           }
           else 
                success = true;
        } while (!success);

        /* instantiation shared region */
        repos = new GeneralRepos (fileName);
        srDepartureAirport = new SRDepartureAirport(SimulationParameters.MIN_PASSENGER, SimulationParameters.MAX_PASSENGER, SimulationParameters.TTL_PASSENGER, repos);
        srDestinationAirport = new SRDestinationAirport(repos);
        srPlane = new SRPlane(SimulationParameters.TTL_PASSENGER, repos);
        
        /* instantiation to the active entities */
        aeHostess = new AEHostess("Hostess", (IDepartureAirport_Hostess) srDepartureAirport, SimulationParameters.TTL_PASSENGER);
        aePilot = new AEPilot("Pilot", (IDepartureAirport_Pilot) srDepartureAirport, (IPlane_Pilot) srPlane);
        aePassenger = new AEPassenger[SimulationParameters.TTL_PASSENGER];
        for (int i=0; i < aePassenger.length; i++){
            aePassenger[i] = new AEPassenger("Passenger_" + i+1, i, (IDepartureAirport_Passenger) srDepartureAirport, (IDestinationAirport_Passenger) srDestinationAirport, (IPlane_Passenger) srPlane);
        }
        
        /* start of the simulation */
        aePilot.start();
        aeHostess.start();
        for (int i=0; i < aePassenger.length; i++){
            aePassenger[i].start();
        }

        /* waiting for the end of the simulation */
        GenericIO.writelnString ();
        try {
            aePilot.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GenericIO.writelnString ("The pilot has terminated.");
        try {
            aeHostess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GenericIO.writelnString ("The hostess has terminated.");
        for (int i=0; i < aePassenger.length; i++){
            try{
                aePassenger[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();                    
            }
            
            GenericIO.writelnString ("The passenger " + (i+1) + " has terminated.");
            if (i == 20){
                repos.reportFinalStatus();
            }
        }
        
        GenericIO.writelnString ();
    }
}
