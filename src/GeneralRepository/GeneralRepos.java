package GeneralRepository;

import ActiveEntry.AEHostessStates;
import ActiveEntry.AEPassengerStates;
import ActiveEntry.AEPilotStates;
import airlift_89293_89264.SimulationParameters;
import genclass.GenericIO;
import genclass.TextFile;
import java.util.Objects;

/**
 *  General Repository.
 *
 *  It is responsible to keep the visible internal state of the problem and to provide means for it to be printed in the logging file.
 *  It is implemented as an implicit monitor.
 *  All public methods are executed in mutual exclusion.
 *  There are no internal synchronization points.
 */
public class GeneralRepos {
    
    /**
    *   Name of the logging file.
    */
    private final String logFileName;
   
    /**
    *   Information of flight.
    */
    private int numOfFlight;
    private int flight;
    private int currentPassenger;
    private String [] voos;
        
    /**
    *   State of the pilot.
    */
    private int pilotState;
    private int lastPilotReportedState;
    
    /**
    *   State of the hostess.
    */
    private int hostessState;
    private int lastHostessReportedState;
    
    /**
    *   State of the passengers.
    */
    private final int [] passengerState;
    private int [] lastPassengerReportedState;
        
    /**
    *   Number of passengers presently forming a queue to board the plane.
    */
    private int inQ;
    
    /**
    *   Number of passengers in the plane.
    */
    private int inF;

    /**
    *   Number of passengers that have already arrived at their destination.
    */
    private int pTAL;    
    
    /**
    *   Instantiation of a general repository object.
    *
    *   @param logFileName name of the logging file
    */
    public GeneralRepos (String logFileName){
        if ((logFileName == null) || Objects.equals (logFileName, "")){
            this.logFileName = "logger";
        }
        else{
            this.logFileName = logFileName;
        } 
        pilotState = AEPilotStates.ATGR;
        lastPilotReportedState = 0;
        currentPassenger = 0;
        hostessState = AEHostessStates.WTFL;
        lastHostessReportedState = 0;
        passengerState = new int [SimulationParameters.TTL_PASSENGER];
        lastPassengerReportedState = new int [SimulationParameters.TTL_PASSENGER];
        for (int i = 0; i < passengerState.length; i++){
            passengerState[i] = AEPassengerStates.GTAP;
            lastPassengerReportedState[i] = 0;
        }
        reportInitialStatus ();
        this.inQ = 0;
        this.inF = 0;
        this.pTAL = 0;
        voos = new String[6];
        this.flight = 1;
    }
    
    /**
    *   Set flight state.
    *
    *   @param nFlight number of flight
    *   @param nPassengers number of passengers on the flight
    */
    public synchronized void setInfoBoardPlane(int nFlight, int nPassengers){
        this.numOfFlight = nFlight;
        voos[numOfFlight-1] = numOfFlight + "-" + nPassengers; 
    }
        
    /**
    *   Set pilot state.
    *
    *   @param state pilot state
    */
    public synchronized void setPilotState (int state){
//        previousPilotState = pilotState;
        pilotState = state;
        reportStatus ();
    }
    
    /**
    *   Set hostess state.
    *   
    *   @param id id passenger
    *   @param state hostess state
    */
    public synchronized void setHostessState (int id,int state){
        hostessState = state;
        currentPassenger = id;
        reportStatus ();
    }
    
    /**
    *   Set passenger state.
    *
    *   @param id passenger id
    *   @param state passenger state
    */
    public synchronized void setPassengerState (int id, int state){    
        passengerState[id] = state;
        reportStatus ();
    }
        
