package DepartureAirport;

import ActiveEntry.AEHostess;
import ActiveEntry.AEPassenger;
import GeneralRepository.GeneralRepos;
import commInfra.MemException;
import commInfra.MemFIFO;
import genclass.GenericIO;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SRDepartureAirport implements IDepartureAirport_Pilot, IDepartureAirport_Hostess, IDepartureAirport_Passenger {
    
    // configurations
    private MemFIFO<Integer> passengers;
    private int MAX_PASSENGER;
    private int MIN_PASSENGER;
    private int numberOfPassengerOnThePlane;
    private boolean passengerShowingDocuments;
    private boolean passengerChekedIn;
    private boolean hostessWaitingForNextPassenger;
    private boolean hostessAsksPassengerForDocuments;
    private boolean hostessInformPlaneReadyToTakeOff;
    private static boolean hostessInformPilotToEndActivity;
    private boolean pilotInformPlaneReadyForBoarding;
    
    /**
    *   Reference to the general repository.
    */
    private final GeneralRepos repos;
    
    /**
    *   Departure Airport instantiation.
    *   
    *   @param min minimum number of passengers per flight
    *   @param max maximum number of passengers per flight
    *   @param repos reference to the general repository
    */
    public SRDepartureAirport(int min, int max,int total, GeneralRepos repos){
        try {
            passengers = new MemFIFO<>(new Integer[total+1]);
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.MIN_PASSENGER = min;
        this.MAX_PASSENGER = max;
        numberOfPassengerOnThePlane = 0;
        passengerShowingDocuments = false;
        passengerChekedIn = false;
        hostessWaitingForNextPassenger = true;
        hostessAsksPassengerForDocuments = false;
        hostessInformPlaneReadyToTakeOff = false;
        hostessInformPilotToEndActivity = false;
        pilotInformPlaneReadyForBoarding = false;
        this.repos = repos;
    }
    
    //--------------------------------------------------------------------------
    //                                 HOSTESS                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void waitForNextFlight(){
        while(!pilotInformPlaneReadyForBoarding){
            GenericIO.writelnString("Hostess is waiting for the next flight to be ready to be boarded");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public synchronized void waitForNextPassenger(){
        notifyAll();
        int next;
        while(passengers.empty()){
            GenericIO.writelnString("Hostess is waiting for passengers");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try {
            next = passengers.read();
            System.out.println(next);
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
        notifyAll();
        GenericIO.writelnString("Hostess accepted a passenger for check in");
    }
    
    @Override
    public synchronized void waitToCheckPassenger(){
        GenericIO.writelnString("Hostess asked passenger for documents");
        hostessAsksPassengerForDocuments = true;
        notifyAll();
        while(!passengerShowingDocuments){
            GenericIO.writelnString("Hostess is waiting for passenger to give documents");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerShowingDocuments = false;
        GenericIO.writelnString("Hostess received and accepted documents");
        numberOfPassengerOnThePlane++;
    }
    
    @Override
    public synchronized void informPlaneReadyToTakeOff(){
        AEHostess hostess = (AEHostess) Thread.currentThread();
        if((passengers.empty() && numberOfPassengerOnThePlane > MIN_PASSENGER) || numberOfPassengerOnThePlane == MAX_PASSENGER || (passengers.empty() && hostess.checkIfAllPassengersAreAttended())){
            hostessInformPlaneReadyToTakeOff = true;
            if (hostess.checkIfAllPassengersAreAttended()){
                hostessInformPilotToEndActivity = true;
                GenericIO.writelnString("Hostess informs pilot that he can end activity");
            }
            notifyAll();
            GenericIO.writelnString("Hostess informs plane is ready to fly");
            while(pilotInformPlaneReadyForBoarding){
                try {
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            numberOfPassengerOnThePlane = 0;
            hostessInformPlaneReadyToTakeOff = false;
        }
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void travelToAirport(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        try {
            passengers.write(pass.getPassengerId());
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
        notifyAll();
        GenericIO.writelnString("Passenger " + pass.getPassengerId() + " go to airport");
    }
    
    @Override
    public synchronized void waitInQueue(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        GenericIO.writelnString("Passenger " + pass.getPassengerId() + " is waiting in queue");
        while(passengers.contains(pass.getPassengerId())){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public synchronized void showDocuments(){
        GenericIO.writelnString("Passenger is being asked for documents");
        while(!hostessAsksPassengerForDocuments){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerShowingDocuments = true;
        GenericIO.writelnString("Passenger showed documents");
        notifyAll();
    }

    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void informPlaneReadyForBoarding(){
        GenericIO.writelnString("Pilot informed plane is ready to be boarded");
        pilotInformPlaneReadyForBoarding = true;
        notifyAll();
    }
    
    @Override
    public synchronized void waitForAllInBoard(){
        GenericIO.writelnString("Pilot is waiting for the boarding to be finished");
        while(!hostessInformPlaneReadyToTakeOff){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        pilotInformPlaneReadyForBoarding = false;
        notifyAll(); 
        GenericIO.writelnString("Boarding is finished and pilot is going to fly");
    }
    
    // extras
    public static synchronized boolean informPilotToEndActivity(){
        return hostessInformPilotToEndActivity;
    }
    
    public synchronized void isInformPilotToCeaseActivity(boolean informPilotToCeaseActivity){
        this.hostessInformPilotToEndActivity = informPilotToCeaseActivity;
    }
}