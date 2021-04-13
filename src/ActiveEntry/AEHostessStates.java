package ActiveEntry;

/** 
 *    Definition of the internal states of the hostess during his life cycle.
 */
public class AEHostessStates {
    
    /**
    *   The hostess waits the plane to be available for boarding.
    */
    public static final int WTFL = 0;
        
    /**
    *   The hostess waits passengers to arrive at the airport to check in.
    */
    public static final int WTPS = 1;
    
    /**
    *   The hostess checks passengers in queue.
    */
    public static final int CKPS = 2;
    
    /**
    *   The hostess announces that there are passengers to board.
    */
    public static final int RDTF = 3;
        
    /**
    *   It can not be instantiated.
    */
    private AEHostessStates () { }
}