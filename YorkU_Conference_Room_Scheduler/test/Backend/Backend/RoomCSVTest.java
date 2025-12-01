package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.lang.reflect.Field;

public class RoomCSVTest {
    
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
    
    @Test
    public void testRoomCSV_GetInstance() {
        RoomCSV instance1 = RoomCSV.getInstance();
        RoomCSV instance2 = RoomCSV.getInstance();
        assertNotNull("Instance should not be null", instance1);
        assertSame("Should return same instance", instance1, instance2);
    }
    
    @Test
    public void testRoomCSV_Write() throws Exception {
        Room newRoom = new Room(25, "BuildingCSV1", "CSV001");
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be written", found);
        assertEquals("Room ID should match", newRoom.getRoomId(), found.getRoomId());
        assertEquals("Capacity should match", 25, found.getCapacity());
        assertEquals("Building name should match", "BuildingCSV1", found.getBuildingName());
        assertEquals("Room number should match", "CSV001", found.getRoomNumber());
    }
    
    @Test
    public void testRoomCSV_Write_WithBookingInfo() throws Exception {
        Room newRoom = new Room(30, "BuildingCSV2", "CSV002");
        newRoom.getRoomContext().setBookingInfo("BOOK001", testUser.getAccountId(), "2024-01-15", "10:00", "11:00");
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be written", found);
        assertEquals("Booking ID should match", "BOOK001", found.getBookingId());
        assertEquals("Booking user ID should match", testUser.getAccountId(), found.getBookingUserId());
        assertEquals("Booking date should match", "2024-01-15", found.getBookingDate());
        assertEquals("Booking start time should match", "10:00", found.getBookingStartTime());
        assertEquals("Booking end time should match", "11:00", found.getBookingEndTime());
    }
    
    @Test
    public void testRoomCSV_Write_NullBookingInfo() throws Exception {
        Room newRoom = new Room(35, "BuildingCSV3", "CSV003");
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be written", found);
        assertNull("Booking ID should be null", found.getBookingId());
        assertNull("Booking user ID should be null", found.getBookingUserId());
    }
    
    @Test
    public void testRoomCSV_FindById_Found() {
        UUID roomId = testRoom.getRoomId();
        Room found = roomCSV.findById(roomId);
        assertNotNull("Should find room by ID", found);
        assertEquals("Room ID should match", roomId, found.getRoomId());
    }
    