    /**
    *   Write the header to the logging file.
    *
    *   Internal operation.
    */
    private void reportInitialStatus (){
        TextFile log = new TextFile ();
        
        if (!log.openForWriting (".", logFileName)){ 
            GenericIO.writelnString ("The operation of creating the file " + logFileName + " failed!");
            System.exit (1);
        }
        
        /* initial status */
        log.writelnString ("\t\t\t\t\t\t   Airlift - Description of the internal state\n");
        log.writelnString ("  PT    HT    P00   P01   P02   P03   P04   P05   P06   P07   P08   P09   P10   P11   P12   P13   P14   P15   P16   P17   P18   P19   P20  InQ   InF  PTAL");
                    
        if (!log.close ()){
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        
        reportStatus();
    }
    
    /**
    *   Write a state line at the end of the logging file.
    *
    *   Internal operation.
    */
    private void reportStatus (){
        TextFile log = new TextFile ();

        String lineStatus = "";
        
        if (!log.openForAppending (".", logFileName)){
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }
                 
        /* check pilot state */
        if (pilotState == AEPilotStates.ATGR){
            lineStatus += " ATGR ";
        } else if (pilotState == AEPilotStates.RDFB){
            /* check and print flight status : BOARDING STARTED */
            lineStatus += " RDFB ";
            log.writelnString ("\n Flight " + flight + ": boarding started.");
            lastPilotReportedState = AEPilotStates.RDFB;
        } else if (pilotState == AEPilotStates.WTFB){
            lineStatus += " WTFB ";
            lastPilotReportedState = AEPilotStates.WTFB;
        } else if (pilotState == AEPilotStates.FLFW){
            lineStatus += " FLFW ";
            lastPilotReportedState = AEPilotStates.FLFW;
        } else if (pilotState == AEPilotStates.DRPP){
            /* check and print flight status : ARRIVED */
            if(lastPilotReportedState == AEPilotStates.FLFW){
                log.writelnString ("\n Flight " + flight + ": arrived.");
            }
            lineStatus += " DRPP ";
            lastPilotReportedState = AEPilotStates.DRPP;
        } else if (pilotState == AEPilotStates.FLBK){
            /* check and print flight status : RETURNING */
            if(lastPilotReportedState == AEPilotStates.DRPP){
                log.writelnString ("\n Flight " + flight + ": returning."); 
                flight++;
            }      
            lineStatus += " FLBK ";
            lastPilotReportedState = AEPilotStates.FLBK;
        }
        
        /* check hostess state */
        if (hostessState == AEHostessStates.WTFL){
            lineStatus += " WTFL ";
            lastHostessReportedState = AEHostessStates.WTFL;
        } else if (hostessState == AEHostessStates.WTPS){
            lineStatus += " WTPS ";
            lastHostessReportedState = AEHostessStates.WTPS;
        } else if (hostessState == AEHostessStates.CKPS){
            /* check and print flight status : PASSENGER _ CHECKED */
            if (lastHostessReportedState == AEHostessStates.WTPS){
                log.writelnString ("\n Flight " + flight + ": passenger " + currentPassenger + " checked.");
                inQ--;
            }
            lineStatus += " CKPS ";
            lastHostessReportedState = AEHostessStates.CKPS;
        } else if (hostessState == AEHostessStates.RDTF){
            /* check and print flight status : DEPARTED WITH _ PASSENGERS */
            if (lastHostessReportedState == AEHostessStates.WTPS){
                log.writelnString ("\n Flight " + flight + ": departed with " + inF + " passengers.");
            }
            lineStatus += " RDTF ";
            lastHostessReportedState = AEHostessStates.RDTF;
        }
        
        /* check passenger state */
        for (int i = 0; i < SimulationParameters.TTL_PASSENGER; i++){
            if (passengerState[i] == AEPassengerStates.GTAP){
                lineStatus += " GTAP ";
                lastPassengerReportedState[i] = AEPassengerStates.GTAP;
            } else if (passengerState[i] == AEPassengerStates.INQE){
                if (lastPassengerReportedState[i] == AEPassengerStates.GTAP){
                    inQ++;
                } 
                lineStatus += " INQE ";
                lastPassengerReportedState[i] = AEPassengerStates.INQE;
            } else if (passengerState[i] == AEPassengerStates.INFL){
                if (lastPassengerReportedState[i] == AEPassengerStates.INQE){
                    inF++;
                } 
                lineStatus += " INFL ";
                lastPassengerReportedState[i] = AEPassengerStates.INFL;
            } else if (passengerState[i] == AEPassengerStates.ATDS){
                if (lastPassengerReportedState[i] == AEPassengerStates.INFL){
                    inF--;
                    pTAL++;
                }
                lineStatus += " ATDS ";
                lastPassengerReportedState[i] = AEPassengerStates.ATDS;
            }
        }
                
        lineStatus += String.format(" %3d   %3d  %3d", inQ, inF, pTAL);
        log.writelnString (lineStatus);
          
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
    
   /**
    *   Write the footer to the logging file.
    *
    *   Internal operation.
    */
    public void reportFinalStatus (){
        TextFile log = new TextFile ();
        
        if (!log.openForAppending (".", logFileName)){
            GenericIO.writelnString ("The operation of opening for appending the file " + logFileName + " failed!");
            System.exit (1);
        }
                
        /* final status */
        log.writelnString ("\n AirLift sum up:");
        for (int i=0; i < numOfFlight; i++){
            if (i == numOfFlight-1){
                log.writelnString (" Flight " + voos[i].split("-")[0] + " transported " + voos[i].split("-")[1] + " passengers." );
            } else {
                log.writelnString (" Flight " + voos[i].split("-")[0] + " transported " + voos[i].split("-")[1] + " passengers" );
            }
        }
        
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
    
}
