package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;

public class ReservedStateTest {
    private ReservedState state;
    private RoomContext context;
    
    @Before
    public void setUp() {
        state = ReservedState.getInstance();
        UUID roomId = UUID.randomUUID();
        context = new RoomContext(roomId);
        context.setState(state);
    }
    
    @Test
    public void testGetInstance() {
        ReservedState instance1 = ReservedState.getInstance();
        ReservedState instance2 = ReservedState.getInstance();
        assertSame("Should return same instance", instance1, instance2);
    }
    
    @Test
    public void testGetStateName() {
        assertEquals("State name should be Reserved", "Reserved", state.getStateName());
    }
    
    @Test
    public void testCanBook() {
        assertFalse("Reserved state should not allow booking", state.canBook());
    }
    
    @Test
    public void testCheckIn() {
        state.checkIn(context);
        assertEquals("State should change to InUse", "InUse", context.getStateName());
    }
    
    @Test
    public void testCancelBooking() {
        context.setBookingInfo("BOOK001", UUID.randomUUID(), "2024-01-15", "10:00", "11:00");
        state.cancelBooking(context);
        assertEquals("State should change to Available", "Available", context.getStateName());
        assertNull("Booking info should be cleared", context.getBookingId());
    }
    
    @Test
    public void testExtendBooking() {
        // Just verify it doesn't throw
        state.extendBooking(context, 1);
        assertEquals("State should remain Reserved", "Reserved", context.getStateName());
    }
    
    @Test
    public void testTriggerNoShow() {
        state.triggerNoShow(context);
        assertEquals("State should change to NoShow", "NoShow", context.getStateName());
    }
}