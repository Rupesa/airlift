package DepartureAirport;

public class SRDepartureAirport implements IDepartureAirport_Pilot, IDepartureAirport_Hostess, IDepartureAirport_Passenger {
    
    public SRDepartureAirport(){
        
    }
    
    //--------------------------------------------------------------------------
    //                                 HOSTESS                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void waitForNextFlight(){
        
    }
    
    @Override
    public synchronized void waitForNextPassenger(){
        
    }
    
    @Override
    public synchronized void askForDocuments(){
        
    }
    
    @Override
    public synchronized void waitToCheckDocuments(){
        
    }
    
    @Override
    public synchronized void informPlaneReadyToFly(){
        
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void travelToAirport(){

    }
    
    @Override
    public synchronized void waitInQueue(){
        
    }
    
    @Override
    public synchronized void showDocuments(){
        
    }
    
    @Override
    public synchronized void waitToBeCheckedDocuments(){
        
    }

    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void informPlaneReadyForBoarding(){
        
    }
    
    @Override
    public synchronized void waitForAllInBoard(){
        
    }
}