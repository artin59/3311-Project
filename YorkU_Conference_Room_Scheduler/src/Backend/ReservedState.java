package Backend;

public class ReservedState implements RoomState {
    
    private static ReservedState instance = new ReservedState();
    
    private ReservedState() {}
    
    public static ReservedState getInstance() {
        return instance;
    }
    
    @Override
    public String getStateName() {
        return "Reserved";
    }
    
    @Override
    public void handle(RoomContext context) {
        System.out.println("Room " + context.getRoomId() + " is reserved.");
    }
    
    @Override
    public boolean canBook() {
        return false;
    }
    
    @Override
    public void checkIn(RoomContext context) {
        context.setState(InUseState.getInstance());
        System.out.println("Checked in to room " + context.getRoomId() + ". Room is now in use.");
    }
    
    @Override
    public void cancelBooking(RoomContext context) {
        context.clearBookingInfo();
        context.setState(AvailableState.getInstance());
        System.out.println("Booking cancelled for room " + context.getRoomId() + ". Room is now available.");
    }
    
    @Override
    public void extendBooking(RoomContext context, int additionalHours) {
        System.out.println("Booking extended by " + additionalHours + " hours for room " + context.getRoomId());
    }
    
    // Called when 30 minutes past start time and no check-in
    public void triggerNoShow(RoomContext context) {
        context.setState(NoShowState.getInstance());
        System.out.println("No-show triggered for room " + context.getRoomId());
    }
}