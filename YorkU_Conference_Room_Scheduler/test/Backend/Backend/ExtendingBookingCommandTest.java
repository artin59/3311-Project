package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExtendingBookingCommandTest {
    
    private BookingCSV bookingCSV;
    private RoomCSV roomCSV;
    private UserCSV userCSV;
    private String originalBookingPath;
    private String originalRoomPath;
    private String originalUserPath;
    
    private static final String TEST_BOOKING_PATH = "TestBookingDatabase2.csv";
    private static final String TEST_ROOM_PATH = "TestRoomDatabase2.csv";
    private static final String TEST_USER_PATH = "TestDatabase.csv";
    
    private User testUser;
    private Room testRoom;
    private Room testRoom2;
    
    static class MockPaymentProcessor implements PaymentProcessor {
        private double lastChargeAmount = 0;
        private double lastRefundAmount = 0;
        private boolean chargeShouldFail = false;
        private boolean refundShouldFail = false;
        
        @Override
        public boolean charge(double amount) {
            if (chargeShouldFail) {
                return false;
            }
            lastChargeAmount = amount;
            return true;
        }
        
        @Override
        public boolean refund(double amount) {
            if (refundShouldFail) {
                return false;
            }
            lastRefundAmount = amount;
            return true;
        }
        
        public double getLastChargeAmount() {
            return lastChargeAmount;
        }
        
        public double getLastRefundAmount() {
            return lastRefundAmount;
        }
        
        public void setChargeShouldFail(boolean fail) {
            chargeShouldFail = fail;
        }
        
        public void setRefundShouldFail(boolean fail) {
            refundShouldFail = fail;
        }
        
        public void reset() {
            lastChargeAmount = 0;
            lastRefundAmount = 0;
            chargeShouldFail = false;
            refundShouldFail = false;
        }
    }
    
    static class MockBookingObserver implements BookingObserver {
        private Booking lastUpdatedBooking = null;
        private String lastCancelledBookingId = null;
        private Booking lastCreatedBooking = null;
        private int updateCount = 0;
        
        @Override
        public void onBookingUpdated(Booking booking) {
            lastUpdatedBooking = booking;
            updateCount++;
        }
        
        @Override
        public void onBookingCancelled(String bookingId) {
            lastCancelledBookingId = bookingId;
        }
        
        @Override
        public void onBookingCreated(Booking booking) {
            lastCreatedBooking = booking;
        }
        
        public Booking getLastUpdatedBooking() {
            return lastUpdatedBooking;
        }
        
        public String getLastCancelledBookingId() {
            return lastCancelledBookingId;
        }
        
        public Booking getLastCreatedBooking() {
            return lastCreatedBooking;
        }
        
        public int getUpdateCount() {
            return updateCount;
        }
        
        public void reset() {
            lastUpdatedBooking = null;
            lastCancelledBookingId = null;
            lastCreatedBooking = null;
            updateCount = 0;
        }
    }
    
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
        
        testUser = new Student("test@yorku.ca", "password123", "12345678");
        userCSV.write(testUser);
        
        List<Room> rooms = roomCSV.findAll();
        if (rooms.size() >= 2) {
            testRoom = rooms.get(0);
            testRoom2 = rooms.get(1);
        } else if (rooms.size() == 1) {
            testRoom = rooms.get(0);
            testRoom2 = new Room(20, "BuildingB", "202");
            roomCSV.write(testRoom2);
        } else {
            testRoom = new Room(10, "BuildingA", "101");
            testRoom2 = new Room(20, "BuildingB", "202");
            roomCSV.write(testRoom);
            roomCSV.write(testRoom2);
        }
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
        java.io.File bookingFile = new java.io.File(TEST_BOOKING_PATH);
        com.csvreader.CsvWriter csvWrite = new com.csvreader.CsvWriter(new java.io.FileWriter(TEST_BOOKING_PATH, false), ',');
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
        
        java.io.File userFile = new java.io.File(TEST_USER_PATH);
        if (!userFile.exists()) {
            com.csvreader.CsvWriter csvWrite1 = new com.csvreader.CsvWriter(new java.io.FileWriter(TEST_USER_PATH, false), ',');
            csvWrite1.write("ID");
            csvWrite1.write("Type");
            csvWrite1.write("Org ID");
            csvWrite1.write("Email");
            csvWrite1.write("Password");
            csvWrite1.write("Date Created");
            csvWrite1.endRecord();
            csvWrite1.close();
        }
        
        java.io.File roomFile = new java.io.File(TEST_ROOM_PATH);
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
                com.csvreader.CsvReader csvRead = new com.csvreader.CsvReader(TEST_ROOM_PATH);
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
            com.csvreader.CsvWriter csvWrite1 = new com.csvreader.CsvWriter(new java.io.FileWriter(TEST_ROOM_PATH, false), ',');
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return futureDate.format(formatter);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_BookingNotFound() {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "NONEXISTENT", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when booking not found", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_InvalidStatus() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Room testRoomForStatus = roomService.addRoom(15, "BuildingStatus", "STATUS001");
        assertNotNull("Room should be created", testRoomForStatus);
        
        roomService.bookRoom(testRoomForStatus.getRoomId(), "EXTEND001", testUser.getAccountId(), futureDate, "10:00", "11:00");
        roomService.checkIn(testRoomForStatus.getRoomId());
        roomService.checkOut(testRoomForStatus.getRoomId());
        
        Room roomAfterCheckout = roomService.getRoomById(testRoomForStatus.getRoomId());
        assertNotNull("Room should be found after checkout", roomAfterCheckout);
        roomAfterCheckout.getRoomContext().setBookingInfo("EXTEND001", testUser.getAccountId(), futureDate, "10:00", "11:00");
        RoomCSV.getInstance().update(roomAfterCheckout);
        
        Booking booking = new Booking("EXTEND001", testUser, 1, testUser.getHourlyRate(),
                                     testRoomForStatus.getRoomNumber(), futureDate, "10:00", "11:00");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND001", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when booking status is not InUse or Reserved", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_EndTimePassed() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        LocalDate pastDate = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String pastDateStr = pastDate.format(formatter);
        
        Booking booking = new Booking("EXTEND002", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), pastDateStr, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND002", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when end time has passed", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_NullEndTime() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND003", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", null);
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND003", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when end time is null", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_TimeConflict() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking1 = new Booking("EXTEND004", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking1.setStatus("InUse");
        bookingCSV.write(booking1);
        
        Booking booking2 = new Booking("EXTEND005", testUser, 1, testUser.getHourlyRate(),
                                      testRoom.getRoomNumber(), futureDate, "11:00", "12:00");
        bookingCSV.write(booking2);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND004", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when extended time slot conflicts", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_Success_InUse() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        MockBookingObserver mockObserver = new MockBookingObserver();
        List<BookingObserver> observers = new ArrayList<>();
        observers.add(mockObserver);
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND006", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND006", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should successfully extend booking", result);
        
        Booking updated = repository.findById("EXTEND006");
        assertNotNull("Booking should still exist", updated);
        assertEquals("End time should be extended", "12:00", updated.getBookingEndTime());
        assertEquals("Hours should be increased", 2, updated.getHours());
        assertTrue("Should charge additional amount", mockProcessor.getLastChargeAmount() > 0);
        assertEquals("Observer should be notified", 1, mockObserver.getUpdateCount());
    }
    
    @Test
    public void testExtendBookingCommand_Execute_Success_Reserved() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        MockBookingObserver mockObserver = new MockBookingObserver();
        List<BookingObserver> observers = new ArrayList<>();
        observers.add(mockObserver);
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND007", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("Reserved");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND007", 2, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should successfully extend booking", result);
        
        Booking updated = repository.findById("EXTEND007");
        assertNotNull("Booking should still exist", updated);
        assertEquals("End time should be extended", "13:00", updated.getBookingEndTime());
        assertEquals("Hours should be increased", 3, updated.getHours());
    }
    
    @Test
    public void testExtendBookingCommand_Execute_PaymentFailure() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        mockProcessor.setChargeShouldFail(true);
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND008", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND008", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when payment fails", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_ZeroAdditionalAmount() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND009", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND009", 0, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should succeed even with zero additional amount", result);
        assertEquals("Should not charge when amount is zero", 0.0, mockProcessor.getLastChargeAmount(), 0.01);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_RoomNotFound() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND010", testUser, 1, testUser.getHourlyRate(),
                                     "NONEXISTENT", futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND010", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should succeed even if room not found", result);
    }
    
    @Test
    public void testExtendBookingCommand_Undo_NoOriginalBooking() {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND011", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.undo();
        assertFalse("Should return false when no original booking", result);
    }
    
    @Test
    public void testExtendBookingCommand_Undo_Success() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        MockBookingObserver mockObserver = new MockBookingObserver();
        List<BookingObserver> observers = new ArrayList<>();
        observers.add(mockObserver);
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND012", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND012", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        double originalCharge = mockProcessor.getLastChargeAmount();
        mockProcessor.reset();
        
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed", undoResult);
        
        Booking reverted = repository.findById("EXTEND012");
        assertNotNull("Booking should still exist", reverted);
        assertEquals("End time should be reverted", "11:00", reverted.getBookingEndTime());
        assertEquals("Hours should be reverted", 1, reverted.getHours());
        assertTrue("Should refund additional amount", mockProcessor.getLastRefundAmount() > 0);
    }
    
    @Test
    public void testExtendBookingCommand_Undo_RoomNotFound() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND013", testUser, 1, testUser.getHourlyRate(),
                                     "NONEXISTENT", futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND013", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed even if room not found", undoResult);
    }
    
    @Test
    public void testExtendBookingCommand_CalculateNewEndTime_MidnightWrap() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND014", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "23:00", "23:00");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND014", 2, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should successfully extend booking", result);
        
        Booking updated = repository.findById("EXTEND014");
        assertNotNull("Booking should still exist", updated);
        assertEquals("End time should wrap around midnight", "01:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testExtendBookingCommand_Execute_CaseInsensitiveStatus() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND015", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus("inuse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND015", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should accept case-insensitive status", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_EmptyEndTime() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND016", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "");
        booking.setStatus("InUse");
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND016", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when end time is empty", result);
    }
    
    @Test
    public void testExtendBookingCommand_Execute_NullStatus() throws Exception {
        BookingRepository repository = BookingRepository.getInstance();
        PricingPolicyFactory pricingFactory = new PricingPolicyFactory();
        MockPaymentProcessor mockProcessor = new MockPaymentProcessor();
        PaymentService paymentService = PaymentService.getInstance();
        paymentService.setProcessor(mockProcessor);
        RoomService roomService = new RoomService();
        List<BookingObserver> observers = new ArrayList<>();
        
        String futureDate = getFutureDate();
        Booking booking = new Booking("EXTEND017", testUser, 1, testUser.getHourlyRate(),
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        booking.setStatus(null);
        bookingCSV.write(booking);
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            "EXTEND017", 1, repository, pricingFactory, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when status is null", result);
    }
}

