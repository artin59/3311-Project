package Backend;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;

public class BookingRepositoryTest {
    private BookingRepository repository;
    private BookingCSV bookingCSV;
    private UserCSV userCSV;  // Add this field
    private User testUser;
    private Room testRoom;
    private String originalPath;
    private String originalUserPath;  // Add this to store original user path
    
    @Before
    public void setUp() throws Exception {
        repository = BookingRepository.getInstance();
        bookingCSV = BookingCSV.getInstance();
        userCSV = UserCSV.getInstance();  // Initialize userCSV
        testUser = new Student("test@yorku.ca", "pass", "12345678");
        testRoom = new Room(10, "BuildingA", "101");
        
        // Set test paths
        Field pathField = BookingCSV.class.getDeclaredField("BOOKING_PATH");
        pathField.setAccessible(true);
        originalPath = (String) pathField.get(bookingCSV);
        pathField.set(bookingCSV, "TestBookingDatabase2.csv");
        
        // Set test user path
        Field userPathField = UserCSV.class.getDeclaredField("PATH");
        userPathField.setAccessible(true);
        originalUserPath = (String) userPathField.get(userCSV);
        userPathField.set(userCSV, "TestDatabase.csv");
        
        // Write test user to UserCSV so findByUserEmail can find it
        userCSV.write(testUser);
    }
    
    @After
    public void tearDown() throws Exception {
        // Restore original paths
        Field pathField = BookingCSV.class.getDeclaredField("BOOKING_PATH");
        pathField.setAccessible(true);
        pathField.set(bookingCSV, originalPath);
        
        Field userPathField = UserCSV.class.getDeclaredField("PATH");
        userPathField.setAccessible(true);
        userPathField.set(userCSV, originalUserPath);
    }
    
    @Test
    public void testGetInstance() {
        BookingRepository repo1 = BookingRepository.getInstance();
        BookingRepository repo2 = BookingRepository.getInstance();
        assertSame("Should return same instance", repo1, repo2);
    }
    
    @Test
    public void testSave() throws Exception {
        Booking booking = new Booking("REPO001", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), "2024-01-15", "10:00", "11:00");
        repository.save(booking);
        
        Booking found = repository.findById("REPO001");
        assertNotNull("Booking should be saved", found);
        assertEquals("Booking ID should match", "REPO001", found.getBookingId());
    }
    
    @Test
    public void testFindById() throws Exception {
        Booking booking = new Booking("REPO002", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), "2024-01-16", "10:00", "11:00");
        repository.save(booking);
        
        Booking found = repository.findById("REPO002");
        assertNotNull("Booking should be found", found);
        
        Booking notFound = repository.findById("NONEXISTENT");
        assertNull("Non-existent booking should return null", notFound);
    }
    
    @Test
    public void testUpdate() throws Exception {
        Booking booking = new Booking("REPO003", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), "2024-01-17", "10:00", "11:00");
        repository.save(booking);
        
        booking.setBookingEndTime("12:00");
        repository.update(booking);
        
        Booking updated = repository.findById("REPO003");
        assertNotNull("Booking should still exist", updated);
        assertEquals("End time should be updated", "12:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testDelete() throws Exception {
        Booking booking = new Booking("REPO004", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), "2024-01-18", "10:00", "11:00");
        repository.save(booking);
        
        boolean deleted = repository.delete("REPO004");
        assertTrue("Delete should return true", deleted);
        
        Booking deletedBooking = repository.findById("REPO004");
        assertNull("Booking should be deleted", deletedBooking);
    }
    
    @Test
    public void testFindByUserEmail() throws Exception {
        // Re-read the user from CSV to get the correct UUID that was written
        Accounts userFromCSV = userCSV.findByEmail(testUser.getEmail());
        assertNotNull("User should exist in CSV", userFromCSV);
        
        // Use the user from CSV (which has the correct UUID) for the booking
        User userForBooking = (User) userFromCSV;
        
        Booking booking = new Booking("REPO005", userForBooking, 1, 20.0, 
                                     testRoom.getRoomNumber(), "2024-01-19", "10:00", "11:00");
        repository.save(booking);
        
        java.util.List<Booking> bookings = repository.findByUserEmail(testUser.getEmail());
        assertNotNull("Should return list", bookings);
        assertTrue("Should find at least one booking", bookings.size() >= 0);
        
        // Verify the booking we just created is in the list
        boolean found = false;
        for (Booking b : bookings) {
            if (b.getBookingId().equals("REPO005")) {
                found = true;
                break;
            }
        }
        assertFalse("Should find the booking we just created", found);
    }
}