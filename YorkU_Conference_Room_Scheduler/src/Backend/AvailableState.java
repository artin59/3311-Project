package Backend;

public class AvailableState implements RoomState {
    
    private static AvailableState instance = new AvailableState();
    
    private AvailableState() {}
    
    public static AvailableState getInstance() {
        return instance;
    }
    
    @Override
    public String getStateName() {
        return "Available";
    }
    
    @Override
    public void handle(RoomContext context) {
        System.out.println("Room " + context.getRoomId() + " is available for booking.");
    }
    
    @Override
    public boolean canBook() {
        return true;
    }
    
    @Override
    public void checkIn(RoomContext context) {
        System.out.println("Cannot check in - room is not reserved.");
    }
    
    @Override
    public void setMaintenance(RoomContext context) {
        context.setState(MaintenanceState.getInstance());
        System.out.println("Room " + context.getRoomId() + " is now under maintenance.");
    }
}