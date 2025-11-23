package AppUI;

import Backend.Booking;
import Backend.BookingObserver;

/**
 * Observer for reserve room UI updates
 * Refreshes the time slot table when bookings change
 */
public class ReserveRoomObserver implements BookingObserver {
    private MainFrame mainFrame;
    
    public ReserveRoomObserver(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    @Override
    public void onBookingUpdated(Booking booking) {
        System.out.println("ReserveRoomObserver: Booking updated - " + booking.getBookingId());
        // Refresh time slot table if room/date is selected
        if (mainFrame != null) {
            mainFrame.refreshTimeSlotTableForSelectedRoom();
        }
    }
    
    @Override
    public void onBookingCancelled(String bookingId) {
        System.out.println("ReserveRoomObserver: Booking cancelled - " + bookingId);
        // Refresh time slot table if room/date is selected
        if (mainFrame != null) {
            mainFrame.refreshTimeSlotTableForSelectedRoom();
        }
    }
    
    @Override
    public void onBookingCreated(Booking booking) {
        System.out.println("ReserveRoomObserver: Booking created - " + booking.getBookingId());
        // Refresh time slot table if room/date is selected
        if (mainFrame != null) {
            mainFrame.refreshTimeSlotTableForSelectedRoom();
        }
    }
}

