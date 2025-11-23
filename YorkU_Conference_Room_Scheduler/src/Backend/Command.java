package Backend;

/**
 * Command interface for the Command pattern
 */
public interface Command {
    /**
     * Execute the command
     * @return true if successful, false otherwise
     */
    boolean execute();
    
    /**
     * Undo the command (if supported)
     * @return true if successful, false otherwise
     */
    boolean undo();
}

