package Plane;

public class SRPlane implements IPlane_Pilot, IPlane_Passenger {
    
    public SRPlane(){
        
    }
    
    //--------------------------------------------------------------------------
    //                                 PASSENGER                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void boardThePlane(){

    }
    
    @Override
    public synchronized void waitForEndOfFlight(){
        
    }
    
    @Override
    public synchronized void leaveThePlane(){

    }
    
    //--------------------------------------------------------------------------
    //                                 PILOT                               
    //--------------------------------------------------------------------------
    
    @Override
    public synchronized void waitForDeboard(){
        
    }
    
    @Override
    public synchronized void announceArrival(){
        
    }   
}
