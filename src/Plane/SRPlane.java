package Plane;

import ActiveEntry.AEPassenger;
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
//    private Queue<Integer> passengers;
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
        //passengers = new LinkedList<>();
        try {
            passengers = new MemFIFO<>(new Integer[total+1]);
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
//        try{
//            passengers = new MemFIFO<> (new Integer [SimulationParameters.TTL_PASSENGER]);
//        }
//        catch (MemException e){
//            GenericIO.writelnString ("Instantiation of waiting FIFO failed: " + e.getMessage ());
//            passengers = null;
//            System.exit (1);
//        }
        this.lastPassengerLeaveThePlane = false;
        this.pilotAnnounceArrival = false;
        this.repos = repos;
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void boardThePlane(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        try {
            passengers.write(pass.getPassengerId());
        } catch (MemException ex) {
            Logger.getLogger(SRPlane.class.getName()).log(Level.SEVERE, null, ex);
        }
//        passengers.write(pass.getPassengerId());
        GenericIO.writelnString("Passenger " + pass.getPassengerId() + " boarded the plane");  
    }
    
    @Override
    public synchronized void waitForEndOfFlight(){
        GenericIO.writelnString("Passenger is waiting for end of flight");  
        while(!pilotAnnounceArrival){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public synchronized void leaveThePlane(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        passengers.remove(pass.getPassengerId());
        GenericIO.writelnString("Passenger " + pass.getPassengerId() + " left the plane");  
        if (passengers.empty()){
            lastPassengerLeaveThePlane = true;
            notifyAll();
            GenericIO.writelnString("Passenger " + pass.getPassengerId() + " notified the pilot that he is the last");  
        }
    }
    
    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void announceArrival(){
        GenericIO.writelnString("Pilot announced the arrival");
        pilotAnnounceArrival = true;
        notifyAll();
        System.out.print("Pilot is waiting for deboarding");  
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
}
