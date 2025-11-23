package Backend;

public class InUseState implements RoomState {
    
    private static InUseState instance = new InUseState();
    
    private InUseState() {}
    
    public static InUseState getInstance() {
        return instance;
    }
    
    @Override
    public String getStateName() {
        return "InUse";
    }
    
    @Override
    public void handle(RoomContext context) {
        System.out.println("Room " + context.getRoomId() + " is currently in use.");
    }
    
    @Override
    public boolean canBook() {
        return false;
    }
    
    @Override
    public void checkOut(RoomContext context) {
        context.clearBookingInfo();
        context.setState(AvailableState.getInstance());
        System.out.println("Checked out of room " + context.getRoomId() + ". Room is now available.");
    }
    
    @Override
    public void extendBooking(RoomContext context, int additionalHours) {
        // TODO: Check if extension is possible (no conflicting bookings)
        // TODO: Additional payment logic for extension
        System.out.println("Booking extended by " + additionalHours + " hours for room " + context.getRoomId());
    }
}