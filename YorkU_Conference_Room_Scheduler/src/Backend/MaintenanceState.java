package Backend;

public class MaintenanceState implements RoomState {
    
    private static MaintenanceState instance = new MaintenanceState();
    
    private MaintenanceState() {}
    
    public static MaintenanceState getInstance() {
        return instance;
    }
    
    @Override
    public String getStateName() {
        return "Maintenance";
    }
    
    @Override
    public void handle(RoomContext context) {
        System.out.println("Room " + context.getRoomId() + " is under maintenance.");
    }
    
    @Override
    public boolean canBook() {
        return false;
    }
    
    @Override
    public void cancelBooking(RoomContext context) {
        // If room goes to maintenance while reserved, cancel the booking
        context.clearBookingInfo();
        System.out.println("Booking cancelled due to maintenance for room " + context.getRoomId());
        // TODO: Full refund logic can be added here
    }
    
    @Override
    public void clearMaintenance(RoomContext context) {
        context.setState(AvailableState.getInstance());
        System.out.println("Maintenance cleared for room " + context.getRoomId() + ". Room is now available.");
    }
}