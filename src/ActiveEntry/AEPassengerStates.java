package ActiveEntry;

/** 
 *    Definition of the internal states of the passenger during his life cycle.
 */
public class AEPassengerStates {
    
    /**
    *   The passenger goes to the departure airport.
    */
    public static final int GTAP = 0;
    
    /**
    *   The passenger queue at the boarding gate waiting for the flight to be announced.
    */
    public static final int INQE = 1;
    
    /**
    *   The passenger flies to the destination airport.
    */
    public static final int INFL = 2;
    
    /**
    *   The passenger arrives at the destination airport, disembarks and leaves the airport.
    */
    public static final int ATDS = 3;
    
    /**
    *   It can not be instantiated.
    */
    private AEPassengerStates () { }
}
