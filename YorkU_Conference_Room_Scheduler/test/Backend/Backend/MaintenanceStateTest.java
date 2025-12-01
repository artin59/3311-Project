package Backend;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;

public class MaintenanceStateTest {
    private MaintenanceState state;
    private RoomContext context;
    
    @Before
    public void setUp() {
        state = MaintenanceState.getInstance();
        UUID roomId = UUID.randomUUID();
        context = new RoomContext(roomId);
        context.setState(state);
    }
    
    @Test
    public void testGetInstance() {
        MaintenanceState instance1 = MaintenanceState.getInstance();
        MaintenanceState instance2 = MaintenanceState.getInstance();
        assertSame("Should return same instance", instance1, instance2);
    }
    
    @Test
    public void testGetStateName() {
        assertEquals("State name should be Maintenance", "Maintenance", state.getStateName());
    }
    
    @Test
    public void testCanBook() {
        assertFalse("Maintenance state should not allow booking", state.canBook());
    }
    
    @Test
    public void testCancelBooking() {
        context.setBookingInfo("BOOK001", UUID.randomUUID(), "2024-01-15", "10:00", "11:00");
        state.cancelBooking(context);
        assertNull("Booking info should be cleared", context.getBookingId());
    }
    
    @Test
    public void testClearMaintenance() {
        state.clearMaintenance(context);
        assertEquals("State should change to Available", "Available", context.getStateName());
    }
}