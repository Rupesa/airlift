package DestinationAirport;

import GeneralRepository.GeneralRepos;
import genclass.GenericIO;

/**
 *  Destination Airport.
 *
 *  Is implemented as an implicit monitor.
 *  All public methods are executed in mutual exclusion.
 */
public class SRDestinationAirport implements IDestinationAirport_Passenger {
    
    // configurations
    private int numberOfPassengersLeavingThePlane;
    
    /**
    *   Reference to the general repository.
    */
    private final GeneralRepos repos;

    /**
    *   Destination Airport instantiation.
    *   
    *   @param repos reference to the general repository
    */
    public SRDestinationAirport(GeneralRepos repos){
        this.repos = repos;
    }
        
    /* ****************************** PASSENGER ***************************** */
    
    /**
    *   The passenger leaves airport.
    *   
    *   It is called by a passenger.
    */  
    @Override
    public synchronized void leaveAirport(){
        GenericIO.writelnString("22 - Passenger leave airport");
        numberOfPassengersLeavingThePlane++;
    }
}