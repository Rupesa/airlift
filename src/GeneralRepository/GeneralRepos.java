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
    private int numOfPassengersOnFlight;
    private int numPass;
    private String [] voos;
        
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
    private int [] previousPassengerState;
    private int idPass;
        
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
        previousPassengerState = new int [SimulationParameters.TTL_PASSENGER];
        for (int i = 0; i < passengerState.length; i++){
            passengerState[i] = AEPassengerStates.GTAP;
            previousPassengerState[i] = passengerState[i];
        }
        reportInitialStatus ();
        this.inQ = 0;
        this.inF = 0;
        this.pTAL = 0;
        voos = new String[6];
        this.idPass = 0;
        this.flight = 1;
        this.numPass = 0;
    }
    
    /**
    *   Set flight state.
    *
    *   @param nFlight number of flight
    *   @param nPassengers number of passengers on the flight
    */
    public synchronized void setInfoBoardPlane(int nFlight, int nPassengers){
        this.numOfFlight = nFlight;
        this.numOfPassengersOnFlight = nPassengers;
        voos[numOfFlight-1] = numOfFlight + "-" + numOfPassengersOnFlight; 
    }
        
    /**
    *   Set pilot state.
    *
    *   @param state pilot state
    */
    public synchronized void setPilotState (int state){
        previousPilotState = pilotState;
        pilotState = state;
        reportStatus ();
    }
    
    /**
    *   Set hostess state.
    *
    *   @param state hostess state
    */
    public synchronized void setHostessState (int state){
        previousHostessState = hostessState;
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
        previousPassengerState[id] = passengerState[id];
        passengerState[id] = state;
        idPass = id;
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
         
        /* print status of entities */
//        switch (pilotState){
//            case AEPilotStates.ATGR:    lineStatus += " ATGR ";
//                                        break;
//            case AEPilotStates.RDFB:    lineStatus += " RDFB ";
//                                        break;
//            case AEPilotStates.WTFB:    lineStatus += " WTFB ";
//                                        break;
//            case AEPilotStates.FLFW:    lineStatus += " FLFW ";
//                                        break;
//            case AEPilotStates.DRPP:    lineStatus += " DRPP ";
//                                        break;
//            case AEPilotStates.FLBK:    lineStatus += " FLBK ";
//                                        break;
//        }
//        switch (hostessState){
//            case AEHostessStates.WTFL:  lineStatus += " WTFL ";
//                                        break;
//            case AEHostessStates.WTPS:  lineStatus += " WTPS ";
//                                        break;
//            case AEHostessStates.CKPS:  lineStatus += " CKPS ";    
//                                        break;
//            case AEHostessStates.RDTF:  lineStatus += " RDTF ";
//                                        break;
//        }
//        for (int i = 0; i < SimulationParameters.TTL_PASSENGER; i++){
//            switch (passengerState[i]){
//                case AEPassengerStates.GTAP:    lineStatus += " GTAP ";
//                                                break;
//                case AEPassengerStates.INQE:    lineStatus += " INQE ";
//                                                break;
//                case AEPassengerStates.INFL:    lineStatus += " INFL ";
//                                                break;
//                case AEPassengerStates.ATDS:    lineStatus += " ATDS ";
//                                                break;
//            }
//        }
        
        /* check pilot state */
        if (pilotState == AEPilotStates.ATGR){
            lineStatus += " ATGR ";
        } else if (pilotState == AEPilotStates.RDFB){
            /* check and print flight status : BOARDING STARTED */
            if (previousPilotState == AEPilotStates.ATGR){
                log.writelnString ("\n Flight " + flight + ": boarding started.");
            }
            lineStatus += " RDFB ";
        } else if (pilotState == AEPilotStates.WTFB){
            lineStatus += " WTFB ";
        } else if (pilotState == AEPilotStates.FLFW){
            lineStatus += " FLFW ";
        } else if (pilotState == AEPilotStates.DRPP){
            /* check and print flight status : ARRIVED */
            if(previousPilotState == AEPilotStates.FLFW){
                log.writelnString ("\n Flight " + flight + ": arrived.");
            }
            lineStatus += " DRPP ";
        } else if (pilotState == AEPilotStates.FLBK){
            /* check and print flight status : RETURNING */
            if(previousPilotState == AEPilotStates.DRPP){
                log.writelnString ("\n Flight " + flight + ": returning."); 
                flight++;
            }      
            lineStatus += " FLBK ";
        }
        
        /* check hostess state */
        if (hostessState == AEHostessStates.WTFL){
            lineStatus += " WTFL ";
        } else if (hostessState == AEHostessStates.WTPS){
            lineStatus += " WTPS ";
        } else if (hostessState == AEHostessStates.CKPS){
            /* check and print flight status : PASSENGER _ CHECKED */
            if (previousHostessState == AEHostessStates.WTPS){
                log.writelnString ("\n Flight " + flight + ": passenger " + idPass + " checked.");
                numPass++;
                inQ--;
            }
            lineStatus += " CKPS ";
        } else if (hostessState == AEHostessStates.RDTF){
            /* check and print flight status : DEPARTED WITH _ PASSENGERS */
            if (previousHostessState == AEHostessStates.WTPS){
                log.writelnString ("\n Flight " + flight + ": departed with " + numOfPassengersOnFlight + " passengers.");
            }
            lineStatus += " RDTF ";
        }
        
        /* check passenger state */
        for (int i = 0; i < SimulationParameters.TTL_PASSENGER; i++){
            if (passengerState[i] == AEPassengerStates.GTAP){
                lineStatus += " GTAP ";
            } else if (passengerState[i] == AEPassengerStates.INQE){
                if (previousPassengerState[i] == AEPassengerStates.GTAP){
                    inQ++;
                } 
                lineStatus += " INQE ";
            } else if (passengerState[i] == AEPassengerStates.INFL){
                if (previousPassengerState[i] == AEPassengerStates.INQE){
                    inF++;
                } 
                lineStatus += " INFL ";
            } else if (passengerState[i] == AEPassengerStates.ATDS){
                if (previousPassengerState[i] == AEPassengerStates.INFL){
                    inF--;
                    pTAL++;
                }
                lineStatus += " ATDS ";
            }
        }
                
        lineStatus += "   " + inQ + "     " + inF + "    " + pTAL;
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
