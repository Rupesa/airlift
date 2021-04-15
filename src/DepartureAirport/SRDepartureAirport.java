package DepartureAirport;

import ActiveEntry.AEHostess;
import ActiveEntry.AEHostessStates;
import ActiveEntry.AEPassenger;
import ActiveEntry.AEPassengerStates;
import ActiveEntry.AEPilot;
import ActiveEntry.AEPilotStates;
import GeneralRepository.GeneralRepos;
import commInfra.MemFIFO;
import commInfra.MemException;
import airlift_89293_89264.SimulationParameters;
import genclass.GenericIO;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Departure Airport.
 *
 *  Is implemented as an implicit monitor.
 *  All public methods are executed in mutual exclusion.
 */
public class SRDepartureAirport implements IDepartureAirport_Pilot, IDepartureAirport_Hostess, IDepartureAirport_Passenger {
    
    // configurations
    private MemFIFO<Integer> passengers;
    private int MAX_PASSENGER;
    private int MIN_PASSENGER;
    private int numberOfPassengerOnThePlane;
    private int numberOfFilght;
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
        numberOfFilght = 0;
        passengerShowingDocuments = false;
        passengerChekedIn = false;
        hostessWaitingForNextPassenger = true;
        hostessAsksPassengerForDocuments = false;
        hostessInformPlaneReadyToTakeOff = false;
        hostessInformPilotToEndActivity = false;
        pilotInformPlaneReadyForBoarding = false;
        this.repos = repos;
    }
    
    /* ******************************* HOSTESS ****************************** */
    
    /**
    *   The hostess waits that the next flight is ready to be boarded.
    *
    *   It is called by a hostess.
    */
    @Override
    public synchronized void waitForNextFlight(){
        /* wait for the pilot to notify that he is ready to board */
        while(!pilotInformPlaneReadyForBoarding){
            GenericIO.writelnString("1 - Hostess is waiting for the next flight to be ready to be boarded");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        
        /* change state of hostess : RDTF -> WTFL */
        AEHostess hostess = (AEHostess) Thread.currentThread();
        hostess.setHostessState(AEHostessStates.WTFL);
        repos.setHostessState(hostess.getHostessState());
    }
    
    /**
    *   The hostess waits for the next passenger in the queue. 
    *   When there is a passenger, the hostess removes him from the queue and starts checking in.
    *
    *   It is called by a hostess.
    */
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
        GenericIO.writelnString("3 - Hostess accepted a passenger for check in");
        
        /* change state of hostess : CKPS -> WTPS */
        AEHostess hostess = (AEHostess) Thread.currentThread();
        hostess.setHostessState(AEHostessStates.WTPS);
        repos.setHostessState(hostess.getHostessState());
    }
    
    /**
    *   The hostess asks the passenger for the documents and waits for him to deliver them. 
    *   When the passenger shows the documents, the hostess accepts it and adds it to the flight.
    *
    *   It is called by a hostess.
    */
    @Override
    public synchronized void waitToCheckPassenger(){
        /* ask for documents from the passenger */
        GenericIO.writelnString("4 - Hostess asked passenger for documents");
        hostessAsksPassengerForDocuments = true;
        notifyAll();
        
        /* wait for the passenger to show the documents */
        while(!passengerShowingDocuments){
            GenericIO.writelnString("5 - Hostess is waiting for passenger to give documents");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerShowingDocuments = false;
        GenericIO.writelnString("6 - Hostess received and accepted documents");
        numberOfPassengerOnThePlane++;
        
        /* change state of hostess : WTPS -> CKPS */
        AEHostess hostess = (AEHostess) Thread.currentThread();
        hostess.setHostessState(AEHostessStates.CKPS);
        repos.setHostessState(hostess.getHostessState());
    }
    
    /**
    *  The hostess informs the pilot that the plane is ready to take off. When:
    *       There are no more passengers in the queue and the plane already has the minimum number of passengers for boarding;
    *       The number of passengers on the plane has already reached its maximum;
    *       There are no more passengers in the queue (because they are the last) and they are all already checked in.
    * 
    *   It is called by a hostess.
    */
    @Override
    public synchronized void informPlaneReadyToTakeOff(){
        /* check if the flight has the conditions to take off */
        AEHostess hostess = (AEHostess) Thread.currentThread();
        if((passengers.empty() && numberOfPassengerOnThePlane > MIN_PASSENGER) || numberOfPassengerOnThePlane == MAX_PASSENGER || (passengers.empty() && hostess.checkIfAllPassengersAreAttended())){
            hostessInformPlaneReadyToTakeOff = true;
            if (hostess.checkIfAllPassengersAreAttended()){
                hostessInformPilotToEndActivity = true;
                GenericIO.writelnString("7 - Hostess informs pilot that he can end activity");
            }
            notifyAll();
            GenericIO.writelnString("8 - Hostess informs plane is ready to fly");
            
            /* wait for the pilot to report that he is ready to board */
            while(pilotInformPlaneReadyForBoarding){
                try {
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            numberOfPassengerOnThePlane = 0;
            hostessInformPlaneReadyToTakeOff = false;
            
            /* change state of hostess : WTPS -> RDTF */
            hostess.setHostessState(AEHostessStates.RDTF);
            repos.setHostessState(hostess.getHostessState());
        }
    }
    
    /* ****************************** PASSENGER ***************************** */
       
    /**
    *   The passenger goes to the airport. 
    *   
    *   It is called by a passenger.
    */
    @Override
    public synchronized void travelToAirport(){
        /* going to airport */
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        try {
            passengers.write(pass.getPassengerId());
        } catch (MemException ex) {
            Logger.getLogger(SRDepartureAirport.class.getName()).log(Level.SEVERE, null, ex);
        }
        notifyAll();
        GenericIO.writelnString("9 - Passenger " + pass.getPassengerId() + " go to airport");
    }
    
    /**
    *   The passenger waits in line to check in.
    *   
    *   It is called by a passenger.
    */
    @Override
    public synchronized void waitInQueue(){
        /* wait in line until the hostess starts checking in */
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        GenericIO.writelnString("10 - Passenger " + pass.getPassengerId() + " is waiting in queue");
        while(passengers.contains(pass.getPassengerId())){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        
        /* change state of passenger : GTAP -> INQE */
        pass.setPassengerState(AEPassengerStates.INQE);
        repos.setPassengerState(pass.getPassengerId(), pass.getPassengerState());
    }
    
    /**
    *   The passenger is asked to show his documents and he shows them to the hostess.
    *   
    *   It is called by a passenger.
    */
    @Override
    public synchronized void showDocuments(){
        /* wait for the hostess to ask you for the documents */
        GenericIO.writelnString("11 - Passenger is being asked for documents");
        while(!hostessAsksPassengerForDocuments){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerShowingDocuments = true;
        GenericIO.writelnString("12 - Passenger showed documents");
        notifyAll();
    }

    /* ******************************** PILOT ******************************* */
     
    /**
    *   The pilot informs the plane that he is ready to board.
    *   
    *   It is called by a pilot.
    */
    @Override
    public synchronized void informPlaneReadyForBoarding(){
        /* wait for the hostess to notify you that the plane is ready to take off */
        GenericIO.writelnString("13 - Pilot informed plane is ready to be boarded");
        pilotInformPlaneReadyForBoarding = true;
        notifyAll();
        
        /* change state of pilot : ATRG -> RDFB */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.RDFB);
        repos.setPilotState(pilot.getPilotState());
    }
    
    /**
    *   The pilot waits for the passengers to be on the plane and then is ready to fly.
    *   
    *   It is called by a pilot.
    */
    @Override
    public synchronized void waitForAllInBoard(){
        /* wait for the hostess to notify you that the plane is ready to take off */
        GenericIO.writelnString("14 - Pilot is waiting for the boarding to be finished");
        while(!hostessInformPlaneReadyToTakeOff){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        pilotInformPlaneReadyForBoarding = false;
        notifyAll(); 
        GenericIO.writelnString("15 - Boarding is finished and pilot is going to fly ----------------------- " + numberOfPassengerOnThePlane);
        
        /* set information of flight */
        numberOfFilght++;
        repos.setInfoBoardPlane(numberOfFilght, numberOfPassengerOnThePlane);
        
        /* change state of pilot : RDFB -> WTFB */
        AEPilot pilot = (AEPilot) Thread.currentThread();
        pilot.setPilotState(AEPilotStates.WTFB);
        repos.setPilotState(pilot.getPilotState());
    }
    
    
    public static synchronized boolean informPilotToEndActivity(){
        return hostessInformPilotToEndActivity;
    }
    
    public synchronized void isInformPilotToCeaseActivity(boolean informPilotToCeaseActivity){
        this.hostessInformPilotToEndActivity = informPilotToCeaseActivity;
    }
}