package Backend;

/**
 * Observer interface for booking changes
 */
public interface BookingObserver {
    /**
     * Called when a booking is updated
     * @param booking The updated booking
     */
    void onBookingUpdated(Booking booking);
    
    /**
     * Called when a booking is cancelled
     * @param bookingId The ID of the cancelled booking
     */
    void onBookingCancelled(String bookingId);
    
    /**
     * Called when a booking is created
     * @param booking The new booking
     */
    void onBookingCreated(Booking booking);
}

