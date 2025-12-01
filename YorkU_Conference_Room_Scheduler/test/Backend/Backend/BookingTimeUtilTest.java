package Backend;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingTimeUtilTest {
    
    private String getFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return futureDate.format(formatter);
    }
    
    private String getPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return pastDate.format(formatter);
    }
    
    @Test
    public void testHasStartTimePassed_PastDate() {
        String pastDate = getPastDate();
        boolean result = BookingTimeUtil.hasStartTimePassed(pastDate, "10:00");
        assertTrue("Start time should have passed for past date", result);
    }
    
    @Test
    public void testHasStartTimePassed_FutureDate() {
        String futureDate = getFutureDate();
        boolean result = BookingTimeUtil.hasStartTimePassed(futureDate, "10:00");
        assertFalse("Start time should not have passed for future date", result);
    }
    
    @Test
    public void testHasEndTimePassed_PastDate() {
        String pastDate = getPastDate();
        boolean result = BookingTimeUtil.hasEndTimePassed(pastDate, "11:00");
        assertTrue("End time should have passed for past date", result);
    }
    
    @Test
    public void testHasEndTimePassed_NullEndTime() {
        String futureDate = getFutureDate();
        boolean result = BookingTimeUtil.hasEndTimePassed(futureDate, null);
        assertFalse("Should return false for null end time", result);
    }
    
    @Test
    public void testIsPreStartState_ReservedFuture() throws Exception {
        User user = new Student("test@yorku.ca", "pass", "12345678");
        String futureDate = getFutureDate();
        Booking booking = new Booking("TIME001", user, 1, 20.0, "101", futureDate, "10:00", "11:00");
        booking.setStatus("Reserved");
        
        boolean result = BookingTimeUtil.isPreStartState(booking);
        assertTrue("Should be in pre-start state", result);
    }
    
    @Test
    public void testIsPreStartState_NullBooking() {
        boolean result = BookingTimeUtil.isPreStartState(null);
        assertFalse("Should return false for null booking", result);
    }
    
    @Test
    public void testCanCancelBooking_Reserved() throws Exception {
        User user = new Student("test@yorku.ca", "pass", "12345678");
        String futureDate = getFutureDate();
        Booking booking = new Booking("TIME002", user, 1, 20.0, "101", futureDate, "10:00", "11:00");
        booking.setStatus("Reserved");
        
        boolean result = BookingTimeUtil.canCancelBooking(booking);
        assertTrue("Should be able to cancel Reserved booking", result);
    }
    
    @Test
    public void testCanCancelBooking_InUse() throws Exception {
        User user = new Student("test@yorku.ca", "pass", "12345678");
        String futureDate = getFutureDate();
        Booking booking = new Booking("TIME003", user, 1, 20.0, "101", futureDate, "10:00", "11:00");
        booking.setStatus("InUse");
        
        boolean result = BookingTimeUtil.canCancelBooking(booking);
        assertFalse("Should not be able to cancel InUse booking", result);
    }
}