package ActiveEntry;

/** 
 *    Definition of the internal states of the pilot during his life cycle.
 */
public class AEPilotStates {
    
    /**
    *   The pilot returns to the airport and prepares for a new departure. 
    */
    public static final int ATGR = 0;
        
    /**
    *   The pilot announces that he is ready for boarding.
    */
    public static final int RDFB = 1;
    
    /**
    *   The pilot waits the plane to be ready to board.
    */
    public static final int WTFB = 2;
    
    /**
    *   The pilot flies to the destination airport.
    */
    public static final int FLFW = 3;
    
    /**
    *   The pilot arrives at the destination airport and waits passengers to disembark.
    */
    public static final int DRPP = 4;
    
    /**
    *   The pilot flies brack to the departure airport.
    */
    public static final int FLBK = 5;
    
    /**
    *   It can not be instantiated.
    */
    private AEPilotStates () { }
}