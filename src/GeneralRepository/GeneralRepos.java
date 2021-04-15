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
   
    
    private int numOfFlight;
    private int numOfPassengersOnFlight;
    private int checkedPassenger;
    private String [] voos;
    
    /**
    *   State of the flight.
    */
    private int flightState;
    
    /**
    *   State of the pilot.
    */
    private int pilotState;
    private int previousPilotState;
    
    /**
    *   State of the hostess.
    */
    private int hostessState;
    private int previousHostessState;
    
    /**
    *   State of the passengers.
    */
    private final int [] passengerState;
    private int  [] previousPassengerState;
        
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
        previousPilotState = pilotState;
        hostessState = AEHostessStates.WTFL;
        previousHostessState = hostessState;
        passengerState = new int [SimulationParameters.TTL_PASSENGER];
        for (int i = 0; i < passengerState.length; i++){
            passengerState[i] = AEPassengerStates.GTAP;
//            previousPassengerState[i] = passengerState[i];
        }
        reportInitialStatus ();
        this.inQ = 0;
        this.inF = 0;
        this.pTAL = 0;
        this.flightState = 0;
        voos = new String[5];
    }
 
    /**
    *   Set flight state.
    *
    *   @param state flight state
    */
    public synchronized void setFlightState (int state){
        flightState = state;
        reportStatus ();
    }
    
    public synchronized void setInfoBoardPlane(int nFlight, int nPassengers){
        this.numOfFlight = nFlight;
        this.numOfPassengersOnFlight = nPassengers;
        voos[numOfFlight-1] = numOfFlight + "-" + numOfPassengersOnFlight; 
    }
    
    public synchronized void setInfoCheckPassenger(int nFlight, int nPassengers, int nPass){
        this.numOfFlight = nFlight;
        this.numOfPassengersOnFlight = nPassengers;
        this.checkedPassenger = nPass;
    }
    
    /**
    *   Set pilot state.
    *
    *   @param state pilot state
    */
    public synchronized void setPilotState (int state){
        pilotState = state;
        reportStatus ();
    }
    
    /**
    *   Set hostess state.
    *
    *   @param state hostess state
    */
    public synchronized void setHostessState (int state){
        hostessState = state;
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
    *   Set pilot, hosstess and passenger state.
    *   
    *   @param pilotState pilot state
    *   @param hostessState hostess state
    *   @param passengerId passenger id
    *   @param passengerState passenger state
    */
    public synchronized void setPilotHostessPassengerState (int pilotState, int hostessState, int passengerId, int passengerState){
        this.pilotState = pilotState;
        this.hostessState = hostessState;
        this.passengerState[passengerId] = passengerState;
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
        log.writelnString ("\t\t\t\t\t\tAirlift - Description of the internal state\n");
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
        
        if (pilotState == AEPilotStates.RDFB && previousPilotState != pilotState){
            log.writelnString ("\n Flight " + numOfFlight + ": boarding started.");
            previousPilotState = pilotState;
        }
        
        if (hostessState == AEHostessStates.RDTF && previousHostessState != hostessState){
            log.writelnString ("\n Flight " + numOfFlight + ": departed with " + numOfPassengersOnFlight + " passengers."); 
            previousHostessState = hostessState;
        }
        
        if (pilotState == AEPilotStates.DRPP && previousPilotState != pilotState){
            log.writelnString ("\n Flight " + numOfFlight + ": arrived."); 
            previousPilotState = pilotState;
        }
        
        if (pilotState == AEPilotStates.FLBK && previousPilotState != pilotState){
            log.writelnString ("\n Flight " + numOfFlight + ": returning."); 
            previousPilotState = pilotState;
        }
        
        switch (pilotState){
                case AEPilotStates.ATGR:    lineStatus += " ATGR ";
                                            break;
                case AEPilotStates.RDFB:    lineStatus += " RDFB ";
                                            break;
                case AEPilotStates.WTFB:    lineStatus += " WTFB ";
                                            break;
                case AEPilotStates.FLFW:    lineStatus += " FLFW ";
                                            break;
                case AEPilotStates.DRPP:    lineStatus += " FLFW ";
                                            break;
                case AEPilotStates.FLBK:    lineStatus += " FLBK ";
                                            break;
            }

            switch (hostessState){
                case AEHostessStates.WTFL:  lineStatus += " WTFL ";
                                            break;
                case AEHostessStates.WTPS:  lineStatus += " WTPS ";
                                            break;
                case AEHostessStates.CKPS:  lineStatus += " CKPS ";
                                            break;
                case AEHostessStates.RDTF:  lineStatus += " RDTF ";
                                            break;
            }

            for (int i = 0; i < SimulationParameters.TTL_PASSENGER; i++){
                switch (passengerState[i]){
                    case AEPassengerStates.GTAP:    lineStatus += " GTAP ";
                                                    break;
                    case AEPassengerStates.INQE:    lineStatus += " INQE ";
//                                                    inQ++;
                                                    break;
                    case AEPassengerStates.INFL:    lineStatus += " INFL ";
//                                                    inQ--;
//                                                    inF++;
                                                    break;
                    case AEPassengerStates.ATDS:    lineStatus += " ATDS ";
                                                    break;
                }
            }

            lineStatus += "   " + inQ + "     " + inF + "     " + pTAL;
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
        log.writelnString ("\nAirLift sum up:");
        for (int i=0; i < numOfFlight; i++){
            if (i == numOfFlight-1){
                log.writelnString ("Flight " + voos[i].split("-")[0] + " transported " + voos[i].split("-")[1] + " passengers." );
            } else {
                log.writelnString ("Flight " + voos[i].split("-")[0] + " transported " + voos[i].split("-")[1] + " passengers" );
            }
        }
        
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
    }
    
}
