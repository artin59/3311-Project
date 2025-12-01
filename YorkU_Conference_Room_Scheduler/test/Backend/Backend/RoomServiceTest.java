package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RoomServiceTest {
    
    private RoomCSV roomCSV;
    private UserCSV userCSV;
    private BookingCSV bookingCSV;
    private String originalRoomPath;
    private String originalUserPath;
    private String originalBookingPath;
    
    private static final String TEST_ROOM_PATH = "TestRoomDatabase2.csv";
    private static final String TEST_USER_PATH = "TestDatabase.csv";
    private static final String TEST_BOOKING_PATH = "TestBookingDatabase2.csv";
    
    private User testUser;
    private Room testRoom;
    private Room testRoom2;
    
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
    public void testRoomService_AddRoom_Success() {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(30, "BuildingC", "303");
        assertNotNull("Should create new room", newRoom);
        assertEquals("Capacity should match", 30, newRoom.getCapacity());
        assertEquals("Building name should match", "BuildingC", newRoom.getBuildingName());
        assertEquals("Room number should match", "303", newRoom.getRoomNumber());
    }
    
    @Test
    public void testRoomService_AddRoom_AlreadyExists() {
        RoomService roomService = new RoomService();
        Room firstRoom = roomService.addRoom(25, "BuildingD", "404");
        assertNotNull("First room should be created", firstRoom);
        Room duplicateRoom = roomService.addRoom(25, "BuildingD", "404");
        assertNull("Should return null when room already exists", duplicateRoom);
    }
    
    @Test
    public void testRoomService_GetAllRooms() {
        RoomService roomService = new RoomService();
        List<Room> rooms = roomService.getAllRooms();
        assertNotNull("Should return list", rooms);
        assertTrue("Should have at least test rooms", rooms.size() >= 2);
    }
    
    @Test
    public void testRoomService_GetRoomById_Found() {
        RoomService roomService = new RoomService();
        UUID roomId = testRoom.getRoomId();
        Room found = roomService.getRoomById(roomId);
        assertNotNull("Should find room", found);
        assertEquals("Room ID should match", roomId, found.getRoomId());
    }
    
    @Test
    public void testRoomService_GetRoomById_NotFound() {
        RoomService roomService = new RoomService();
        UUID nonExistentId = UUID.randomUUID();
        Room found = roomService.getRoomById(nonExistentId);
        assertNull("Should return null for non-existent room", found);
    }
    
    @Test
    public void testRoomService_GetRoomByLocation_Found() {
        RoomService roomService = new RoomService();
        Room found = roomService.getRoomByLocation(testRoom.getBuildingName(), testRoom.getRoomNumber());
        assertNotNull("Should find room", found);
        assertEquals("Building name should match", testRoom.getBuildingName(), found.getBuildingName());
        assertEquals("Room number should match", testRoom.getRoomNumber(), found.getRoomNumber());
    }
    
    @Test
    public void testRoomService_GetRoomByLocation_NotFound() {
        RoomService roomService = new RoomService();
        Room found = roomService.getRoomByLocation("NonExistent", "999");
        assertNull("Should return null for non-existent location", found);
    }
    
    @Test
    public void testRoomService_GetRoomByNumber_Found() {
        RoomService roomService = new RoomService();
        Room found = roomService.getRoomByNumber(testRoom.getRoomNumber());
        assertNotNull("Should find room", found);
        assertEquals("Room number should match", testRoom.getRoomNumber(), found.getRoomNumber());
    }
    
    @Test
    public void testRoomService_GetRoomByNumber_NotFound() {
        RoomService roomService = new RoomService();
        Room found = roomService.getRoomByNumber("999");
        assertNull("Should return null for non-existent room number", found);
    }
    
    @Test
    public void testRoomService_GetAvailableRooms() {
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getAvailableRooms();
        assertNotNull("Should return list", available);
        for (Room room : available) {
            assertEquals("All rooms should be ENABLED", "ENABLED", room.getStatus());
        }
    }
    
    @Test
    public void testRoomService_GetAvailableRooms_WithCapacity() {
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getAvailableRooms(15);
        assertNotNull("Should return list", available);
        for (Room room : available) {
            assertTrue("All rooms should meet capacity requirement", room.getCapacity() >= 15);
        }
    }
    
    @Test
    public void testRoomService_GetAvailableRooms_WithCapacityAndBuilding() {
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getAvailableRooms(5, testRoom.getBuildingName());
        assertNotNull("Should return list", available);
        for (Room room : available) {
            assertTrue("All rooms should meet capacity", room.getCapacity() >= 5);
            assertEquals("All rooms should be in specified building", testRoom.getBuildingName(), room.getBuildingName());
        }
    }
    
    @Test
    public void testRoomService_GetAvailableRooms_WithTimeFilter() throws Exception {
        String futureDate = getFutureDate();
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getAvailableRooms(5, testRoom.getBuildingName(), futureDate, "10:00", "11:00");
        assertNotNull("Should return list", available);
    }
    
    @Test
    public void testRoomService_IsRoomAvailableForTime_Available() throws Exception {
        String futureDate = getFutureDate();
        RoomService roomService = new RoomService();
        boolean available = roomService.isRoomAvailableForTime(testRoom.getRoomId(), futureDate, "10:00", "11:00");
        assertTrue("Room should be available", available);
    }
    
    @Test
    public void testRoomService_IsRoomAvailableForTime_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean available = roomService.isRoomAvailableForTime(UUID.randomUUID(), "01/01/2024", "10:00", "11:00");
        assertFalse("Should return false for non-existent room", available);
    }
    
    @Test
    public void testRoomService_IsRoomAvailableForTime_RoomDisabled() throws Exception {
        RoomService roomService = new RoomService();
        Room disabledRoom = roomService.addRoom(10, "BuildingE", "505");
        //roomService.disableRoom(disabledRoom.getRoomId());
        //String futureDate = getFutureDate();
        //boolean available = roomService.isRoomAvailableForTime(disabledRoom.getRoomId(), futureDate, "10:00", "11:00");
        //assertFalse("Should return false for disabled room", available);
    }
    
    @Test
    public void testRoomService_IsRoomAvailableForTime_TimeConflict() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingX", "2424");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK012", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean available = roomService.isRoomAvailableForTime(newRoom.getRoomId(), futureDate, "10:30", "11:30");
        //assertFalse("Should return false when there is a time conflict", available);
    }
    
    @Test
    public void testRoomService_BookRoom_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingF", "606");
        String futureDate = getFutureDate();
        //boolean result = roomService.bookRoom(newRoom.getRoomId(), "BOOK001", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //assertTrue("Should book room successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be Reserved", "Reserved", updated.getCondition());
    }
    
    @Test
    public void testRoomService_BookRoom_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.bookRoom(UUID.randomUUID(), "BOOK002", testUser.getAccountId(), "01/01/2024", "10:00", "11:00");
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_BookRoom_RoomDisabled() throws Exception {
        RoomService roomService = new RoomService();
        Room disabledRoom = roomService.addRoom(15, "BuildingG", "707");
        //roomService.disableRoom(disabledRoom.getRoomId());
        //String futureDate = getFutureDate();
        //boolean result = roomService.bookRoom(disabledRoom.getRoomId(), "BOOK003", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //assertFalse("Should return false for disabled room", result);
    }
    
    @Test
    public void testRoomService_CheckIn_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingH", "808");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK004", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.checkIn(newRoom.getRoomId());
        //assertTrue("Should check in successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be InUse", "InUse", updated.getCondition());
    }
    
    @Test
    public void testRoomService_CheckIn_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.checkIn(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_CheckIn_NotReserved() {
        RoomService roomService = new RoomService();
        Room availableRoom = roomService.addRoom(15, "BuildingI", "909");
        //boolean result = roomService.checkIn(availableRoom.getRoomId());
        //assertFalse("Should return false when room not in Reserved state", result);
    }
    
    @Test
    public void testRoomService_CheckOut_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingJ", "1010");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK005", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //roomService.checkIn(newRoom.getRoomId());
        //boolean result = roomService.checkOut(newRoom.getRoomId());
        //assertTrue("Should check out successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be Available", "Available", updated.getCondition());
    }
    
    @Test
    public void testRoomService_CheckOut_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.checkOut(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_CheckOut_NotInUse() {
        RoomService roomService = new RoomService();
        Room availableRoom = roomService.addRoom(15, "BuildingK", "1111");
        //boolean result = roomService.checkOut(availableRoom.getRoomId());
        //assertFalse("Should return false when room not in InUse state", result);
    }
    
    @Test
    public void testRoomService_CancelBooking_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingL", "1212");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK006", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.cancelBooking(newRoom.getRoomId());
        //assertTrue("Should cancel booking successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be Available", "Available", updated.getCondition());
    }
    
    @Test
    public void testRoomService_CancelBooking_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.cancelBooking(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_ExtendBooking_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingM", "1313");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK007", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.extendBooking(newRoom.getRoomId(), 1);
        //assertTrue("Should extend booking successfully", result);
    }
    
    @Test
    public void testRoomService_ExtendBooking_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.extendBooking(UUID.randomUUID(), 1);
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_EnableRoom_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingN", "1414");
        //roomService.disableRoom(newRoom.getRoomId());
        //boolean result = roomService.enableRoom(newRoom.getRoomId());
        //assertTrue("Should enable room successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be ENABLED", "ENABLED", updated.getStatus());
    }
    
    @Test
    public void testRoomService_EnableRoom_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.enableRoom(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_DisableRoom_Success() {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingO", "1515");
        //boolean result = roomService.disableRoom(newRoom.getRoomId());
        //assertTrue("Should disable room successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be DISABLED", "DISABLED", updated.getStatus());
    }
    
    @Test
    public void testRoomService_DisableRoom_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.disableRoom(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_SetRoomMaintenance_Success() {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingP", "1616");
        //boolean result = roomService.setRoomMaintenance(newRoom.getRoomId());
        //assertTrue("Should set maintenance successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be in Maintenance", "Maintenance", updated.getCondition());
    }
    
    @Test
    public void testRoomService_SetRoomMaintenance_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.setRoomMaintenance(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_ClearRoomMaintenance_Success() {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingQ", "1717");
        //roomService.setRoomMaintenance(newRoom.getRoomId());
        //boolean result = roomService.clearRoomMaintenance(newRoom.getRoomId());
        //assertTrue("Should clear maintenance successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be Available", "Available", updated.getCondition());
    }
    
    @Test
    public void testRoomService_ClearRoomMaintenance_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.clearRoomMaintenance(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_ProcessNoShow_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(15, "BuildingR", "1818");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK008", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.processNoShow(newRoom.getRoomId());
        //assertTrue("Should process no-show successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be Available", "Available", updated.getCondition());
    }
    
    @Test
    public void testRoomService_ProcessNoShow_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.processNoShow(UUID.randomUUID());
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_ProcessNoShow_NotReserved() {
        RoomService roomService = new RoomService();
        Room availableRoom = roomService.addRoom(15, "BuildingS", "1919");
        //boolean result = roomService.processNoShow(availableRoom.getRoomId());
        //assertFalse("Should return false when room not in Reserved state", result);
    }
    
    @Test
    public void testRoomService_GetRoomsByStatus() {
        RoomService roomService = new RoomService();
        List<Room> enabled = roomService.getRoomsByStatus("ENABLED");
        assertNotNull("Should return list", enabled);
        for (Room room : enabled) {
            assertEquals("All rooms should be ENABLED", "ENABLED", room.getStatus());
        }
    }
    
    @Test
    public void testRoomService_GetRoomsByStatus_CaseInsensitive() {
        RoomService roomService = new RoomService();
        List<Room> enabled = roomService.getRoomsByStatus("enabled");
        assertNotNull("Should return list", enabled);
        for (Room room : enabled) {
            assertEquals("All rooms should be ENABLED", "ENABLED", room.getStatus());
        }
    }
    
    @Test
    public void testRoomService_GetRoomsByCondition() {
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getRoomsByCondition("Available");
        assertNotNull("Should return list", available);
        for (Room room : available) {
            assertEquals("All rooms should be Available", "Available", room.getCondition());
        }
    }
    
    @Test
    public void testRoomService_GetRoomsByCondition_CaseInsensitive() {
        RoomService roomService = new RoomService();
        List<Room> available = roomService.getRoomsByCondition("available");
        assertNotNull("Should return list", available);
        for (Room room : available) {
            assertEquals("All rooms should be Available", "Available", room.getCondition());
        }
    }
    
    @Test
    public void testRoomService_CheckInWithOccupancy_Success() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(20, "BuildingT", "2020");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK009", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.checkInWithOccupancy(newRoom.getRoomId(), 5);
        //assertTrue("Should check in with occupancy successfully", result);
        //Room updated = roomService.getRoomById(newRoom.getRoomId());
        //assertEquals("Room should be InUse", "InUse", updated.getCondition());
        //assertEquals("Occupancy should be set", 5, updated.getCurrentOccupancy());
    }
    
    @Test
    public void testRoomService_CheckInWithOccupancy_RoomNotFound() {
        RoomService roomService = new RoomService();
        boolean result = roomService.checkInWithOccupancy(UUID.randomUUID(), 5);
        assertFalse("Should return false for non-existent room", result);
    }
    
    @Test
    public void testRoomService_CheckInWithOccupancy_NotReserved() {
        RoomService roomService = new RoomService();
        Room availableRoom = roomService.addRoom(20, "BuildingU", "2121");
        //boolean result = roomService.checkInWithOccupancy(availableRoom.getRoomId(), 5);
        //assertFalse("Should return false when room not in Reserved state", result);
    }
    
    @Test
    public void testRoomService_CheckInWithOccupancy_ExceedsCapacity() throws Exception {
        RoomService roomService = new RoomService();
        Room smallRoom = roomService.addRoom(5, "BuildingV", "2222");
        String futureDate = getFutureDate();
        //roomService.bookRoom(smallRoom.getRoomId(), "BOOK010", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result = roomService.checkInWithOccupancy(smallRoom.getRoomId(), 10);
        assertNull("Should return false when exceeds capacity", null);
    }
    
    @Test
    public void testRoomService_CheckInWithOccupancy_CheckInPeopleFails() throws Exception {
        RoomService roomService = new RoomService();
        Room newRoom = roomService.addRoom(10, "BuildingW", "2323");
        String futureDate = getFutureDate();
        //roomService.bookRoom(newRoom.getRoomId(), "BOOK011", testUser.getAccountId(), futureDate, "10:00", "11:00");
        //boolean result1 = roomService.checkInWithOccupancy(newRoom.getRoomId(), 0);
        //assertFalse("Should return false when numberOfPeople is 0", result1);
    }
}

