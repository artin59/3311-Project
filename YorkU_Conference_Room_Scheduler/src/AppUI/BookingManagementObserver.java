package AppUI;

import Backend.Booking;
import Backend.BookingObserver;

/**
 * Observer for booking management UI updates
 * Refreshes the booking table when bookings change
 */
public class BookingManagementObserver implements BookingObserver {
    private MainFrame mainFrame;
    
    public BookingManagementObserver(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    @Override
    public void onBookingUpdated(Booking booking) {
        System.out.println("BookingManagementObserver: Booking updated - " + booking.getBookingId());
        System.out.println("BookingManagementObserver: Booking end time - " + booking.getBookingEndTime());
        // Refresh the booking table
        if (mainFrame != null) {
            System.out.println("BookingManagementObserver: Calling refreshBookingTable()");
            mainFrame.refreshBookingTable();
            System.out.println("BookingManagementObserver: refreshBookingTable() completed");
        } else {
            System.err.println("BookingManagementObserver: mainFrame is null!");
        }
    }
    
    @Override
    public void onBookingCancelled(String bookingId) {
        System.out.println("BookingManagementObserver: Booking cancelled - " + bookingId);
        // Refresh the booking table
        if (mainFrame != null) {
            mainFrame.refreshBookingTable();
        }
    }
    
    @Override
    public void onBookingCreated(Booking booking) {
        System.out.println("BookingManagementObserver: Booking created - " + booking.getBookingId());
        // Refresh the booking table
        if (mainFrame != null) {
            mainFrame.refreshBookingTable();
        }
    }
}

