package Plane;

import ActiveEntry.AEPassenger;
import java.util.LinkedList;
import java.util.Queue;

public class SRPlane implements IPlane_Pilot, IPlane_Passenger {
    
    // configurations
    private Queue<Integer> passengers;
    private boolean lastPassengerLeaveThePlane;
    private boolean pilotAnnounceArrival;
    
    // constructor
    public SRPlane(){
        passengers = new LinkedList<>();
        this.lastPassengerLeaveThePlane = false;
        this.pilotAnnounceArrival = false;
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void boardThePlane(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        passengers.add(pass.getPassengerId());
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
