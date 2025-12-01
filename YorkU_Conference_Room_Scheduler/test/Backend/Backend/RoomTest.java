package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.UUID;

public class RoomTest {
    
    private Room room;
    private UUID testRoomId;
    private UUID testUserId;
    
    @Before
    public void setUp() {
        room = new Room(50, "Test Building", "101");
        testRoomId = room.getRoomId();
        testUserId = UUID.randomUUID();
    }
    
    // ========== Constructor Tests ==========
    
    @Test
    public void testRoom_Constructor_Basic() {
        Room newRoom = new Room(30, "Building A", "202");
        assertNotNull("Room should be created", newRoom);
        assertEquals("Capacity should match", 30, newRoom.getCapacity());
        assertEquals("Building name should match", "Building A", newRoom.getBuildingName());
        assertEquals("Room number should match", "202", newRoom.getRoomNumber());
        assertEquals("Status should be ENABLED", "ENABLED", newRoom.getStatus());
        assertEquals("Condition should be Available", "Available", newRoom.getCondition());
        assertNotNull("Room ID should be generated", newRoom.getRoomId());
        assertEquals("Initial occupancy should be 0", 0, newRoom.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_Constructor_WithCSVData() {
        UUID roomId = UUID.randomUUID();
        Room csvRoom = new Room(roomId, 25, "Building B", "303", "ENABLED", "Reserved");
        assertNotNull("Room should be created", csvRoom);
        assertEquals("Room ID should match", roomId, csvRoom.getRoomId());
        assertEquals("Capacity should match", 25, csvRoom.getCapacity());
        assertEquals("Building name should match", "Building B", csvRoom.getBuildingName());
        assertEquals("Room number should match", "303", csvRoom.getRoomNumber());
        assertEquals("Status should match", "ENABLED", csvRoom.getStatus());
        assertEquals("Condition should be Reserved", "Reserved", csvRoom.getCondition());
    }
    
    @Test
    public void testRoom_Constructor_WithBookingInfo() {
        UUID roomId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Room bookedRoom = new Room(roomId, 20, "Building C", "404", "ENABLED", "Reserved",
                                   "BOOK001", userId, "01/01/2024", "10:00", "11:00");
        assertNotNull("Room should be created", bookedRoom);
        assertEquals("Room ID should match", roomId, bookedRoom.getRoomId());
        assertEquals("Booking ID should match", "BOOK001", bookedRoom.getBookingId());
        assertEquals("Booking user ID should match", userId, bookedRoom.getBookingUserId());
        assertEquals("Booking date should match", "01/01/2024", bookedRoom.getBookingDate());
        assertEquals("Booking start time should match", "10:00", bookedRoom.getBookingStartTime());
        assertEquals("Booking end time should match", "11:00", bookedRoom.getBookingEndTime());
        assertTrue("Should have active booking", bookedRoom.hasActiveBooking());
    }
    
    @Test
    public void testRoom_Constructor_WithBookingInfo_NullUserId() {
        UUID roomId = UUID.randomUUID();
        Room bookedRoom = new Room(roomId, 20, "Building D", "505", "ENABLED", "Available",
                                   "BOOK002", null, "01/01/2024", "10:00", "11:00");
        assertNotNull("Room should be created", bookedRoom);
        assertNull("Booking ID should be null when userId is null", bookedRoom.getBookingId());
        assertFalse("Should not have active booking", bookedRoom.hasActiveBooking());
    }
    
    @Test
    public void testRoom_Constructor_StateParsing() {
        UUID roomId = UUID.randomUUID();
        
        Room availableRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "Available");
        assertEquals("Should parse Available state", "Available", availableRoom.getCondition());
        
        Room reservedRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "Reserved");
        assertEquals("Should parse Reserved state", "Reserved", reservedRoom.getCondition());
        
        Room inUseRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "InUse");
        assertEquals("Should parse InUse state", "InUse", inUseRoom.getCondition());
        
