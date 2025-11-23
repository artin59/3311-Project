package Backend;

import java.util.List;

/**
 * Repository interface for booking persistence
 * Acts as an abstraction over BookingCSV
 */
public class BookingRepository {
    private static BookingRepository instance;
    private BookingCSV bookingCSV;
    
    private BookingRepository() {
        this.bookingCSV = BookingCSV.getInstance();
    }
    
    public static BookingRepository getInstance() {
        if (instance == null) {
            instance = new BookingRepository();
        }
        return instance;
    }
    
    /**
     * Save a booking
     * @param booking The booking to save
     */
    public void save(Booking booking) {
        bookingCSV.write(booking);
    }
    
    /**
     * Update a booking
     * @param booking The updated booking
     */
    public void update(Booking booking) {
        bookingCSV.update(booking);
    }
    
    /**
     * Delete a booking
     * @param bookingId The ID of the booking to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(String bookingId) {
        return bookingCSV.deleteBooking(bookingId);
    }
    
    /**
     * Find a booking by ID
     * @param bookingId The booking ID
     * @return The booking, or null if not found
     */
    public Booking findById(String bookingId) {
        return bookingCSV.findById(bookingId);
    }
    
    /**
     * Find bookings by user email
     * @param email The user email
     * @return List of bookings
     */
    public List<Booking> findByUserEmail(String email) {
        return bookingCSV.findByUserEmail(email);
    }
}

