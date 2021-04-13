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
    *   State of the pilot.
    */
    private int pilotState;

    /**
    *   State of the hostess.
    */
    private int hostessState;
   
    /**
    *   State of the passengers.
    */
    private final int [] passengerState;
    
    /**
    *   Number of flights.
    */
    private int numberOfFlights;

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
        hostessState = AEHostessStates.WTFL;
        passengerState = new int [SimulationParameters.TTL_PASSENGER];
        for (int i = 0; i < passengerState.length; i++)
            passengerState[i] = AEPassengerStates.GTAP;
        reportInitialStatus ();
        this.numberOfFlights = 0;
        this.inQ = 0;
        this.inF = 0;
        this.pTAL = 0;
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
        log.writelnString ("\t\t\t\t\t\tAirlift - Description of the internal state\n");
        log.writelnString ("  PT    HT    P00   P01   P02   P03   P04   P05   P06   P07   P08   P09   P10   P11   P12   P13   P14   P15   P16   P17   P18   P19   P20  InQ   InF  PTAL");
        if (!log.close ()){
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
        reportStatus ();
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
                                                break;
                case AEPassengerStates.INFL:    lineStatus += " INFL ";
                                                break;
                case AEPassengerStates.ATDS:    lineStatus += " ATDS ";
                                                break;
            }
        }
        
        lineStatus += "   " + inQ + "     " + inF + "     " + pTAL;
        
        log.writelnString (lineStatus);
        
        /* add new flight */
        log.writelnString ("\n Flight " + numberOfFlights++ + ":");
        
       
        if (!log.close ()){ 
            GenericIO.writelnString ("The operation of closing the file " + logFileName + " failed!");
            System.exit (1);
        }
   }
}