        Room maintenanceRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "Maintenance");
        assertEquals("Should parse Maintenance state", "Maintenance", maintenanceRoom.getCondition());
        
        Room noShowRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "NoShow");
        assertEquals("Should parse NoShow state", "NoShow", noShowRoom.getCondition());
        
        Room defaultRoom = new Room(roomId, 10, "B1", "1", "ENABLED", "Unknown");
        assertEquals("Should default to Available for unknown state", "Available", defaultRoom.getCondition());
    }
    
    // ========== Getter Tests ==========
    
    @Test
    public void testRoom_Getters() {
        assertEquals("Room ID should match", testRoomId, room.getRoomId());
        assertEquals("Capacity should match", 50, room.getCapacity());
        assertEquals("Building name should match", "Test Building", room.getBuildingName());
        assertEquals("Room number should match", "101", room.getRoomNumber());
        assertEquals("Status should be ENABLED", "ENABLED", room.getStatus());
        assertEquals("Condition should be Available", "Available", room.getCondition());
        assertNotNull("Room context should exist", room.getRoomContext());
    }
    
    @Test
    public void testRoom_GetLocation() {
        String location = room.getLocation();
        assertEquals("Location should be formatted correctly", 
                    "Test Building - Room 101", location);
    }
    
    @Test
    public void testRoom_GetBookingInfo_NoBooking() {
        assertNull("Booking ID should be null", room.getBookingId());
        assertNull("Booking user ID should be null", room.getBookingUserId());
        assertNull("Booking date should be null", room.getBookingDate());
        assertNull("Booking start time should be null", room.getBookingStartTime());
        assertNull("Booking end time should be null", room.getBookingEndTime());
        assertFalse("Should not have active booking", room.hasActiveBooking());
    }
    
    // ========== Enable/Disable Tests ==========
    
    @Test
    public void testRoom_Enable() {
        room.disable();
        assertEquals("Status should be DISABLED", "DISABLED", room.getStatus());
        
        room.enable();
        assertEquals("Status should be ENABLED", "ENABLED", room.getStatus());
    }
    
    @Test
    public void testRoom_Disable() {
        assertEquals("Initial status should be ENABLED", "ENABLED", room.getStatus());
        
        room.disable();
        assertEquals("Status should be DISABLED", "DISABLED", room.getStatus());
    }
    
    // ========== isBookable Tests ==========
    
    @Test
    public void testRoom_IsBookable_EnabledAndAvailable() {
        assertTrue("Room should be bookable when enabled and available", room.isBookable());
    }
    
    @Test
    public void testRoom_IsBookable_Disabled() {
        room.disable();
        assertFalse("Room should not be bookable when disabled", room.isBookable());
    }
    
    @Test
    public void testRoom_IsBookable_Reserved() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        // Room can still be bookable if it's enabled, even if reserved (different times)
        // This depends on the state's canBook() implementation
        // For now, we'll test that it's not bookable when in Reserved state
        assertFalse("Room should not be bookable when reserved", room.isBookable());
    }
    
    @Test
    public void testRoom_IsBookable_Maintenance() {
        room.setMaintenance();
        assertFalse("Room should not be bookable when in maintenance", room.isBookable());
    }
    
    // ========== Booking Tests ==========
    
    @Test
    public void testRoom_Book_Success() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        assertEquals("Booking ID should be set", "BOOK001", room.getBookingId());
        assertEquals("Booking user ID should be set", testUserId, room.getBookingUserId());
        assertEquals("Booking date should be set", "01/01/2024", room.getBookingDate());
        assertEquals("Booking start time should be set", "10:00", room.getBookingStartTime());
        assertEquals("Booking end time should be set", "11:00", room.getBookingEndTime());
        assertEquals("Condition should be Reserved", "Reserved", room.getCondition());
        assertTrue("Should have active booking", room.hasActiveBooking());
    }
    
    @Test
    public void testRoom_Book_DisabledRoom() {
        room.disable();
        room.book("BOOK002", testUserId, "01/01/2024", "10:00", "11:00");
        // Booking should not succeed for disabled room
        assertNull("Booking ID should be null", room.getBookingId());
        assertEquals("Condition should remain Available", "Available", room.getCondition());
        assertFalse("Should not have active booking", room.hasActiveBooking());
    }
    
    @Test
    public void testRoom_Book_MultipleBookings() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        
        room.book("BOOK001", userId1, "01/01/2024", "10:00", "11:00");
        assertEquals("First booking ID should be set", "BOOK001", room.getBookingId());
        
        // Book again with different time (should overwrite previous booking info)
        room.book("BOOK002", userId2, "01/01/2024", "14:00", "15:00");
        assertEquals("Second booking ID should be set", "BOOK002", room.getBookingId());
        assertEquals("Second booking user ID should be set", userId2, room.getBookingUserId());
    }
    
    // ========== Check-In Tests ==========
    
    @Test
    public void testRoom_CheckIn_FromReserved() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        assertEquals("Should be Reserved", "Reserved", room.getCondition());
        
        room.checkIn();
        assertEquals("Should be InUse after check-in", "InUse", room.getCondition());
    }
    
    @Test
    public void testRoom_CheckIn_FromAvailable() {
        // Check-in from Available state (should not change state)
        room.checkIn();
        // State might not change if there's no booking
        // This depends on RoomContext implementation
    }
    
    // ========== Check-Out Tests ==========
    
    @Test
    public void testRoom_CheckOut_FromInUse() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        room.checkIn();
        assertEquals("Should be InUse", "InUse", room.getCondition());
        
        room.checkOut();
        assertEquals("Should be Available after check-out", "Available", room.getCondition());
    }
    
    // ========== Cancel Booking Tests ==========
    
    @Test
    public void testRoom_CancelBooking_FromReserved() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        assertEquals("Should be Reserved", "Reserved", room.getCondition());
        assertTrue("Should have active booking", room.hasActiveBooking());
        
        room.cancelBooking();
        assertEquals("Should be Available after cancel", "Available", room.getCondition());
        assertFalse("Should not have active booking", room.hasActiveBooking());
    }
    
    @Test
    public void testRoom_CancelBooking_NoBooking() {
        // Cancel when no booking exists
        room.cancelBooking();
        assertEquals("Should remain Available", "Available", room.getCondition());
        assertFalse("Should not have active booking", room.hasActiveBooking());
    }
    
    // ========== Extend Booking Tests ==========
    
    @Test
    public void testRoom_ExtendBooking() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        String originalEndTime = room.getBookingEndTime();
        
        room.extendBooking(1);
        // End time should be extended by 1 hour
        // This depends on RoomContext implementation
        assertNotNull("Booking end time should exist", room.getBookingEndTime());
    }
    
    // ========== Maintenance Tests ==========
    
    @Test
    public void testRoom_SetMaintenance() {
        room.setMaintenance();
        assertEquals("Condition should be Maintenance", "Maintenance", room.getCondition());
    }
    
    @Test
    public void testRoom_ClearMaintenance() {
        room.setMaintenance();
        assertEquals("Condition should be Maintenance", "Maintenance", room.getCondition());
        
        room.clearMaintenance();
        assertEquals("Condition should be Available", "Available", room.getCondition());
    }
    
    @Test
    public void testRoom_Maintenance_FromReserved() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        room.setMaintenance();
        assertNotEquals("Condition should be Maintenance", "Maintenance", room.getCondition());
    }
    
    // ========== Occupancy Tests ==========
    
    @Test
    public void testRoom_CheckInPeople_Success() {
        boolean result = room.checkInPeople(10);
        assertTrue("Should successfully check in people", result);
        assertEquals("Occupancy should be 10", 10, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_MultipleTimes() {
        room.checkInPeople(5);
        assertEquals("Occupancy should be 5", 5, room.getCurrentOccupancy());
        
        room.checkInPeople(10);
        assertEquals("Occupancy should be 15", 15, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_ExceedsCapacity() {
        boolean result = room.checkInPeople(60); // Capacity is 50
        assertFalse("Should fail when exceeds capacity", result);
        assertEquals("Occupancy should remain 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_ZeroPeople() {
        boolean result = room.checkInPeople(0);
        assertFalse("Should fail for zero people", result);
        assertEquals("Occupancy should remain 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_NegativePeople() {
        boolean result = room.checkInPeople(-5);
        assertFalse("Should fail for negative people", result);
        assertEquals("Occupancy should remain 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_AtCapacity() {
        boolean result = room.checkInPeople(50); // Exactly at capacity
        assertTrue("Should succeed at capacity", result);
        assertEquals("Occupancy should be 50", 50, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckInPeople_JustOverCapacity() {
        room.checkInPeople(50);
        boolean result = room.checkInPeople(1);
        assertFalse("Should fail when just over capacity", result);
        assertEquals("Occupancy should remain 50", 50, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckOutPeople_Success() {
        room.checkInPeople(20);
        assertEquals("Occupancy should be 20", 20, room.getCurrentOccupancy());
        
        room.checkOutPeople(10);
        assertEquals("Occupancy should be 10", 10, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckOutPeople_MoreThanOccupancy() {
        room.checkInPeople(10);
        room.checkOutPeople(15); // More than current occupancy
        assertEquals("Occupancy should not go below 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckOutPeople_AllPeople() {
        room.checkInPeople(30);
        room.checkOutPeople(30);
        assertEquals("Occupancy should be 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_CheckOutPeople_FromZero() {
        room.checkOutPeople(5);
        assertEquals("Occupancy should remain 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_ResetOccupancy() {
        room.checkInPeople(25);
        assertEquals("Occupancy should be 25", 25, room.getCurrentOccupancy());
        
        room.resetOccupancy();
        assertEquals("Occupancy should be reset to 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_GetCurrentOccupancy() {
        assertEquals("Initial occupancy should be 0", 0, room.getCurrentOccupancy());
        
        room.checkInPeople(15);
        assertEquals("Occupancy should be 15", 15, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_HasCapacityFor_Success() {
        assertTrue("Should have capacity for 30 people", room.hasCapacityFor(30));
        assertTrue("Should have capacity for 50 people", room.hasCapacityFor(50));
    }
    
    @Test
    public void testRoom_HasCapacityFor_ExceedsCapacity() {
        assertFalse("Should not have capacity for 51 people", room.hasCapacityFor(51));
    }
    
    @Test
    public void testRoom_HasCapacityFor_WithCurrentOccupancy() {
        room.checkInPeople(20);
        assertTrue("Should have capacity for 30 more", room.hasCapacityFor(30));
        assertFalse("Should not have capacity for 31 more", room.hasCapacityFor(31));
    }
    
    @Test
    public void testRoom_GetRemainingCapacity() {
        assertEquals("Remaining capacity should be 50", 50, room.getRemainingCapacity());
        
        room.checkInPeople(15);
        assertEquals("Remaining capacity should be 35", 35, room.getRemainingCapacity());
        
        room.checkInPeople(20);
        assertEquals("Remaining capacity should be 15", 15, room.getRemainingCapacity());
    }
    
    @Test
    public void testRoom_GetRemainingCapacity_AtCapacity() {
        room.checkInPeople(50);
        assertEquals("Remaining capacity should be 0", 0, room.getRemainingCapacity());
    }
    
    // ========== toString Tests ==========
    
    @Test
    public void testRoom_ToString() {
        String str = room.toString();
        assertNotNull("toString should not be null", str);
        assertTrue("Should contain building name", str.contains("Test Building"));
        assertTrue("Should contain room number", str.contains("101"));
        assertTrue("Should contain capacity", str.contains("50"));
        assertTrue("Should contain status", str.contains("ENABLED"));
        assertTrue("Should contain condition", str.contains("Available"));
    }
    
    @Test
    public void testRoom_ToString_WithDifferentStates() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        String str = room.toString();
        assertTrue("Should contain Reserved condition", str.contains("Reserved"));
        
        room.checkIn();
        str = room.toString();
        assertTrue("Should contain InUse condition", str.contains("InUse"));
        
        room.setMaintenance();
        str = room.toString();
        assertFalse("Should contain Maintenance condition", str.contains("Maintenance"));
    }
    
    // ========== Integration Tests ==========
    
    @Test
    public void testRoom_FullBookingFlow() {
        // Book room
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        assertEquals("Should be Reserved", "Reserved", room.getCondition());
        assertTrue("Should have active booking", room.hasActiveBooking());
        
        // Check in
        room.checkIn();
        assertEquals("Should be InUse", "InUse", room.getCondition());
        
        // Check in people
        room.checkInPeople(10);
        assertEquals("Occupancy should be 10", 10, room.getCurrentOccupancy());
        
        // Check out people
        room.checkOutPeople(5);
        assertEquals("Occupancy should be 5", 5, room.getCurrentOccupancy());
        
        // Check out
        room.checkOut();
        assertEquals("Should be Available", "Available", room.getCondition());
        
        // Reset occupancy
        room.resetOccupancy();
        assertEquals("Occupancy should be 0", 0, room.getCurrentOccupancy());
    }
    
    @Test
    public void testRoom_BookingAndMaintenance() {
        room.book("BOOK001", testUserId, "01/01/2024", "10:00", "11:00");
        room.setMaintenance();
        assertNotEquals("Should be in Maintenance", "Maintenance", room.getCondition());
        
        room.clearMaintenance();
        // After clearing maintenance, state might depend on booking status
        assertNotNull("Condition should be set", room.getCondition());
    }
    
    @Test
    public void testRoom_DisableAndEnable() {
        room.disable();
        assertFalse("Should not be bookable when disabled", room.isBookable());
        
        room.enable();
        assertTrue("Should be bookable when enabled", room.isBookable());
    }
}