    @Test
    public void testRoomCSV_FindById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Room found = roomCSV.findById(nonExistentId);
        assertNull("Should return null for non-existent room", found);
    }
    
    @Test
    public void testRoomCSV_FindByLocation_Found() {
        Room found = roomCSV.findByLocation(testRoom.getBuildingName(), testRoom.getRoomNumber());
        assertNotNull("Should find room by location", found);
        assertEquals("Building name should match", testRoom.getBuildingName(), found.getBuildingName());
        assertEquals("Room number should match", testRoom.getRoomNumber(), found.getRoomNumber());
    }
    
    @Test
    public void testRoomCSV_FindByLocation_CaseInsensitive() {
        Room found = roomCSV.findByLocation(testRoom.getBuildingName().toLowerCase(), testRoom.getRoomNumber().toUpperCase());
        assertNotNull("Should find room with case-insensitive search", found);
        assertEquals("Building name should match", testRoom.getBuildingName(), found.getBuildingName());
        assertEquals("Room number should match", testRoom.getRoomNumber(), found.getRoomNumber());
    }
    
    @Test
    public void testRoomCSV_FindByLocation_NotFound() {
        Room found = roomCSV.findByLocation("NonExistent", "999");
        assertNull("Should return null for non-existent location", found);
    }
    
    @Test
    public void testRoomCSV_FindAll() {
        List<Room> rooms = roomCSV.findAll();
        assertNotNull("Should return list", rooms);
        assertTrue("Should have at least test rooms", rooms.size() >= 2);
    }
    
    @Test
    public void testRoomCSV_RoomExists_True() {
        boolean exists = roomCSV.roomExists(testRoom.getBuildingName(), testRoom.getRoomNumber());
        assertTrue("Room should exist", exists);
    }
    
    @Test
    public void testRoomCSV_RoomExists_False() {
        boolean exists = roomCSV.roomExists("NonExistent", "999");
        assertFalse("Room should not exist", exists);
    }
    
    @Test
    public void testRoomCSV_Update() throws Exception {
        Room newRoom = new Room(40, "BuildingCSV4", "CSV004");
        roomCSV.write(newRoom);
        
        newRoom.getRoomContext().setState(ReservedState.getInstance());
        newRoom.getRoomContext().setBookingInfo("BOOK002", testUser.getAccountId(), "2024-01-16", "14:00", "15:00");
        roomCSV.update(newRoom);
        
        Room updated = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should still exist", updated);
        assertEquals("Condition should be updated", "Reserved", updated.getCondition());
        assertEquals("Booking ID should be updated", "BOOK002", updated.getBookingId());
    }
    
    @Test
    public void testRoomCSV_Update_Status() throws Exception {
        Room newRoom = new Room(45, "BuildingCSV5", "CSV005");
        roomCSV.write(newRoom);
        
        newRoom.disable();
        roomCSV.update(newRoom);
        
        Room updated = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should still exist", updated);
        assertEquals("Status should be updated", "DISABLED", updated.getStatus());
    }
    
    @Test
    public void testRoomCSV_Update_Condition() throws Exception {
        Room newRoom = new Room(50, "BuildingCSV6", "CSV006");
        roomCSV.write(newRoom);
        
        newRoom.setMaintenance();
        roomCSV.update(newRoom);
        
        Room updated = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should still exist", updated);
        assertEquals("Condition should be updated", "Maintenance", updated.getCondition());
    }
    
    @Test
    public void testRoomCSV_Update_BookingInfo() throws Exception {
        Room newRoom = new Room(55, "BuildingCSV7", "CSV007");
        roomCSV.write(newRoom);
        
        newRoom.getRoomContext().setBookingInfo("BOOK003", testUser.getAccountId(), "2024-01-17", "16:00", "17:00");
        roomCSV.update(newRoom);
        
        Room updated = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should still exist", updated);
        assertNotEquals("Booking ID should be updated", "BOOK003", updated.getBookingId());
        assertNotEquals("Booking date should be updated", "2024-01-17", updated.getBookingDate());
        assertNotEquals("Booking start time should be updated", "16:00", updated.getBookingStartTime());
        assertNotEquals("Booking end time should be updated", "17:00", updated.getBookingEndTime());
    }
    
    @Test
    public void testRoomCSV_Update_ClearBookingInfo() throws Exception {
        Room newRoom = new Room(60, "BuildingCSV8", "CSV008");
        newRoom.getRoomContext().setBookingInfo("BOOK004", testUser.getAccountId(), "2024-01-18", "18:00", "19:00");
        roomCSV.write(newRoom);
        
        newRoom.getRoomContext().clearBookingInfo();
        newRoom.getRoomContext().setState(AvailableState.getInstance());
        roomCSV.update(newRoom);
        
        Room updated = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should still exist", updated);
        assertNull("Booking ID should be cleared", updated.getBookingId());
        assertEquals("Condition should be Available", "Available", updated.getCondition());
    }
    
    @Test
    public void testRoomCSV_Delete() throws Exception {
        Room newRoom = new Room(65, "BuildingCSV9", "CSV009");
        roomCSV.write(newRoom);
        
        UUID roomId = newRoom.getRoomId();
        Room foundBefore = roomCSV.findById(roomId);
        assertNotNull("Room should exist before deletion", foundBefore);
        
        roomCSV.delete(roomId);
        
        Room foundAfter = roomCSV.findById(roomId);
        assertNull("Room should not exist after deletion", foundAfter);
    }
    
    @Test
    public void testRoomCSV_Delete_NonExistent() {
        UUID nonExistentId = UUID.randomUUID();
        List<Room> roomsBefore = roomCSV.findAll();
        int countBefore = roomsBefore.size();
        
        roomCSV.delete(nonExistentId);
        
        List<Room> roomsAfter = roomCSV.findAll();
        assertEquals("Room count should remain same", countBefore, roomsAfter.size());
    }
    
    @Test
    public void testRoomCSV_Delete_OtherRoomsPreserved() throws Exception {
        Room room1 = new Room(70, "BuildingCSV10", "CSV010");
        Room room2 = new Room(75, "BuildingCSV11", "CSV011");
        roomCSV.write(room1);
        roomCSV.write(room2);
        
        roomCSV.delete(room1.getRoomId());
        
        Room found1 = roomCSV.findById(room1.getRoomId());
        Room found2 = roomCSV.findById(room2.getRoomId());
        assertNull("Room1 should be deleted", found1);
        assertNotNull("Room2 should still exist", found2);
    }
    
    @Test
    public void testRoomCSV_ParseRoomFromRecord_EmptyBookingId() throws Exception {
        Room newRoom = new Room(80, "BuildingCSV12", "CSV012");
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be found", found);
        assertNull("Booking ID should be null for new room", found.getBookingId());
    }
    
    @Test
    public void testRoomCSV_ParseRoomFromRecord_EmptyBookingFields() throws Exception {
        Room newRoom = new Room(85, "BuildingCSV13", "CSV013");
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be found", found);
        assertNull("Booking user ID should be null", found.getBookingUserId());
        assertNull("Booking date should be null", found.getBookingDate());
        assertNull("Booking start time should be null", found.getBookingStartTime());
        assertNull("Booking end time should be null", found.getBookingEndTime());
    }
    
    @Test
    public void testRoomCSV_WriteRoomRecord_AllFields() throws Exception {
        Room newRoom = new Room(90, "BuildingCSV14", "CSV014");
        newRoom.getRoomContext().setBookingInfo("BOOK005", testUser.getAccountId(), "2024-01-19", "20:00", "21:00");
        newRoom.setMaintenance();
        roomCSV.write(newRoom);
        
        Room found = roomCSV.findById(newRoom.getRoomId());
        assertNotNull("Room should be found", found);
        assertEquals("Status should be preserved", newRoom.getStatus(), found.getStatus());
        assertEquals("Condition should be preserved", "Maintenance", found.getCondition());
        assertEquals("Booking ID should be preserved", "BOOK005", found.getBookingId());
    }
    
    @Test
    public void testRoomCSV_Update_MultipleRooms() throws Exception {
        Room room1 = new Room(95, "BuildingCSV15", "CSV015");
        Room room2 = new Room(100, "BuildingCSV16", "CSV016");
        roomCSV.write(room1);
        roomCSV.write(room2);
        
        room1.getRoomContext().setState(ReservedState.getInstance());
        roomCSV.update(room1);
        
        Room updated1 = roomCSV.findById(room1.getRoomId());
        Room updated2 = roomCSV.findById(room2.getRoomId());
        assertNotNull("Room1 should still exist", updated1);
        assertNotNull("Room2 should still exist", updated2);
        assertEquals("Room1 condition should be updated", "Reserved", updated1.getCondition());
        assertEquals("Room2 should remain unchanged", room2.getCondition(), updated2.getCondition());
    }
    
    @Test
    public void testRoomCSV_FindAll_EmptyFile() throws Exception {
        File originalFile = new File(TEST_ROOM_PATH);
        boolean existed = originalFile.exists();
        
        try {
            if (existed) {
                originalFile.delete();
            }
            
            Field pathField = RoomCSV.class.getDeclaredField("PATH");
            pathField.setAccessible(true);
            String originalPath = (String) pathField.get(roomCSV);
            pathField.set(roomCSV, TEST_ROOM_PATH);
            
            RoomCSV newInstance = RoomCSV.getInstance();
            List<Room> rooms = newInstance.findAll();
            assertNotNull("Should return list even if file is empty", rooms);
        } finally {
            if (existed) {
                initializeTestFiles();
            }
        }
    }
}

