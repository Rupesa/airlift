package commInfra;

/**
 *  Parametric FIFO derived from a parametric memory.
 *  Errors are reported.
 *
 *  @param <R> data type of stored objects
 */
public class MemFIFO<R> extends MemObject<R>{
    /**
    *   Pointer to the first empty location.
    */
    private int inPnt;

    /**
    *   Pointer to the first occupied location.
    */
    private int outPnt;

    /**
    *   Signaling FIFO empty state.
    */
   private boolean empty;

    /**
    *   FIFO instantiation.
    *   The instantiation only takes place if the memory exists.
    *   Otherwise, an error is reported.
    *
    *   @param storage memory to be used
    *   @throws MemException when the memory does not exist
    */
    public MemFIFO (R [] storage) throws MemException{
        super (storage);
        inPnt = outPnt = 0;
        empty = true;
    }

    /**
    *   FIFO insertion.
    *   A parametric object is written into it.
    *   If the FIFO is full, an error is reported.
    *
    *   @param val parametric object to be written
    *   @throws MemException when the FIFO is full
    */
    @Override
    public void write (R val) throws MemException{
        if ((inPnt != outPnt) || empty){
            mem[inPnt] = val;
            inPnt = (inPnt + 1) % mem.length;
            empty = false;
        }
        else
            throw new MemException (inPnt+" - "+outPnt+" - "+(inPnt != outPnt)+" - Fifo full!");
   }

    /**
    *   FIFO retrieval.
    *   A parametric object is read from it.
    *   If the FIFO is empty, an error is reported.
    *
    *   @return first parametric object that was written
    *   @throws MemException when the FIFO is empty
    */
    @Override
    public R read () throws MemException{
        R val;

        if (!empty){
            val = mem[outPnt];
            outPnt = (outPnt + 1) % mem.length;
            empty = (inPnt == outPnt);
        }
        else 
            throw new MemException ("Fifo empty!");
        return val;
    }

    /**
    *   Test FIFO current full status.
    *
    *   @return true, if FIFO is full -
    *            false, otherwise
    */
    public boolean full (){
        return !((inPnt != outPnt) || empty);
    }
    
    /**
    *   Test FIFO current empty status.
    *
    *   @return true, if FIFO is empty -
    *            false, otherwise
    */
    public boolean empty (){
        return empty;
    }
    
    /**
    *   Verify if FIFO contains val.
    *
    *   @param val parametric object to be checked
    *   @return true, if FIFO contains -
    *            false, otherwise
    */
    public boolean contains(R val){
        for (int i = outPnt; i < inPnt; i = (i + 1) % mem.length) {
            if (mem[i].equals(val)) {
                return true;
            }
        }
        return false;
    }
    
    /**
    *   Removes parametric object from FIFO.
    *
    *   @param val parametric object to be removed
    */
    public void remove(R val){
        boolean shift = false;
        int i = outPnt;
        int next;
        for (int n = 0; n < mem.length-1; n++) {
            next = (i + 1) % mem.length;
            if (mem[i] != null && mem[i].equals(val)) {
                shift = true;
            }
            if (shift){
                mem[i] = mem[next];
            }
            i = next;
        }
        if (shift) inPnt = (inPnt - 1) % mem.length;
        empty = (inPnt == outPnt);
    }
}
