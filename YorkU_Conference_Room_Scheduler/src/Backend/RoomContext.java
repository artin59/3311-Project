package Backend;

import java.util.UUID;

public class RoomContext {
    
    private UUID roomId;
    private RoomState currentState;
    
    // Booking info (null when no active booking)
    private String bookingId;
    private UUID bookingUserId;
    private String bookingStartTime;
    private String bookingEndTime;
    private String bookingDate;
    
    public RoomContext(UUID roomId) {
        this.roomId = roomId;
        this.currentState = AvailableState.getInstance();
    }
    
    public RoomContext(UUID roomId, RoomState initialState) {
        this.roomId = roomId;
        this.currentState = initialState;
    }
    
    // State pattern methods
    public void setState(RoomState state) {
        this.currentState = state;
        System.out.println("Room " + roomId + " state changed to: " + state.getStateName());
    }
    
    public RoomState getState() {
        return currentState;
    }
    
    public String getStateName() {
        return currentState.getStateName();
    }
    
    public void handle() {
        currentState.handle(this);
    }
    
    // Delegate methods to current state
    public boolean canBook() {
        return currentState.canBook();
    }
    
    public void checkIn() {
        currentState.checkIn(this);
    }
    
    public void checkOut() {
        currentState.checkOut(this);
    }
    
    public void cancelBooking() {
        currentState.cancelBooking(this);
    }
    
    public void extendBooking(int additionalHours) {
        currentState.extendBooking(this, additionalHours);
    }
    
    public void setMaintenance() {
        currentState.setMaintenance(this);
    }
    
    public void clearMaintenance() {
        currentState.clearMaintenance(this);
    }
    
    // Booking info methods
    public void setBookingInfo(String bookingId2, UUID userId, String date, String startTime, String endTime) {
        this.bookingId = bookingId2;  // Fixed: use bookingId2 parameter instead of bookingId field
        this.bookingUserId = userId;
        this.bookingDate = date;
        this.bookingStartTime = startTime;
        this.bookingEndTime = endTime;
    }
    
    public void clearBookingInfo() {
        this.bookingId = null;
        this.bookingUserId = null;
        this.bookingDate = null;
        this.bookingStartTime = null;
        this.bookingEndTime = null;
    }
    
    // Getters
    public UUID getRoomId() {
        return roomId;
    }
    
    public String getBookingId() {
        return bookingId;
    }
    
    public UUID getBookingUserId() {
        return bookingUserId;
    }
    
    public String getBookingStartTime() {
        return bookingStartTime;
    }
    
    public String getBookingEndTime() {
        return bookingEndTime;
    }
    
    public String getBookingDate() {
        return bookingDate;
    }
    
    public boolean hasActiveBooking() {
        return bookingUserId != null;
    }
}