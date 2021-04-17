package Plane;

import ActiveEntry.AEPassenger;
import ActiveEntry.AEPassengerStates;
import ActiveEntry.AEPilot;
import ActiveEntry.AEPilotStates;
import DepartureAirport.SRDepartureAirport;
import GeneralRepository.GeneralRepos;
import commInfra.MemException;
import commInfra.MemFIFO;
import genclass.GenericIO;
import static java.lang.Thread.sleep;
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
        
         /* change state of passanger to INFL */
        pass.setPassengerState(AEPassengerStates.INFL);
        repos.setPassengerState(pass.getPassengerId(), pass.getPassengerState());
        
        try {
            passengers.write(pass.getPassengerId());
        } catch (MemException ex) {
            Logger.getLogger(SRPlane.class.getName()).log(Level.SEVERE, null, ex);
        }
        GenericIO.writelnString("(16) Passenger " + pass.getPassengerId() + " boarded the plane");  
        
       
    }
    
    /**
    *   The passenger waits for the flight to end.
    *   
    *   It is called by a passenger.
    */   
    @Override
    public synchronized void waitForEndOfFlight(){
        /* wait for the flight to end */
        GenericIO.writelnString("(17) Passenger is waiting for end of flight");  
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
        GenericIO.writelnString("(19) Passenger " + pass.getPassengerId() + " left the plane"); 
        
        /* change state of passanger to ATDS */
        pass.setPassengerState(AEPassengerStates.ATDS);
        repos.setPassengerState(pass.getPassengerId(), pass.getPassengerState());
        
        /* the last passenger notifies the pilot that he is the last */
        if (passengers.empty()){
            lastPassengerLeaveThePlane = true;
            notifyAll();
            GenericIO.writelnString("(20) Passenger " + pass.getPassengerId() + " notified the pilot that he is the last");  
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
        GenericIO.writelnString("(21) Pilot announced the arrival");
        pilotAnnounceArrival = true;
        notifyAll();
        
        /* change state of pilot to DRPP */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.DRPP);
        repos.setPilotState(pilot.getPilotState());
        
        /* wait for the last passenger to notify that he is the last to leave */
        GenericIO.writelnString("(22) Pilot is waiting for deboarding");
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
        
        
    } 
    
    /**
    *   The pilot flight to the destination airport.
    *   
    *   It is called by a pilot.
    */ 
    @Override
    public synchronized void flyToDestinationPoint(){
        /* change state of pilot to FLFW */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.FLFW);
        repos.setPilotState(pilot.getPilotState());
        
        /* fly to destinaton airport */ 
        GenericIO.writelnString("(23) Pilot flies to destination point");
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

    } 
    
    /**
    *   The pilot flight to the departure airport.
    *   
    *   It is called by a pilot.
    */ 
    @Override
    public synchronized void flyToDeparturePoint(){
        /* change state of pilot to FLBK */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.FLBK);
        repos.setPilotState(pilot.getPilotState());
        
        /* fly to departure airport */ 
        GenericIO.writelnString("(24) Pilot flies to departure point");
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

    } 
    
    /**
    *   The pilot parks the plane at the transfer gate.
    *   
    *   It is called by a pilot.
    */ 
    @Override
    public synchronized void parkAtTransferGate(){
        /* change state of pilot to ATGR */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.ATGR);
        repos.setPilotState(pilot.getPilotState());
        
        /* parks the plane at the transfer gate */ 
        GenericIO.writelnString("(25) Pilot park at transfer gate");
        try{
            sleep ((long) (1 + 20 * Math.random ()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       
    } 
}
