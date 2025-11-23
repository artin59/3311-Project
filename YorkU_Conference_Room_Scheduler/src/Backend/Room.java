package Backend;

import java.util.UUID;

public class Room {
    
    private UUID roomId;
    private int capacity;
    private String buildingName;
    private String roomNumber;
    private String status; // ENABLED, DISABLED
    private RoomContext roomContext; // State pattern context
    
    public Room(int capacity, String buildingName, String roomNumber) {
        this.roomId = UUID.randomUUID();
        this.capacity = capacity;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.status = "ENABLED";
        this.roomContext = new RoomContext(roomId);
    }
    
    // Constructor for loading from CSV
    public Room(UUID roomId, int capacity, String buildingName, String roomNumber, 
                String status, String stateName) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.status = status;
        this.roomContext = new RoomContext(roomId, parseState(stateName));
    }
    
    // Constructor with booking info (for loading from CSV)
    public Room(UUID roomId, int capacity, String buildingName, String roomNumber,
                String status, String stateName, UUID bookingUserId, 
                String bookingDate, String bookingStartTime, String bookingEndTime) {
        this(roomId, capacity, buildingName, roomNumber, status, stateName);
        if (bookingUserId != null) {
            this.roomContext.setBookingInfo(bookingUserId, bookingDate, bookingStartTime, bookingEndTime);
        }
    }
    
    private RoomState parseState(String stateName) {
        switch (stateName) {
            case "Available":
                return AvailableState.getInstance();
            case "Reserved":
                return ReservedState.getInstance();
            case "InUse":
                return InUseState.getInstance();
            case "Maintenance":
                return MaintenanceState.getInstance();
            case "NoShow":
                return NoShowState.getInstance();
            default:
                return AvailableState.getInstance();
        }
    }
    
    // Check if room can be booked (must be enabled AND in Available state)
    public boolean isBookable() {
        return status.equals("ENABLED") && roomContext.canBook();
    }
    
    // Admin actions
    public void enable() {
        this.status = "ENABLED";
        System.out.println("Room " + roomId + " has been enabled.");
    }
    
    public void disable() {
        this.status = "DISABLED";
        System.out.println("Room " + roomId + " has been disabled.");
    }
    
    // Delegate state actions to context
    public void book(UUID userId, String date, String startTime, String endTime) {
        if (!isBookable()) {
            System.out.println("Room " + roomId + " cannot be booked.");
            return;
        }
        roomContext.setBookingInfo(userId, date, startTime, endTime);
        roomContext.setState(ReservedState.getInstance());
        System.out.println("Room " + roomId + " booked successfully.");
    }
    
    public void checkIn() {
        roomContext.checkIn();
    }
    
    public void checkOut() {
        roomContext.checkOut();
    }
    
    public void cancelBooking() {
        roomContext.cancelBooking();
    }
    
    public void extendBooking(int additionalHours) {
        roomContext.extendBooking(additionalHours);
    }
    
    public void setMaintenance() {
        roomContext.setMaintenance();
    }
    
    public void clearMaintenance() {
        roomContext.clearMaintenance();
    }
    
    // Getters
    public UUID getRoomId() {
        return roomId;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getCondition() {
        return roomContext.getStateName();
    }
    
    public RoomContext getRoomContext() {
        return roomContext;
    }
    
    public String getLocation() {
        return buildingName + " - Room " + roomNumber;
    }
    
    // Booking info getters (delegated to context)
    public UUID getBookingUserId() {
        return roomContext.getBookingUserId();
    }
    
    public String getBookingDate() {
        return roomContext.getBookingDate();
    }
    
    public String getBookingStartTime() {
        return roomContext.getBookingStartTime();
    }
    
    public String getBookingEndTime() {
        return roomContext.getBookingEndTime();
    }
    
    public boolean hasActiveBooking() {
        return roomContext.hasActiveBooking();
    }
    
    @Override
    public String toString() {
        return "Room [" + getLocation() + ", Capacity: " + capacity + 
               ", Status: " + status + ", Condition: " + getCondition() + "]";
    }
}