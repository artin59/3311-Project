package Backend;

import java.util.UUID;

public class Room {
    
    private UUID roomId;
    private int capacity;
    private String buildingName;
    private String roomNumber;
    private String status; // ENABLED, DISABLED
    private RoomContext roomContext; // State pattern context
    
    private int currentOccupancy;
    
    
    public Room(int capacity, String buildingName, String roomNumber) {
        this.roomId = UUID.randomUUID();
        this.capacity = capacity;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.status = "ENABLED";
        this.roomContext = new RoomContext(roomId);
        this.currentOccupancy = 0;

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
        this.currentOccupancy = 0;

    }
    
    // Constructor with booking info (for loading from CSV)
    public Room(UUID roomId, int capacity, String buildingName, String roomNumber,
                String status, String stateName, String bookingId, UUID bookingUserId, 
                String bookingDate, String bookingStartTime, String bookingEndTime) {
        this(roomId, capacity, buildingName, roomNumber, status, stateName);
        this.currentOccupancy = 0;

        if (bookingUserId != null) {
            this.roomContext.setBookingInfo(bookingId, bookingUserId, bookingDate, bookingStartTime, bookingEndTime);
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
    public void book(String bookingId, UUID userId, String date, String startTime, String endTime) {
        // Check if room is enabled (but allow booking even if already reserved for different time)
        if (!status.equals("ENABLED")) {
            System.out.println("Room " + roomId + " is not enabled for booking.");
            return;
        }
        // Set booking info and state to Reserved for this booking
        // Multiple bookings can exist for the same room at different times
        roomContext.setBookingInfo(bookingId, userId, date, startTime, endTime);
        roomContext.setState(ReservedState.getInstance());
        System.out.println("Room " + roomId + " booked successfully for " + date + " " + startTime + "-" + endTime);
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
    public String getBookingId() {
        return roomContext.getBookingId();
    }
    
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
 // Check in people to the room
    public boolean checkInPeople(int numberOfPeople) {
        if (numberOfPeople <= 0) {
            System.out.println("Number of people must be positive.");
            return false;
        }
        
        if (currentOccupancy + numberOfPeople > capacity) {
            System.out.println("Cannot check in " + numberOfPeople + " people. Would exceed capacity of " 
                              + capacity + ". Current occupancy: " + currentOccupancy);
            return false;
        }
        
        currentOccupancy += numberOfPeople;
        System.out.println(numberOfPeople + " people checked in. Current occupancy: " 
                          + currentOccupancy + "/" + capacity);
        return true;
    }

    // Check out people from the room
    public void checkOutPeople(int numberOfPeople) {
        currentOccupancy -= numberOfPeople;
        if (currentOccupancy < 0) {
            currentOccupancy = 0;
        }
        System.out.println(numberOfPeople + " people checked out. Current occupancy: " 
                          + currentOccupancy + "/" + capacity);
    }

    // Reset occupancy (called when room becomes available)
    public void resetOccupancy() {
        currentOccupancy = 0;
        System.out.println("Room occupancy reset.");
    }

    // Getter for current occupancy
    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    // Check if room has space for more people
    public boolean hasCapacityFor(int numberOfPeople) {
        return (currentOccupancy + numberOfPeople) <= capacity;
    }

    public int getRemainingCapacity() {
        return capacity - currentOccupancy;
    }
    @Override
    public String toString() {
        return "Room [" + getLocation() + ", Capacity: " + capacity + 
               ", Status: " + status + ", Condition: " + getCondition() + "]";
    }
}