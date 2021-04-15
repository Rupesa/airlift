package Plane;

import ActiveEntry.AEHostess;
import ActiveEntry.AEHostessStates;
import ActiveEntry.AEPassenger;
import ActiveEntry.AEPassengerStates;
import ActiveEntry.AEPilot;
import ActiveEntry.AEPilotStates;
import DepartureAirport.SRDepartureAirport;
import GeneralRepository.GeneralRepos;
import airlift_89293_89264.SimulationParameters;
import commInfra.MemException;
import commInfra.MemFIFO;
import genclass.GenericIO;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Plane.
 *
 *  Is implemented as an implicit monitor.
 *  All public methods are executed in mutual exclusion.
 */
public class SRPlane implements IPlane_Pilot, IPlane_Passenger {
    
    /**
    *   Passenger queue on the plane.
    */
    private MemFIFO<Integer> passengers;  
    
    private boolean lastPassengerLeaveThePlane;
    private boolean pilotAnnounceArrival;
    
    /**
    *   Reference to the general repository.
    */
    private final GeneralRepos repos;
    
    /**
    *   Plane instantiation.
    *   
    *   @param repos reference to the general repository
    */
    public SRPlane(int total, GeneralRepos repos){
        try {
            passengers = new MemFIFO<>(new Integer[total+1]);
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lastPassengerLeaveThePlane = false;
        this.pilotAnnounceArrival = false;
        this.repos = repos;
    }
    
    /* ****************************** PASSENGER ***************************** */
    
    /**
    *   The passenger boards the plane.
    *   
    *   It is called by a passenger.
    */   
    @Override
    public synchronized void boardThePlane(){
        /* add passenger to the queue of passengers */
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        try {
            passengers.write(pass.getPassengerId());
        } catch (MemException ex) {
            Logger.getLogger(SRPlane.class.getName()).log(Level.SEVERE, null, ex);
        }
        GenericIO.writelnString("16 - Passenger " + pass.getPassengerId() + " boarded the plane");  
        
        /* change state of passanger : INQE -> INFL */
        pass.setPassengerState(AEPassengerStates.INFL);
        repos.setPassengerState(pass.getPassengerId(), pass.getPassengerState());
    }
    
    /**
    *   The passenger waits for the flight to end.
    *   
    *   It is called by a passenger.
    */   
    @Override
    public synchronized void waitForEndOfFlight(){
        /* wait for the flight to end */
        GenericIO.writelnString("17 - Passenger is waiting for end of flight");  
        while(!pilotAnnounceArrival){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    /**
    *   The passenger leaves the plane and, if he is the last to leave, notifies the pilot that he is the last passenger of the plane.
    *   
    *   It is called by a passenger.
    */ 
    @Override
    public synchronized void leaveThePlane(){
        /* remove passenger from the queue of passangers */
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        passengers.remove(pass.getPassengerId());
        
        /* change state of passanger : INFL -> ATDS */
        pass.setPassengerState(AEPassengerStates.ATDS);
        repos.setPassengerState(pass.getPassengerId(), pass.getPassengerState());
        
        GenericIO.writelnString("Passenger " + pass.getPassengerId() + " left the plane");  
        if (passengers.empty()){
            lastPassengerLeaveThePlane = true;
            notifyAll();
            GenericIO.writelnString("19 - Passenger " + pass.getPassengerId() + " notified the pilot that he is the last");  
        }
    }
    
    /* ******************************** PILOT ******************************* */
    
    /**
    *   The pilot announces the arrival and waits all passengers to leave.
    *   
    *   It is called by a pilot.
    */ 
    @Override
    public synchronized void announceArrival(){
        
        /* announce arrival */ 
        GenericIO.writelnString("20 - Pilot announced the arrival");
        pilotAnnounceArrival = true;
        notifyAll();
        
        /* wait for the last passenger to notify that he is the last to leave */
        GenericIO.writelnString("21 - Pilot is waiting for deboarding");
        while(!lastPassengerLeaveThePlane){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        pilotAnnounceArrival = false;
        lastPassengerLeaveThePlane = false;
        notifyAll();
        
        /* change state of pilot : FLFW -> DRPP */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.DRPP);
        repos.setPilotState(pilot.getPilotState());
        
    } 
}
