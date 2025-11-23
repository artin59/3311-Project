package Backend;

public class NoShowState implements RoomState {
    
    private static NoShowState instance = new NoShowState();
    
    private NoShowState() {}
    
    public static NoShowState getInstance() {
        return instance;
    }
    
    @Override
    public String getStateName() {
        return "NoShow";
    }
    
    @Override
    public void handle(RoomContext context) {
        System.out.println("Room " + context.getRoomId() + " had a no-show. Processing...");
        // Automatically transition to Available
        cancelBooking(context);
    }
    
    @Override
    public boolean canBook() {
        return false; // Will be available after cancelBooking is processed
    }
    
    @Override
    public void cancelBooking(RoomContext context) {
        // Clear booking info and make room available again
        // Note: Deposit is already forfeited at this point (handled when entering NoShow state)
        context.clearBookingInfo();
        context.setState(AvailableState.getInstance());
        System.out.println("No-show processed for room " + context.getRoomId() + ". Room is now available.");
        // TODO: Record no-show against user account (modular for future implementation)
    }
}