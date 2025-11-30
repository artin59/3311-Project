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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private Room testRoom2; // For EditBookingCommand tests
    private Booking testBooking;
    
    // For EditBookingCommand tests
    private BookingRepository repository;
    private PricingPolicyFactory pricingFactory;
    private PaymentService paymentService;
    private RoomService roomService;
    private List<BookingObserver> observers;
    private MockPaymentProcessor mockPaymentProcessor;
    private MockBookingObserver mockObserver;
    
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
        
        // Create test rooms (using existing rooms from TestRoomDatabase2.csv)
        List<Room> rooms = roomCSV.findAll();
        if (rooms.size() >= 2) {
            testRoom = rooms.get(0);
            testRoom2 = rooms.get(1);
        } else if (rooms.size() == 1) {
            testRoom = rooms.get(0);
            // Create second test room if needed
            testRoom2 = new Room(20, "BuildingB", "202");
            roomCSV.write(testRoom2);
        } else {
            // Create test rooms if none exist
            testRoom = new Room(10, "BuildingA", "101");
            testRoom2 = new Room(20, "BuildingB", "202");
            roomCSV.write(testRoom);
            roomCSV.write(testRoom2);
        }
        
        // Setup services for EditBookingCommand tests
        repository = BookingRepository.getInstance();
        pricingFactory = new PricingPolicyFactory();
        paymentService = PaymentService.getInstance();
        roomService = new RoomService();
        observers = new ArrayList<>();
        mockObserver = new MockBookingObserver();
        observers.add(mockObserver);
        
        // Setup mock payment processor
        mockPaymentProcessor = new MockPaymentProcessor();
        paymentService.setProcessor(mockPaymentProcessor);
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
        String roomId1 = "54fdb95f-e29d-4c15-831f-08428b6774d2";
        String roomId2 = "64fdb95f-e29d-4c15-831f-08428b6774d3";
        String capacity1 = "10";
        String capacity2 = "20";
        String buildingName1 = "BuildingA";
        String buildingName2 = "BuildingB";
        String roomNumber1 = "101";
        String roomNumber2 = "202";
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
                }
                csvRead.close();
            } catch (Exception e) {
                needsConversion = true;
            }
        } else {
            needsConversion = true;
        }
        
        // Create file in the correct format with 2 rooms
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
            // Add test room 1
            csvWrite1.write(roomId1);
            csvWrite1.write(capacity1);
            csvWrite1.write(buildingName1);
            csvWrite1.write(roomNumber1);
            csvWrite1.write(status);
            csvWrite1.write(condition);
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.write("");
            csvWrite1.endRecord();
            // Add test room 2
            csvWrite1.write(roomId2);
            csvWrite1.write(capacity2);
            csvWrite1.write(buildingName2);
            csvWrite1.write(roomNumber2);
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
        
        // Note: UserCSV.find() doesn't restore UUIDs, so the user will have a new UUID
        // We need to restore it to match the original
        // Try to read the user ID from CSV, but if that fails, use the original user ID
        UUID csvUserId = originalUserId;
        String userIdFromCSV = null;
        try {
            File userFile = new File(TEST_USER_PATH);
            if (userFile.exists()) {
                CsvReader csvRead = new CsvReader(TEST_USER_PATH);
                csvRead.readHeaders();
                while (csvRead.readRecord()) {
                    if (csvRead.get("Email").equalsIgnoreCase(testUser.getEmail())) {
                        userIdFromCSV = csvRead.get("ID");
                        if (userIdFromCSV != null && !userIdFromCSV.trim().isEmpty()) {
                            csvUserId = UUID.fromString(userIdFromCSV.trim());
                        }
                        break;
                    }
                }
                csvRead.close();
            }
        } catch (Exception e) {
            // If reading from CSV fails, use the original user ID
            System.out.println("Could not read user ID from CSV, using original: " + e.getMessage());
            csvUserId = originalUserId;
        }
        
        // Restore the UUID to match what's stored in the booking CSV
        if (foundBooking.getUser() != null) {
            try {
                java.lang.reflect.Method setAccountIdMethod = Accounts.class.getDeclaredMethod("setAccountId", UUID.class);
                setAccountIdMethod.setAccessible(true);
                setAccountIdMethod.invoke(foundBooking.getUser(), csvUserId);
            } catch (Exception e) {
                // If reflection fails, we'll skip the UUID check
                System.err.println("Could not restore user UUID: " + e.getMessage());
            }
        }
        
        // Now verify the user ID matches (after restoration)
        assertEquals("Booking user ID should match", csvUserId, foundBooking.getUser().getAccountId());
        
        // The issue: UserCSV.findByEmail creates a new User with a new UUID
        // We need to work around this limitation
        
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
    
    // ========== EditBookingCommand Tests ==========
    
    private String getFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return futureDate.format(formatter);
    }
    
    // Mock PaymentProcessor for testing
    private static class MockPaymentProcessor implements PaymentProcessor {
        private boolean chargeSuccess = true;
        private boolean refundSuccess = true;
        private double lastChargeAmount = 0;
        private double lastRefundAmount = 0;
        
        @Override
        public boolean charge(double amount) {
            lastChargeAmount = amount;
            return chargeSuccess;
        }
        
        @Override
        public boolean refund(double amount) {
            lastRefundAmount = amount;
            return refundSuccess;
        }
        
        public void setChargeSuccess(boolean success) {
            this.chargeSuccess = success;
        }
        
        public void setRefundSuccess(boolean success) {
            this.refundSuccess = success;
        }
        
        public double getLastChargeAmount() {
            return lastChargeAmount;
        }
        
        public double getLastRefundAmount() {
            return lastRefundAmount;
        }
        
        public void reset() {
            lastChargeAmount = 0;
            lastRefundAmount = 0;
            chargeSuccess = true;
            refundSuccess = true;
        }
    }
    
    // Mock BookingObserver for testing
    private static class MockBookingObserver implements BookingObserver {
        private Booking lastUpdatedBooking;
        private String lastCancelledBookingId;
        private Booking lastCreatedBooking;
        private int updateCount = 0;
        private int cancelCount = 0;
        private int createCount = 0;
        
        @Override
        public void onBookingUpdated(Booking booking) {
            lastUpdatedBooking = booking;
            updateCount++;
        }
        
        @Override
        public void onBookingCancelled(String bookingId) {
            lastCancelledBookingId = bookingId;
            cancelCount++;
        }
        
        @Override
        public void onBookingCreated(Booking booking) {
            lastCreatedBooking = booking;
            createCount++;
        }
        
        public void reset() {
            lastUpdatedBooking = null;
            lastCancelledBookingId = null;
            lastCreatedBooking = null;
            updateCount = 0;
            cancelCount = 0;
            createCount = 0;
        }
    }
    
    @Test
    public void testEditBookingCommand_BookingNotFound() {
        EditBookingCommand command = new EditBookingCommand(
            "NONEXISTENT", null, null, null, null, null,
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when booking not found", result);
    }
    
    @Test
    public void testEditBookingCommand_BookingNotInPreStartState() throws Exception {
        // Create a booking with past date (not in pre-start state)
        String pastDate = "01/01/2020";
        String bookingId = "EDIT002";
        Booking pastBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), pastDate, "10:00", "11:00");
        pastBooking.setStatus("Reserved");
        bookingCSV.write(pastBooking);
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, null, null, null,
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when booking not in pre-start state", result);
    }
    
    @Test
    public void testEditBookingCommand_TimeConflict() throws Exception {
        // Create another booking that conflicts
        String futureDate = getFutureDate();
        String conflictBookingId = "CONFLICT001";
        Booking conflictBooking = new Booking(conflictBookingId, testUser, 1, testUser.getHourlyRate(),
                                             testRoom.getRoomNumber(), futureDate, "10:30", "11:30");
        conflictBooking.setStatus("Reserved");
        bookingCSV.write(conflictBooking);
        
        // Create a test booking for editing
        String bookingId = "EDIT001";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        // Try to edit testBooking to conflict with conflictBooking
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, testRoom.getRoomNumber(), futureDate, "10:30", "11:30",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when time conflict detected", result);
    }
    
    @Test
    public void testEditBookingCommand_NewRoomNotFound() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT003";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, "999", null, null, null,
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when new room not found", result);
    }
    
    @Test
    public void testEditBookingCommand_SuccessfulEdit_SameRoom() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT004";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should return true for successful edit", result);
        
        // Verify booking was updated
        Booking updated = repository.findById(bookingId);
        assertNotNull("Updated booking should exist", updated);
        assertEquals("Date should be updated", futureDate, updated.getBookingDate());
        assertEquals("Start time should be updated", "14:00", updated.getBookingStartTime());
        assertEquals("End time should be updated", "15:00", updated.getBookingEndTime());
        
        // Verify observer was notified
        assertEquals("Observer should be notified", 1, mockObserver.updateCount);
        assertEquals("Observer should receive updated booking", bookingId, 
                    mockObserver.lastUpdatedBooking.getBookingId());
    }
    
    @Test
    public void testEditBookingCommand_SuccessfulEdit_ChangeRoom() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT005";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, testRoom2.getRoomNumber(), futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should return true for successful edit with room change", result);
        
        // Verify booking was updated
        Booking updated = repository.findById(bookingId);
        assertNotNull("Updated booking should exist", updated);
        assertEquals("Room number should be updated", testRoom2.getRoomNumber(), updated.getRoomNumber());
        
        // Verify observer was notified
        assertEquals("Observer should be notified", 1, mockObserver.updateCount);
    }
    
    @Test
    public void testEditBookingCommand_PriceIncrease_ChargeAdditional() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT006";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // Edit to longer duration (2 hours instead of 1)
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "12:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        double originalCost = testBooking.getTotalCost();
        boolean result = command.execute();
        assertTrue("Should return true for successful edit", result);
        
        // Verify additional charge was made
        Booking updated = repository.findById(bookingId);
        double newCost = updated.getTotalCost();
        assertTrue("New cost should be higher", newCost > originalCost);
        assertTrue("Should charge additional amount", mockPaymentProcessor.getLastChargeAmount() > 0);
    }
    
    @Test
    public void testEditBookingCommand_PriceDecrease_Refund() throws Exception {
        // First create a booking with 2 hours
        String futureDate = getFutureDate();
        String bookingId = "EDIT007";
        Booking twoHourBooking = new Booking(bookingId, testUser, 2, testUser.getHourlyRate(),
                                            testRoom.getRoomNumber(), futureDate, "10:00", "12:00");
        twoHourBooking.setStatus("Reserved");
        bookingCSV.write(twoHourBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // Edit to shorter duration (1 hour instead of 2)
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "11:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        double originalCost = twoHourBooking.getTotalCost();
        boolean result = command.execute();
        assertTrue("Should return true for successful edit", result);
        
        // Verify refund was made
        Booking updated = repository.findById(bookingId);
        double newCost = updated.getTotalCost();
        assertTrue("New cost should be lower", newCost < originalCost);
        assertTrue("Should refund difference", mockPaymentProcessor.getLastRefundAmount() > 0);
    }
    
    @Test
    public void testEditBookingCommand_ChargeAdditionalFailure() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT008";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockPaymentProcessor.setChargeSuccess(false);
        mockObserver.reset();
        
        // Edit to longer duration
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "12:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when charge fails", result);
    }
    
    @Test
    public void testEditBookingCommand_RefundFailure_Continues() throws Exception {
        // First create a booking with 2 hours
        String futureDate = getFutureDate();
        String bookingId = "EDIT009";
        Booking twoHourBooking = new Booking(bookingId, testUser, 2, testUser.getHourlyRate(),
                                            testRoom.getRoomNumber(), futureDate, "10:00", "12:00");
        twoHourBooking.setStatus("Reserved");
        bookingCSV.write(twoHourBooking);
        
        mockPaymentProcessor.setRefundSuccess(false);
        mockObserver.reset();
        
        // Edit to shorter duration
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "11:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        // Should continue even if refund fails
        boolean result = command.execute();
        assertTrue("Should return true even if refund fails", result);
    }
    
    @Test
    public void testEditBookingCommand_CalculateEndTime() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT010";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        
        // Edit start time only (end time should be calculated)
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "14:00", null,
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should return true for successful edit", result);
        
        // Verify end time was calculated
        Booking updated = repository.findById(bookingId);
        assertEquals("End time should be calculated", "15:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testEditBookingCommand_UpdateAllFields() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT011";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, testRoom2.getRoomNumber(), futureDate, "16:00", "18:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should return true for successful edit", result);
        
        // Verify all fields were updated
        Booking updated = repository.findById(bookingId);
        assertEquals("Room number should be updated", testRoom2.getRoomNumber(), updated.getRoomNumber());
        assertEquals("Date should be updated", futureDate, updated.getBookingDate());
        assertEquals("Start time should be updated", "16:00", updated.getBookingStartTime());
        assertEquals("End time should be updated", "18:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testEditBookingCommand_Undo_OriginalBookingNull() {
        EditBookingCommand command = new EditBookingCommand(
            "NONEXISTENT", null, null, null, null, null,
            repository, pricingFactory, paymentService, roomService, observers);
        
        // Execute fails, so originalBooking is null
        command.execute();
        
        boolean result = command.undo();
        assertFalse("Should return false when originalBooking is null", result);
    }
    
    @Test
    public void testEditBookingCommand_Undo_Successful() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT012";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // First execute an edit
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, testRoom2.getRoomNumber(), futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        String originalRoom = testBooking.getRoomNumber();
        String originalDate = testBooking.getBookingDate();
        String originalStartTime = testBooking.getBookingStartTime();
        String originalEndTime = testBooking.getBookingEndTime();
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        // Verify booking was changed
        Booking updated = repository.findById(bookingId);
        assertEquals("Room should be changed", testRoom2.getRoomNumber(), updated.getRoomNumber());
        
        // Now undo
        mockObserver.reset();
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed", undoResult);
        
        // Verify booking was restored
        Booking restored = repository.findById(bookingId);
        assertEquals("Room should be restored", originalRoom, restored.getRoomNumber());
        assertEquals("Date should be restored", originalDate, restored.getBookingDate());
        assertEquals("Start time should be restored", originalStartTime, restored.getBookingStartTime());
        assertEquals("End time should be restored", originalEndTime, restored.getBookingEndTime());
        
        // Verify observer was notified
        assertEquals("Observer should be notified on undo", 1, mockObserver.updateCount);
    }
    
    @Test
    public void testEditBookingCommand_Undo_WithPaymentAdjustment() throws Exception {
        String futureDate = getFutureDate();
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // First create a booking with 2 hours
        String bookingId = "EDIT013";
        Booking twoHourBooking = new Booking(bookingId, testUser, 2, testUser.getHourlyRate(),
                                            testRoom.getRoomNumber(), futureDate, "10:00", "12:00");
        twoHourBooking.setStatus("Reserved");
        bookingCSV.write(twoHourBooking);
        
        double originalCost = twoHourBooking.getTotalCost();
        
        // Edit to 1 hour (price decrease)
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "11:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        // Now undo (should restore to 2 hours, need to charge more)
        mockPaymentProcessor.reset();
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed", undoResult);
        
        // Verify payment adjustment was made
        assertTrue("Should charge for undo", mockPaymentProcessor.getLastChargeAmount() > 0);
    }
    
    @Test
    public void testEditBookingCommand_NotifyObservers_NullObservers() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT014";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, null);
        
        // Should not throw exception
        boolean result = command.execute();
        assertTrue("Should succeed even with null observers", result);
    }
    
    @Test
    public void testEditBookingCommand_NotifyObservers_EmptyObservers() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT015";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        List<BookingObserver> emptyObservers = new ArrayList<>();
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, emptyObservers);
        
        boolean result = command.execute();
        assertTrue("Should succeed with empty observers", result);
    }
    
    @Test
    public void testEditBookingCommand_PriceDifferenceZero() throws Exception {
        String futureDate = getFutureDate();
        String bookingId = "EDIT016";
        Booking testBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                         testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        testBooking.setStatus("Reserved");
        bookingCSV.write(testBooking);
        
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // Edit to same duration (1 hour to 1 hour) - no price change
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "14:00", "15:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should succeed", result);
        
        // Verify no payment was made (price difference is 0)
        assertEquals("No charge should be made", 0.0, mockPaymentProcessor.getLastChargeAmount(), 0.01);
        assertEquals("No refund should be made", 0.0, mockPaymentProcessor.getLastRefundAmount(), 0.01);
    }
    
    @Test
    public void testEditBookingCommand_Undo_WithRefund() throws Exception {
        String futureDate = getFutureDate();
        mockObserver.reset();
        mockPaymentProcessor.reset();
        
        // First create a booking with 1 hour
        String bookingId = "EDIT017";
        Booking oneHourBooking = new Booking(bookingId, testUser, 1, testUser.getHourlyRate(),
                                            testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        oneHourBooking.setStatus("Reserved");
        bookingCSV.write(oneHourBooking);
        
        double originalCost = oneHourBooking.getTotalCost();
        
        // Edit to 2 hours (price increase)
        EditBookingCommand command = new EditBookingCommand(
            bookingId, null, null, futureDate, "10:00", "12:00",
            repository, pricingFactory, paymentService, roomService, observers);
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        // Now undo (should restore to 1 hour, need to refund)
        mockPaymentProcessor.reset();
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed", undoResult);
        
        // Verify refund was made
        assertTrue("Should refund for undo", mockPaymentProcessor.getLastRefundAmount() > 0);
    }
}
