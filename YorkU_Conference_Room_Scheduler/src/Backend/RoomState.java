package Backend;

public interface RoomState {
    
    String getStateName();
    
    // State behavior method - each state handles actions differently
    void handle(RoomContext context);
    
    // Default implementations - override in specific states where allowed
    default boolean canBook() {
        return false;
    }
    
    default void checkIn(RoomContext context) {
        System.out.println("Check-in not allowed in " + getStateName() + " state.");
    }
    
    default void checkOut(RoomContext context) {
        System.out.println("Check-out not allowed in " + getStateName() + " state.");
    }
    
    default void cancelBooking(RoomContext context) {
        System.out.println("Cancel booking not allowed in " + getStateName() + " state.");
    }
    
    default void extendBooking(RoomContext context, int additionalHours) {
        System.out.println("Extend booking not allowed in " + getStateName() + " state.");
    }
    
    default void setMaintenance(RoomContext context) {
        System.out.println("Set maintenance not allowed in " + getStateName() + " state.");
    }
    
    default void clearMaintenance(RoomContext context) {
        System.out.println("Clear maintenance not allowed in " + getStateName() + " state.");
    }
}