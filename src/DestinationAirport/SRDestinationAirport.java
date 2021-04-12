package DestinationAirport;

public class SRDestinationAirport implements IDestinationAirport_Passenger {
    
    // configurations
    private int numberOfPassengersLeavingThePlane;
    
    // constructor
    public SRDestinationAirport(){
        
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void leaveAirport(){
        numberOfPassengersLeavingThePlane++;
    }
}