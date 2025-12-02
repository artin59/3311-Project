package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class ReservationSystemTest {
    
    private BookingCSV bookingCSV;
    private RoomCSV roomCSV;
    private UserCSV userCSV;
    private String originalBookingPath;
    private String originalRoomPath;
    private String originalUserPath;
    
    private static final String TEST_BOOKING_PATH = "TestBookingDatabase2.csv";
    private static final String TEST_ROOM_PATH = "TestRoomDatabase2.csv";
    private static final String TEST_USER_PATH = "TestDatabase.csv";
    
    @Before
    public void setUp() throws Exception {
        bookingCSV = BookingCSV.getInstance();
        roomCSV = RoomCSV.getInstance();
        userCSV = UserCSV.getInstance();
        
        Field bookingPathField = BookingCSV.class.getDeclaredField("BOOKING_PATH");
        bookingPathField.setAccessible(true);
        originalBookingPath = (String) bookingPathField.get(bookingCSV);
        
        Field roomPathField = BookingCSV.class.getDeclaredField("ROOM_PATH");
        roomPathField.setAccessible(true);
        originalRoomPath = (String) roomPathField.get(bookingCSV);
        
        Field userPathField = UserCSV.class.getDeclaredField("PATH");
        userPathField.setAccessible(true);
        originalUserPath = (String) userPathField.get(userCSV);
        
        bookingPathField.set(bookingCSV, TEST_BOOKING_PATH);
        roomPathField.set(bookingCSV, TEST_ROOM_PATH);
        userPathField.set(userCSV, TEST_USER_PATH);
        
        Field roomCSVPathField = RoomCSV.class.getDeclaredField("PATH");
        roomCSVPathField.setAccessible(true);
        roomCSVPathField.set(roomCSV, TEST_ROOM_PATH);
        
        initializeTestFiles();
    }
    
    @After
    public void tearDown() throws Exception {
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
    }
    
    private void initializeTestFiles() throws Exception {
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
        
        if (roomFile.exists()) {
            try {
                CsvReader csvRead = new CsvReader(TEST_ROOM_PATH);
                csvRead.readHeaders();
                String[] headers = csvRead.getHeaders();
                
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
    
    private String getFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        return futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    @Test
    public void testReservationSystem_GetInstance() {
        ReservationSystem instance1 = ReservationSystem.getInstance();
        ReservationSystem instance2 = ReservationSystem.getInstance();
        assertNotNull("Instance should not be null", instance1);
        assertSame("Should return same instance", instance1, instance2);
    }
    
    @Test
    public void testReservationSystem_CreateUserForLogin_Success() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("loginuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        User user = system.createUserForLogin("loginuser@yorku.ca", "Student");
        assertNotNull("User should be created", user);
        assertEquals("Email should match", "loginuser@yorku.ca", user.getEmail());
        assertEquals("Account type should match", "Student", user.getAccountType());
    }
    
    @Test
    public void testReservationSystem_CreateUserForLogin_TypeMismatch() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("typemismatch@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        try {
            system.createUserForLogin("typemismatch@yorku.ca", "Faculty");
            fail("Should throw IllegalArgumentException for type mismatch");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention user not found", e.getMessage().contains("User not found"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateUserForLogin_UserNotFound() {
        ReservationSystem system = ReservationSystem.getInstance();
        
        try {
            system.createUserForLogin("nonexistent@yorku.ca", "Student");
            fail("Should throw IllegalArgumentException for non-existent user");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention user not found", e.getMessage().contains("User not found"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateUserForLogin_NotUserAccount() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Admin admin = new Admin("adminlogin@yorku.ca", "adminpass");
        userCSV.write(admin);
        
        try {
            system.createUserForLogin("adminlogin@yorku.ca", "Admin");
            fail("Should throw IllegalArgumentException for non-User account");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention user not found", e.getMessage().contains("User not found"));
        }
    }
    
    @Test
    public void testReservationSystem_CalculateHourlyRate_Student() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("rateuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        double rate = system.calculateHourlyRate(student);
        assertTrue("Rate should be positive", rate > 0);
    }
    
    @Test
    public void testReservationSystem_CalculateHourlyRate_Faculty() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Faculty faculty = new Faculty("ratefaculty@yorku.ca", "password123", "FAC001");
        userCSV.write(faculty);
        
        double rate = system.calculateHourlyRate(faculty);
        assertTrue("Rate should be positive", rate > 0);
    }
    
    @Test
    public void testReservationSystem_CalculateHourlyRate_Staff() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Staff staff = new Staff("ratestaff@yorku.ca", "password123", "STAFF001");
        userCSV.write(staff);
        
        double rate = system.calculateHourlyRate(staff);
        assertTrue("Rate should be positive", rate > 0);
    }
    
    @Test
    public void testReservationSystem_CalculateHourlyRate_ExternalPartner() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        ExternalPartner partner = new ExternalPartner("ratepartner@external.com", "password123", "ORG001");
        userCSV.write(partner);
        
        double rate = system.calculateHourlyRate(partner);
        assertTrue("Rate should be positive", rate > 0);
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithoutRoom() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("bookinguser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Booking booking = system.createBooking(student, 2, 10.0);
        assertNotNull("Booking should be created", booking);
        assertEquals("User should match", student.getEmail(), booking.getUser().getEmail());
        assertEquals("Hours should match", 2, booking.getHours());
        assertEquals("Rate should match", 10.0, booking.getRate(), 0.01);
        assertNotNull("Booking ID should be generated", booking.getBookingId());
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_Success() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("roombooking@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room testRoomForBooking = new Room(15, "BuildingRS", "RS001");
        roomCSV.write(testRoomForBooking);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, 
                                               testRoomForBooking.getRoomNumber(), 
                                               futureDate, "10:00", "11:00");
        assertNotNull("Booking should be created", booking);
        assertEquals("Room number should match", testRoomForBooking.getRoomNumber(), booking.getRoomNumber());
        assertEquals("Date should match", futureDate, booking.getBookingDate());
        assertEquals("Start time should match", "10:00", booking.getBookingStartTime());
        assertEquals("End time should match", "11:00", booking.getBookingEndTime());
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_NullRoomNumber() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("nullroom@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        try {
            system.createBooking(student, 1, 10.0, null, "2024-01-20", "10:00", "11:00");
            fail("Should throw IllegalArgumentException for null room number");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention room number", e.getMessage().contains("Room number"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_EmptyRoomNumber() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("emptyroom@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        try {
            system.createBooking(student, 1, 10.0, "", "2024-01-20", "10:00", "11:00");
            fail("Should throw IllegalArgumentException for empty room number");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention room number", e.getMessage().contains("Room number"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_RoomNotFound() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("notfoundroom@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        try {
            system.createBooking(student, 1, 10.0, "NONEXISTENT", "2024-01-20", "10:00", "11:00");
            fail("Should throw IllegalArgumentException for non-existent room");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention room not found", e.getMessage().contains("Room not found"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_RoomDisabled() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("disabledroom@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room disabledRoom = new Room(15, "BuildingDisabled", "DISABLED001");
        disabledRoom.disable();
        roomCSV.write(disabledRoom);
        
        try {
            system.createBooking(student, 1, 10.0, disabledRoom.getRoomNumber(), 
                                "2024-01-20", "10:00", "11:00");
            fail("Should throw IllegalStateException for disabled room");
        } catch (IllegalStateException e) {
            assertTrue("Error message should mention room not enabled", e.getMessage().contains("not enabled"));
        }
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_TimeConflict() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("conflictuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room conflictRoom = new Room(15, "BuildingConflict", "CONFLICT001");
        roomCSV.write(conflictRoom);
        
        String futureDate = getFutureDate();
        Booking existingBooking = new Booking("CONFLICT001", student, 1, 10.0,
                                              conflictRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        bookingCSV.write(existingBooking);
        
        try {
            system.createBooking(student, 1, 10.0, conflictRoom.getRoomNumber(),
                                futureDate, "10:00", "11:00");
            fail("Should throw IllegalStateException for time conflict");
        } catch (IllegalStateException e) {
            assertTrue("Error message should mention conflict", e.getMessage().contains("already reserved"));
        }
    }
    
    @Test
    public void testReservationSystem_FindBooking_InMemory() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("finduser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Booking booking = system.createBooking(student, 1, 10.0);
        String bookingId = booking.getBookingId();
        
        Booking found = system.findBooking(bookingId);
        assertNotNull("Booking should be found in memory", found);
        assertEquals("Booking ID should match", bookingId, found.getBookingId());
    }
    
    @Test
    public void testReservationSystem_FindBooking_FromDatabase() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("finddbuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room findRoom = new Room(15, "BuildingFind", "FIND001");
        roomCSV.write(findRoom);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, findRoom.getRoomNumber(),
                                               futureDate, "10:00", "11:00");
        String bookingId = booking.getBookingId();
        
        ReservationSystem newSystem = ReservationSystem.getInstance();
        Booking found = newSystem.findBooking(bookingId);
        assertNotNull("Booking should be found from database", found);
        assertEquals("Booking ID should match", bookingId, found.getBookingId());
    }
    
    @Test
    public void testReservationSystem_FindBooking_NotFound() {
        ReservationSystem system = ReservationSystem.getInstance();
        
        Booking found = system.findBooking("NONEXISTENT");
        assertNull("Booking should not be found", found);
    }
    
    @Test
    public void testReservationSystem_UpdateBooking() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("updateuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Booking booking = system.createBooking(student, 1, 10.0);
        booking.setHours(2);
        booking.setRate(20.0);
        
        system.updateBooking(booking);
        
        Booking updated = system.findBooking(booking.getBookingId());
        assertNotNull("Booking should still exist", updated);
        assertEquals("Hours should be updated", 2, updated.getHours());
        assertEquals("Rate should be updated", 20.0, updated.getRate(), 0.01);
    }
    
    @Test
    public void testReservationSystem_CheckIn_Success() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("checkinuser@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room checkInRoom = new Room(15, "BuildingCheckIn", "CHECKIN001");
        roomCSV.write(checkInRoom);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, checkInRoom.getRoomNumber(),
                                               futureDate, "10:00", "11:00");
        
        boolean result = system.checkIn(booking.getBookingId(), student.getEmail());
        assertTrue("Check-in should succeed", result);
        
        Booking checkedIn = system.findBooking(booking.getBookingId());
        assertNotNull("Booking should still exist", checkedIn);
        assertEquals("Status should be InUse", "InUse", checkedIn.getStatus());
    }
    
    @Test
    public void testReservationSystem_CheckIn_BookingNotFound() {
        ReservationSystem system = ReservationSystem.getInstance();
        
        boolean result = system.checkIn("NONEXISTENT", "test@yorku.ca");
        assertFalse("Check-in should fail for non-existent booking", result);
    }
    
    @Test
    public void testReservationSystem_CheckIn_EmailMismatch() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("emailmismatch@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room emailRoom = new Room(15, "BuildingEmail", "EMAIL001");
        roomCSV.write(emailRoom);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, emailRoom.getRoomNumber(),
                                               futureDate, "10:00", "11:00");
        
        boolean result = system.checkIn(booking.getBookingId(), "wrong@yorku.ca");
        assertFalse("Check-in should fail for email mismatch", result);
    }
    
    @Test
    public void testReservationSystem_CheckIn_EmailCaseInsensitive() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("casecheckin@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room caseRoom = new Room(15, "BuildingCase", "CASE001");
        roomCSV.write(caseRoom);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, caseRoom.getRoomNumber(),
                                               futureDate, "10:00", "11:00");
        
        boolean result = system.checkIn(booking.getBookingId(), "CASECHECKIN@YORKU.CA");
        assertTrue("Check-in should succeed with case-insensitive email", result);
    }
    
    @Test
    public void testReservationSystem_CheckIn_UpdatesRoomState() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("roomstate@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room stateRoom = new Room(15, "BuildingState", "STATE001");
        roomCSV.write(stateRoom);
        
        String futureDate = getFutureDate();
        Booking booking = system.createBooking(student, 1, 10.0, stateRoom.getRoomNumber(),
                                               futureDate, "10:00", "11:00");
        
        system.checkIn(booking.getBookingId(), student.getEmail());
        
        Room updatedRoom = roomCSV.findByLocation(stateRoom.getBuildingName(), stateRoom.getRoomNumber());
        assertNotNull("Room should still exist", updatedRoom);
        assertEquals("Room condition should be InUse", "InUse", updatedRoom.getCondition());
    }
    
    @Test
    public void testReservationSystem_CheckIn_NoRoomNumber() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("noroom@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Booking booking = system.createBooking(student, 1, 10.0);
        
        boolean result = system.checkIn(booking.getBookingId(), student.getEmail());
        assertTrue("Check-in should succeed even without room number", result);
        
        Booking checkedIn = system.findBooking(booking.getBookingId());
        assertEquals("Status should be InUse", "InUse", checkedIn.getStatus());
    }
    
    @Test
    public void testReservationSystem_GetUserFactory() {
        ReservationSystem system = ReservationSystem.getInstance();
        UserFactory factory = system.getUserFactory();
        assertNotNull("UserFactory should not be null", factory);
    }
    
    @Test
    public void testReservationSystem_GetPricingFactory() {
        ReservationSystem system = ReservationSystem.getInstance();
        PricingPolicyFactory factory = system.getPricingFactory();
        assertNotNull("PricingPolicyFactory should not be null", factory);
    }
    
    @Test
    public void testReservationSystem_GenerateBookingId() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("idgen@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Booking booking1 = system.createBooking(student, 1, 10.0);
        Booking booking2 = system.createBooking(student, 1, 10.0);
        
        assertNotNull("Booking ID 1 should be generated", booking1.getBookingId());
        assertNotNull("Booking ID 2 should be generated", booking2.getBookingId());
        assertTrue("Booking IDs should be different", !booking1.getBookingId().equals(booking2.getBookingId()));
        assertTrue("Booking ID should start with B", booking1.getBookingId().startsWith("B"));
    }
    
    @Test
    public void testReservationSystem_CreateBooking_WithRoom_RoomBookedFailure() throws Exception {
        ReservationSystem system = ReservationSystem.getInstance();
        Student student = new Student("bookfail@yorku.ca", "password123", "12345678");
        userCSV.write(student);
        
        Room failRoom = new Room(15, "BuildingFail", "FAIL001");
        failRoom.disable();
        roomCSV.write(failRoom);
        
        try {
            failRoom.enable();
            roomCSV.update(failRoom);
            
            String futureDate = getFutureDate();
            system.createBooking(student, 1, 10.0, failRoom.getRoomNumber(),
                                futureDate, "10:00", "11:00");
            
            failRoom.disable();
            roomCSV.update(failRoom);
            
            try {
                system.createBooking(student, 1, 10.0, failRoom.getRoomNumber(),
                                    futureDate, "12:00", "13:00");
                fail("Should throw IllegalStateException if room booking fails");
            } catch (IllegalStateException e) {
                assertTrue("Error message should mention failed to book", e.getMessage().contains("Failed to book"));
            }
        } catch (Exception e) {
        }
    }
    
}

