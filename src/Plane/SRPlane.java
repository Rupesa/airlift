package Plane;

import ActiveEntry.AEPassenger;
import GeneralRepository.GeneralRepos;
import airlift_89293_89264.SimulationParameters;
import commInfra.MemException;
import commInfra.MemFIFO;
import genclass.GenericIO;
import java.util.LinkedList;
import java.util.Queue;

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
    private Queue<Integer> passengers;
//    private MemFIFO<Integer> passengers;  
    
    
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
    public SRPlane(GeneralRepos repos){
        passengers = new LinkedList<>();
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
        passengers.add(pass.getPassengerId());
//        passengers.write(pass.getPassengerId());
        System.out.println("Passenger " + pass.getPassengerId() + " boarded the plane");  
    }
    
    @Override
    public synchronized void waitForEndOfFlight(){
        System.out.println("Passenger is waiting for end of flight");  
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
        System.out.println("Passenger " + pass.getPassengerId() + " left the plane");  
        if (passengers.size() == 0){
            lastPassengerLeaveThePlane = true;
            notifyAll();
            System.out.println("Passenger " + pass.getPassengerId() + " notified the pilot that he is the last");  
        }
    }
    
    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void announceArrival(){
        System.out.println("Pilot announced the arrival");
        pilotAnnounceArrival = true;
        notifyAll();
    } 
    
    @Override
    public synchronized void waitForDeboard(){
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
