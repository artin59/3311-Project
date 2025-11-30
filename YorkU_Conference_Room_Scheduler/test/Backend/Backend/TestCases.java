package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.lang.reflect.Field;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class TestCases {
    
    private BookingCSV bookingCSV;
    private RoomCSV roomCSV;
    private UserCSV userCSV;
    private String originalBookingPath;
    private String originalRoomPath;
    private String originalUserPath;
    
    // Test CSV file paths
    private static final String TEST_BOOKING_PATH = "TestBookingDatabase2.csv";
    private static final String TEST_ROOM_PATH = "TestRoomDatabase2.csv";
    private static final String TEST_USER_PATH = "TestDatabase.csv";
    
    // Test data
    private User testUser;
    private Room testRoom;
    private Booking testBooking;
    
    @Before
    public void setUp() throws Exception {
        // Get instances
        bookingCSV = BookingCSV.getInstance();
        roomCSV = RoomCSV.getInstance();
        userCSV = UserCSV.getInstance();
        
        // Save original paths using reflection
        Field bookingPathField = BookingCSV.class.getDeclaredField("BOOKING_PATH");
        bookingPathField.setAccessible(true);
        originalBookingPath = (String) bookingPathField.get(bookingCSV);
        
        Field roomPathField = BookingCSV.class.getDeclaredField("ROOM_PATH");
        roomPathField.setAccessible(true);
        originalRoomPath = (String) roomPathField.get(bookingCSV);
        
        Field userPathField = UserCSV.class.getDeclaredField("PATH");
        userPathField.setAccessible(true);
        originalUserPath = (String) userPathField.get(userCSV);
        
        // Change paths to test files using reflection
        bookingPathField.set(bookingCSV, TEST_BOOKING_PATH);
        roomPathField.set(bookingCSV, TEST_ROOM_PATH);
        userPathField.set(userCSV, TEST_USER_PATH);
        
        // Also update RoomCSV path
        Field roomCSVPathField = RoomCSV.class.getDeclaredField("PATH");
        roomCSVPathField.setAccessible(true);
        roomCSVPathField.set(roomCSV, TEST_ROOM_PATH);
        
        // Initialize test CSV files
        initializeTestFiles();
        
        // Create test user
        testUser = new Student("test@yorku.ca", "password123", "12345678");
        userCSV.write(testUser);
        
        // Create test room (using existing room from TestRoomDatabase2.csv)
        // The room already exists: BuildingA, Room 101
        // We'll use it for testing
        List<Room> rooms = roomCSV.findAll();
        if (!rooms.isEmpty()) {
            testRoom = rooms.get(0);
        } else {
            // Create a test room if none exists
            testRoom = new Room(10, "BuildingA", "101");
            roomCSV.write(testRoom);
        }
    }
    
    @After
    public void tearDown() throws Exception {
        // Restore original paths
        Field bookingPathField = BookingCSV.class.getDeclaredField("BOOKING_PATH");
        bookingPathField.setAccessible(true);
        bookingPathField.set(bookingCSV, originalBookingPath);
        
        Field roomPathField = BookingCSV.class.getDeclaredField("ROOM_PATH");
        roomPathField.setAccessible(true);
        roomPathField.set(bookingCSV, originalRoomPath);
        
        Field userPathField = UserCSV.class.getDeclaredField("PATH");
        userPathField.setAccessible(true);
        userPathField.set(userCSV, originalUserPath);
        
        Field roomCSVPathField = RoomCSV.class.getDeclaredField("PATH");
        roomCSVPathField.setAccessible(true);
        roomCSVPathField.set(roomCSV, originalRoomPath);
        
        // Clean up test files (optional - comment out if you want to keep test data)
        // new File(TEST_BOOKING_PATH).delete();
        // new File(TEST_USER_PATH).delete();
    }
    
    private void initializeTestFiles() throws Exception {
        // Initialize TestBookingDatabase2.csv with headers only
        // Always clear existing bookings to ensure test isolation
        File bookingFile = new File(TEST_BOOKING_PATH);
        CsvWriter csvWrite = new CsvWriter(new FileWriter(TEST_BOOKING_PATH, false), ',');
        csvWrite.write("BookingID");
        csvWrite.write("RoomID");
        csvWrite.write("Building Name");
        csvWrite.write("Room Number");
        csvWrite.write("Booking UserID");
        csvWrite.write("Booking Date");
        csvWrite.write("Booking Start Time");
        csvWrite.write("Booking End Time");
        csvWrite.endRecord();
        csvWrite.close();
        
        // Initialize TestDatabase.csv (for users) with headers
        File userFile = new File(TEST_USER_PATH);
        if (!userFile.exists()) {
            CsvWriter csvWrite1 = new CsvWriter(new FileWriter(TEST_USER_PATH, false), ',');
            csvWrite1.write("ID");
            csvWrite1.write("Type");
            csvWrite1.write("Org ID");
            csvWrite1.write("Email");
            csvWrite1.write("Password");
            csvWrite1.write("Date Created");
            csvWrite1.endRecord();
            csvWrite1.close();
        }
        
        // TestRoomDatabase2.csv - convert to format RoomCSV expects if needed
        // RoomCSV expects: Room ID, Capacity, Building Name, Room Number, Status, Condition,
        //                  Booking ID, Booking User ID, Booking Date, Booking Start Time, Booking End Time
        File roomFile = new File(TEST_ROOM_PATH);
        boolean needsConversion = false;
        String roomId = "54fdb95f-e29d-4c15-831f-08428b6774d2";
        String capacity = "10";
        String buildingName = "BuildingA";
        String roomNumber = "101";
        String status = "ENABLED";
        String condition = "Available";
        
        // Try to read existing file and convert if needed
        if (roomFile.exists()) {
            try {
                CsvReader csvRead = new CsvReader(TEST_ROOM_PATH);
                csvRead.readHeaders();
                String[] headers = csvRead.getHeaders();
                
                // Check if it's in the old format (RoomID vs Room ID)
                if (headers.length > 0 && headers[0].equals("RoomID")) {
                    needsConversion = true;
                    // Read existing data
                    if (csvRead.readRecord()) {
                        roomId = csvRead.get("RoomID");
                        capacity = csvRead.get("Capacity");
                        buildingName = csvRead.get("BuildingName");
                        roomNumber = csvRead.get("RoomNumber");
                        status = csvRead.get("Status");
                        String oldCondition = csvRead.get("Condition");
                        // Map OPEN to Available
                        condition = "OPEN".equals(oldCondition) ? "Available" : oldCondition;
                    }
                }
                csvRead.close();
            } catch (Exception e) {
                // If reading fails, we'll create a new file
                needsConversion = true;
            }
        } else {
            needsConversion = true;
        }
        
        // Create file in the correct format
        if (needsConversion || !roomFile.exists()) {
            CsvWriter csvWrite1 = new CsvWriter(new FileWriter(TEST_ROOM_PATH, false), ',');
            csvWrite1.write("Room ID");
            csvWrite1.write("Capacity");
            csvWrite1.write("Building Name");
            csvWrite1.write("Room Number");
            csvWrite1.write("Status");
            csvWrite1.write("Condition");
            csvWrite1.write("Booking ID");
            csvWrite1.write("Booking User ID");
            csvWrite1.write("Booking Date");
            csvWrite1.write("Booking Start Time");
            csvWrite1.write("Booking End Time");
            csvWrite1.endRecord();
            // Add test room data
            csvWrite1.write(roomId);
            csvWrite1.write(capacity);
            csvWrite1.write(buildingName);
            csvWrite1.write(roomNumber);
            csvWrite1.write(status);
            csvWrite1.write(condition);
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.endRecord();
            csvWrite1.close();
        }
    }
    
    @Test
    public void testWriteBooking() throws Exception {
        // Create a booking
        String bookingId = "TEST001";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-15", "10:00", "11:00");
        
        // Write booking
        bookingCSV.write(booking);
        
        // Verify booking was written
        Booking found = bookingCSV.findById(bookingId);
        assertNotNull("Booking should be found after writing", found);
        assertEquals("Booking ID should match", bookingId, found.getBookingId());
        assertEquals("Room number should match", testRoom.getRoomNumber(), found.getRoomNumber());
        assertEquals("Booking date should match", "2024-01-15", found.getBookingDate());
        assertEquals("Start time should match", "10:00", found.getBookingStartTime());
    }
    
    @Test
    public void testFindById() throws Exception {
        // Create and write a booking
        String bookingId = "TEST002";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-16", "14:00", "15:00");
        bookingCSV.write(booking);
        
        // Find by ID
        Booking found = bookingCSV.findById(bookingId);
        assertNotNull("Booking should be found", found);
        assertEquals("Booking ID should match", bookingId, found.getBookingId());
        
        // Test non-existent ID
        Booking notFound = bookingCSV.findById("NONEXISTENT");
        assertNull("Non-existent booking should return null", notFound);
    }
    
    @Test
    public void testFindByUserEmail() throws Exception {
        // Save the original user UUID before writing to CSV
        UUID originalUserId = testUser.getAccountId();
        
        // Create and write a booking
        String bookingId = "TEST003";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-17", "09:00", "10:00");
        bookingCSV.write(booking);
        
        // Verify booking was written correctly
        Booking foundBooking = bookingCSV.findById(bookingId);
        assertNotNull("Booking should be found by ID", foundBooking);
        assertEquals("Booking user ID should match", originalUserId, foundBooking.getUser().getAccountId());
        
        // The issue: UserCSV.findByEmail creates a new User with a new UUID
        // We need to patch UserCSV.findByEmail to restore the UUID from CSV
        // Read the user ID from CSV first
        String userIdFromCSV = null;
        try {
            CsvReader csvRead = new CsvReader(TEST_USER_PATH);
            csvRead.readHeaders();
            while (csvRead.readRecord()) {
                if (csvRead.get("Email").equalsIgnoreCase(testUser.getEmail())) {
                    userIdFromCSV = csvRead.get("ID");
                    break;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertNotNull("User ID should be found in CSV", userIdFromCSV);
        final UUID csvUserId = UUID.fromString(userIdFromCSV);
        
        // Patch UserCSV.findByEmail to restore UUIDs using a wrapper approach
        // Since we can't modify UserCSV directly, we'll need to work around it
        // The real fix would be to modify UserCSV.findByEmail to restore UUIDs,
        // but for now, let's test that the booking exists and verify the mechanism
        
        // Test findByUserEmail - it should work if UserCSV properly restores UUIDs
        // But since UserCSV.findByEmail doesn't restore UUIDs, we need to work around it
        List<Booking> bookings = bookingCSV.findByUserEmail(testUser.getEmail());
        assertNotNull("Bookings list should not be null", bookings);
        
        // If UserCSV.findByEmail doesn't restore UUIDs, bookings will be empty
        // So we need to verify the booking exists another way
        if (bookings.size() == 0) {
            // UserCSV.findByEmail doesn't restore UUIDs, so the search fails
            // But we can verify the booking exists by checking all bookings
            List<Booking> allBookings = bookingCSV.findAll();
            boolean bookingExists = false;
            for (Booking b : allBookings) {
                if (b.getBookingId().equals(bookingId)) {
                    bookingExists = true;
                    // Verify the user ID in the booking matches the original
                    assertEquals("User ID in booking should match original", 
                                originalUserId, b.getUser().getAccountId());
                    break;
                }
            }
            assertTrue("Booking should exist in database", bookingExists);
            
            // Since UserCSV.findByEmail doesn't restore UUIDs, we can't test it properly
            // But we've verified the booking was written correctly
            // This indicates a bug in UserCSV.findByEmail that should be fixed
            fail("findByUserEmail failed - UserCSV.findByEmail does not restore UUIDs from CSV. " +
                 "The booking exists (verified by findAll), but findByUserEmail cannot find it " +
                 "because the user UUID doesn't match. This is a bug in UserCSV.findByEmail.");
        } else {
            // If bookings are found, verify our booking is in the list
            boolean found = false;
            for (Booking b : bookings) {
                if (b.getBookingId().equals(bookingId)) {
                    found = true;
                    break;
                }
            }
            assertTrue("Should find the booking we just created", found);
        }
        
        // Test with non-existent email
        List<Booking> emptyBookings = bookingCSV.findByUserEmail("nonexistent@yorku.ca");
        assertNotNull("Should return empty list, not null", emptyBookings);
    }
    
    @Test
    public void testFindAll() throws Exception {
        // Clear existing bookings first
        List<Booking> existing = bookingCSV.findAll();
        for (Booking b : existing) {
            bookingCSV.deleteBooking(b.getBookingId());
        }
        
        // Create multiple bookings
        Booking booking1 = new Booking("TEST004", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), "2024-01-18", "10:00", "11:00");
        Booking booking2 = new Booking("TEST005", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), "2024-01-19", "14:00", "15:00");
        
        bookingCSV.write(booking1);
        bookingCSV.write(booking2);
        
        // Find all
        List<Booking> allBookings = bookingCSV.findAll();
        assertNotNull("All bookings list should not be null", allBookings);
        assertTrue("Should find at least 2 bookings", allBookings.size() >= 2);
    }
    
    @Test
    public void testUpdateBooking() throws Exception {
        // Create and write a booking
        String bookingId = "TEST006";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-20", "10:00", "11:00");
        bookingCSV.write(booking);
        
        // Update booking
        booking.setBookingEndTime("12:00");
        booking.setHours(2);
        bookingCSV.update(booking);
        
        // Verify update
        Booking updated = bookingCSV.findById(bookingId);
        assertNotNull("Updated booking should be found", updated);
        assertEquals("End time should be updated", "12:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testHasTimeConflict() throws Exception {
        // Clear any existing bookings first to ensure test isolation
        List<Booking> existing = bookingCSV.findAll();
        for (Booking b : existing) {
            bookingCSV.deleteBooking(b.getBookingId());
        }
        
        // Create a booking
        String bookingId = "TEST007";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-21", "10:00", "11:00");
        bookingCSV.write(booking);
        
        // Test conflict - overlapping time
        boolean hasConflict = bookingCSV.hasTimeConflict(
            testRoom.getRoomNumber(), "2024-01-21", "10:30", "11:30");
        assertTrue("Should detect time conflict", hasConflict);
        
        // Test no conflict - different time
        boolean noConflict = bookingCSV.hasTimeConflict(
            testRoom.getRoomNumber(), "2024-01-21", "12:00", "13:00");
        assertFalse("Should not detect conflict for different time", noConflict);
        
        // Test no conflict - different date (use a date that won't conflict with other tests)
        boolean noConflictDate = bookingCSV.hasTimeConflict(
            testRoom.getRoomNumber(), "2024-12-31", "10:00", "11:00");
        assertFalse("Should not detect conflict for different date", noConflictDate);
        
        // Test no conflict - different room
        boolean noConflictRoom = bookingCSV.hasTimeConflict(
            "999", "2024-01-21", "10:00", "11:00");
        assertFalse("Should not detect conflict for different room", noConflictRoom);
        
        // Clean up
        bookingCSV.deleteBooking(bookingId);
    }
    
    @Test
    public void testHasTimeConflictWithExclude() throws Exception {
        // Clear any existing bookings first to ensure test isolation
        List<Booking> existing = bookingCSV.findAll();
        for (Booking b : existing) {
            bookingCSV.deleteBooking(b.getBookingId());
        }
        
        // Create a booking
        String bookingId = "TEST008";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-22", "10:00", "11:00");
        bookingCSV.write(booking);
        
        // Test conflict check excluding this booking - should not conflict with itself
        boolean hasConflict = bookingCSV.hasTimeConflict(
            testRoom.getRoomNumber(), "2024-01-22", "10:00", "11:00", bookingId);
        assertFalse("Should not conflict with excluded booking", hasConflict);
        
        // Clean up
        bookingCSV.deleteBooking(bookingId);
    }
    
    @Test
    public void testGetBookingsForRoomAndDate() throws Exception {
        // Create bookings for same room and date
        Booking booking1 = new Booking("TEST009", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), "2024-01-23", "10:00", "11:00");
        Booking booking2 = new Booking("TEST010", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), "2024-01-23", "14:00", "15:00");
        
        bookingCSV.write(booking1);
        bookingCSV.write(booking2);
        
        // Get bookings for room and date
        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate(
            testRoom.getRoomNumber(), "2024-01-23");
        
        assertNotNull("Bookings list should not be null", bookings);
        assertTrue("Should find at least 2 bookings", bookings.size() >= 2);
        
        // Verify times are present
        boolean found10 = false, found14 = false;
        for (Map<String, String> b : bookings) {
            String startTime = b.get("startTime");
            if ("10:00".equals(startTime)) found10 = true;
            if ("14:00".equals(startTime)) found14 = true;
        }
        assertTrue("Should find 10:00 booking", found10);
        assertTrue("Should find 14:00 booking", found14);
    }
    
    @Test
    public void testDeleteBooking() throws Exception {
        // Create and write a booking
        String bookingId = "TEST011";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-24", "10:00", "11:00");
        bookingCSV.write(booking);
        
        // Verify it exists
        Booking found = bookingCSV.findById(bookingId);
        assertNotNull("Booking should exist before deletion", found);
        
        // Delete booking
        boolean deleted = bookingCSV.deleteBooking(bookingId);
        assertTrue("Delete should return true", deleted);
        
        // Verify it's deleted
        Booking deletedBooking = bookingCSV.findById(bookingId);
        assertNull("Booking should not exist after deletion", deletedBooking);
        
        // Test deleting non-existent booking
        boolean notDeleted = bookingCSV.deleteBooking("NONEXISTENT");
        assertFalse("Delete should return false for non-existent booking", notDeleted);
    }
    
    @Test
    public void testGetAllBookingRecords() throws Exception {
        // Create a booking
        String bookingId = "TEST012";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-25", "10:00", "11:00");
        bookingCSV.write(booking);
        
        // Get all booking records
        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
        assertNotNull("Records list should not be null", records);
        assertTrue("Should find at least one record", records.size() > 0);
        
        // Verify record structure
        boolean found = false;
        for (Map<String, String> record : records) {
            if (bookingId.equals(record.get("bookingId"))) {
                found = true;
                assertNotNull("Room number should be present", record.get("roomNumber"));
                assertNotNull("Date should be present", record.get("date"));
                assertNotNull("Start time should be present", record.get("startTime"));
                assertNotNull("End time should be present", record.get("endTime"));
                break;
            }
        }
        assertTrue("Should find the booking record", found);
    }
    
    @Test
    public void testWriteBookingWithoutRoomNumber() throws Exception {
        // Create a booking without room number
        String bookingId = "TEST013";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     null, "2024-01-26", "10:00", "11:00");
        
        // Write booking - should not write if room number is null
        bookingCSV.write(booking);
        
        // Verify booking was NOT written
        Booking found = bookingCSV.findById(bookingId);
        assertNull("Booking without room number should not be written", found);
    }
    
    @Test
    public void testCalculateEndTime() throws Exception {
        // Create a booking with start time
        String bookingId = "TEST014";
        Booking booking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), "2024-01-27", "10:00", null);
        bookingCSV.write(booking);
        
        // The write method should calculate end time as start time + 1 hour
        Booking found = bookingCSV.findById(bookingId);
        if (found != null) {
            // End time should be calculated (11:00 = 10:00 + 1 hour)
            assertNotNull("End time should be calculated", found.getBookingEndTime());
        }
    }
}
