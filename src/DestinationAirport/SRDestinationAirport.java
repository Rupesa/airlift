package DestinationAirport;

import GeneralRepository.GeneralRepos;

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
        
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void leaveAirport(){
        numberOfPassengersLeavingThePlane++;
    }
}