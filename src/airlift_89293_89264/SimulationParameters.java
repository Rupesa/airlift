package airlift_89293_89264;

/**
 *  Definition of the simulation parameters.
 */
public class SimulationParameters {
    
    /**
    *    Total number of passengers.
    */
    public static final int TTL_PASSENGER = 21;
   
    /**
    *   Maximum number of passengers per flight.
    */
    public static final int MAX_PASSENGER = 10;
    
    /**
    *   Minimum number of passengers per flight.
    */
    public static final int MIN_PASSENGER = 5;

    /**
    *   It can not be instantiated.
    */
    private SimulationParameters () { }
}
