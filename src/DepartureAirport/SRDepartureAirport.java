package DepartureAirport;

import ActiveEntry.AEHostess;
import ActiveEntry.AEPassenger;
import GeneralRepository.GeneralRepos;
import java.util.LinkedList;
import java.util.Queue;

public class SRDepartureAirport implements IDepartureAirport_Pilot, IDepartureAirport_Hostess, IDepartureAirport_Passenger {
    
    // configurations
    private Queue<Integer> passengers;
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
    public SRDepartureAirport(int min, int max, GeneralRepos repos){
        passengers = new LinkedList<>();
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
            System.out.println("Hostess is waiting for the next flight to be ready to be boarded");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public synchronized void waitForNextPassenger(){
        hostessWaitingForNextPassenger = false;
        notifyAll();
        int next;
        while(passengers.size() == 0){
            System.out.println("Hostess is waiting for passengers");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        next = passengers.remove();
        notifyAll();
        System.out.println("Hostess accepted a passenger for check in");
    }
    
    @Override
    public synchronized void askForDocuments(){
        System.out.println("Hostess asked passenger for documents");
        hostessAsksPassengerForDocuments = true;
        notifyAll();
    }
    
    @Override
    public synchronized void waitToCheckPassenger(){
        while(!passengerShowingDocuments){
            System.out.println("Hostess is waiting for passenger to give documents");
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        hostessWaitingForNextPassenger = true;
        passengerShowingDocuments = false;
        System.out.println("Hostess received and accepted documents");
        notifyAll();
        while(!passengerChekedIn){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerChekedIn = false;
    }
    
    @Override
    public synchronized void informPlaneReadyToFly(){
        AEHostess hostess = (AEHostess) Thread.currentThread();
        if((passengers.size() == 0 && numberOfPassengerOnThePlane > MIN_PASSENGER) || numberOfPassengerOnThePlane == MAX_PASSENGER || (passengers.size() == 0 && hostess.checkIfAllPassengersAreAttended())){
            hostessInformPlaneReadyToTakeOff = true;
            if (hostess.checkIfAllPassengersAreAttended()){
                hostessInformPilotToEndActivity = true;
                System.out.println("Hostess informs pilot that he can end activity");
            }
            notifyAll();
            System.out.println("Hostess informs plane is ready to fly");
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
        passengers.add(pass.getPassengerId());
        notifyAll();
        System.out.println("Passenger " + pass.getPassengerId() + " go to airport");
    }
    
    @Override
    public synchronized void waitInQueue(){
        AEPassenger pass = (AEPassenger) Thread.currentThread();
        System.out.println("Passenger " + pass.getPassengerId() + " is waiting in queue");
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
        System.out.println("Passenger is being asked for documents");
        while(!hostessAsksPassengerForDocuments){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerShowingDocuments = true;
        notifyAll();
        System.out.println("Passenger showed documents");
    }
    
    @Override
    public synchronized void waitToBeCheckedDocuments(){
        System.out.println("Passenger waiting to be cheked documents");
        while(!hostessWaitingForNextPassenger){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        passengerChekedIn = true;
        numberOfPassengerOnThePlane++;
        System.out.println("Passenger was checked documents");
        notifyAll();
    }

    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void informPlaneReadyForBoarding(){
        System.out.println("Pilot informed plane is ready to be boarded");
        pilotInformPlaneReadyForBoarding = true;
        notifyAll();
    }
    
    @Override
    public synchronized void waitForAllInBoard(){
        System.out.println("Pilot is waiting for the boarding to be finished");
        while(!hostessInformPlaneReadyToTakeOff){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        pilotInformPlaneReadyForBoarding = false;
        notifyAll(); 
        System.out.println("Boarding is finished and pilot is going to fly");
    }
    
    // extras
    public static synchronized boolean informPilotToEndActivity(){
        return hostessInformPilotToEndActivity;
    }
    
    public synchronized void isInformPilotToCeaseActivity(boolean informPilotToCeaseActivity){
        this.hostessInformPilotToEndActivity = informPilotToCeaseActivity;
    }
}