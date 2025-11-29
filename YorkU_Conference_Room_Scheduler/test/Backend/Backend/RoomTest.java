package Backend;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;

/**
 * Test class for Room
 * Tests room creation, state management, booking, and room operations
 */
public class RoomTest {

    // Test 1: Create room with valid parameters
    @Test
    public void testCreateRoom() {
        Room room = new Room(50, "Building A", "R101");
        
        assertNotNull(room);
        assertEquals(50, room.getCapacity());
        assertEquals("Building A", room.getBuildingName());
        assertEquals("R101", room.getRoomNumber());
    }

    // Test 2: Room initial status is ENABLED
    @Test
    public void testRoomInitialStatusIsEnabled() {
        Room room = new Room(50, "Building A", "R101");
        
        assertEquals("ENABLED", room.getStatus());
    }

    // Test 3: Room initial condition is Available
    @Test
    public void testRoomInitialConditionIsAvailable() {
        Room room = new Room(50, "Building A", "R101");
        
        assertEquals("Available", room.getCondition());
    }

    // Test 4: Room is bookable when enabled and available
    @Test
    public void testRoomIsBookableWhenEnabledAndAvailable() {
        Room room = new Room(50, "Building A", "R101");
        
        assertTrue(room.isBookable());
    }

    // Test 5: Room is not bookable when disabled
    @Test
    public void testRoomIsNotBookableWhenDisabled() {
        Room room = new Room(50, "Building A", "R101");
        room.disable();
        
        assertFalse(room.isBookable());
    }

    // Test 6: Enable room
    @Test
    public void testEnableRoom() {
        Room room = new Room(50, "Building A", "R101");
        room.disable();
        room.enable();
        
        assertEquals("ENABLED", room.getStatus());
    }

    // Test 7: Disable room
    @Test
    public void testDisableRoom() {
        Room room = new Room(50, "Building A", "R101");
        room.disable();
        
        assertEquals("DISABLED", room.getStatus());
    }

    // Test 8: Book room
    @Test
    public void testBookRoom() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        assertEquals("Reserved", room.getCondition());
        assertEquals("B001", room.getBookingId());
    }

    // Test 9: Cannot book disabled room
    @Test
    public void testCannotBookDisabledRoom() {
        Room room = new Room(50, "Building A", "R101");
        room.disable();
        UUID userId = UUID.randomUUID();
        
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        // Room should remain in Available state
        assertEquals("Available", room.getCondition());
    }

    // Test 10: Check in to room
    @Test
    public void testCheckInToRoom() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        room.checkIn();
        
        assertEquals("InUse", room.getCondition());
    }

    // Test 11: Check out from room
    @Test
    public void testCheckOutFromRoom() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        room.checkIn();
        
        room.checkOut();
        
        assertEquals("Available", room.getCondition());
    }

    // Test 12: Cancel booking
    @Test
    public void testCancelBooking() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        room.cancelBooking();
        
        assertEquals("Available", room.getCondition());
    }

    // Test 13: Set maintenance
    @Test
    public void testSetMaintenance() {
        Room room = new Room(50, "Building A", "R101");
        
        room.setMaintenance();
        
        assertEquals("Maintenance", room.getCondition());
    }

    // Test 14: Clear maintenance
    @Test
    public void testClearMaintenance() {
        Room room = new Room(50, "Building A", "R101");
        room.setMaintenance();
        
        room.clearMaintenance();
        
        assertEquals("Available", room.getCondition());
    }

    // Test 15: Extend booking
    @Test
    public void testExtendBooking() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        room.checkIn();
        
        room.extendBooking(2);
        
        // Room should still be in use
        assertEquals("InUse", room.getCondition());
    }

    // Test 16: Get room location
    @Test
    public void testGetRoomLocation() {
        Room room = new Room(50, "Building A", "R101");
        
        assertEquals("Building A - Room R101", room.getLocation());
    }

    // Test 17: Room has active booking
    @Test
    public void testRoomHasActiveBooking() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        assertTrue(room.hasActiveBooking());
    }

    // Test 18: Room does not have active booking initially
    @Test
    public void testRoomDoesNotHaveActiveBookingInitially() {
        Room room = new Room(50, "Building A", "R101");
        
        assertFalse(room.hasActiveBooking());
    }

    // Test 19: Get booking details
    @Test
    public void testGetBookingDetails() {
        Room room = new Room(50, "Building A", "R101");
        UUID userId = UUID.randomUUID();
        room.book("B001", userId, "01/01/2024", "10:00", "12:00");
        
        assertEquals("B001", room.getBookingId());
        assertEquals(userId, room.getBookingUserId());
        assertEquals("01/01/2024", room.getBookingDate());
        assertEquals("10:00", room.getBookingStartTime());
        assertEquals("12:00", room.getBookingEndTime());
    }

    // Test 20: Room with different capacity
    @Test
    public void testRoomWithDifferentCapacity() {
        Room room1 = new Room(25, "Building A", "R101");
        Room room2 = new Room(100, "Building B", "R201");
        
        assertEquals(25, room1.getCapacity());
        assertEquals(100, room2.getCapacity());
    }

    // Test 21: Room ID is unique
    @Test
    public void testRoomIdIsUnique() {
        Room room1 = new Room(50, "Building A", "R101");
        Room room2 = new Room(50, "Building A", "R101");
        
        assertNotEquals(room1.getRoomId(), room2.getRoomId());
    }

    // Test 22: Room toString
    @Test
    public void testRoomToString() {
        Room room = new Room(50, "Building A", "R101");
        String roomString = room.toString();
        
        assertNotNull(roomString);
        assertTrue(roomString.contains("Building A"));
        assertTrue(roomString.contains("R101"));
        assertTrue(roomString.contains("50"));
    }

    // Test 23: Room context is not null
    @Test
    public void testRoomContextIsNotNull() {
        Room room = new Room(50, "Building A", "R101");
        
        assertNotNull(room.getRoomContext());
    }

    // Test 24: Load room from CSV constructor
    @Test
    public void testLoadRoomFromCSV() {
        UUID roomId = UUID.randomUUID();
        Room room = new Room(roomId, 50, "Building A", "R101", "ENABLED", "Available");
        
        assertEquals(roomId, room.getRoomId());
        assertEquals(50, room.getCapacity());
        assertEquals("Building A", room.getBuildingName());
        assertEquals("R101", room.getRoomNumber());
        assertEquals("ENABLED", room.getStatus());
        assertEquals("Available", room.getCondition());
    }
}


