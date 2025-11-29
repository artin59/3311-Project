package Backend;

import org.junit.Test;
import static org.junit.Assert.*;

public class BookingTest {

    // -------------------------
    // Test-only concrete User subclass
    // -------------------------
    private static class MockUser extends User {
        public MockUser(String email, String password) {
            super(email, password);
        }

        @Override
        public String getOrgID() {
            return "ORG123";
        }

        @Override
        public double getHourlyRate() {
            return 15.0;
        }

        @Override
        public boolean requiresVerfication() {
            return false;
        }

		@Override
		public String getAccountType() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    // -------------------------
    // Actual Tests
    // -------------------------

    @Test
    public void testConstructorAndGetters() {
        MockUser user = new MockUser("john@example.com", "pass123");

        Booking b = new Booking(
                "B001",
                user,
                3,
                10.0,
                "R101",
                "2025-01-01",
                "10:00",
                "13:00"
        );

        assertEquals("B001", b.getBookingId());
        assertEquals(user, b.getUser());
        assertEquals(3, b.getHours());
        assertEquals(10.0, b.getRate(), 0.0001);
        assertEquals(30.0, b.getTotalCost(), 0.0001);
        assertEquals("R101", b.getRoomNumber());
        assertEquals("2025-01-01", b.getBookingDate());
        assertEquals("10:00", b.getBookingStartTime());
        assertEquals("13:00", b.getBookingEndTime());
        assertEquals("Reserved", b.getStatus());
    }

    @Test
    public void testSetHoursRecalculatesTotalCost() {
        Booking b = sampleBooking();
        b.setHours(5);
        assertEquals(5, b.getHours());
        assertEquals(5 * b.getRate(), b.getTotalCost(), 0.0001);
    }

    @Test
    public void testSetRateRecalculatesTotalCost() {
        Booking b = sampleBooking();
        b.setRate(20.0);
        assertEquals(20.0, b.getRate(), 0.0001);
        assertEquals(b.getHours() * 20.0, b.getTotalCost(), 0.0001);
    }

    @Test
    public void testSetTotalCostDirect() {
        Booking b = sampleBooking();
        b.setTotalCost(50.55);
        assertEquals(50.55, b.getTotalCost(), 0.0001);
    }

    @Test
    public void testSetStatus() {
        Booking b = sampleBooking();
        b.setStatus("Completed");
        assertEquals("Completed", b.getStatus());
    }

    @Test
    public void testSetRoomNumber() {
        Booking b = sampleBooking();
        b.setRoomNumber("R202");
        assertEquals("R202", b.getRoomNumber());
    }

    @Test
    public void testSetBookingDate() {
        Booking b = sampleBooking();
        b.setBookingDate("2030-12-25");
        assertEquals("2030-12-25", b.getBookingDate());
    }

    @Test
    public void testSetStartTime() {
        Booking b = sampleBooking();
        b.setBookingStartTime("11:30");
        assertEquals("11:30", b.getBookingStartTime());
    }

    @Test
    public void testSetEndTime() {
        Booking b = sampleBooking();
        b.setBookingEndTime("17:45");
        assertEquals("17:45", b.getBookingEndTime());
    }

    // Helper to avoid repetition
    private Booking sampleBooking() {
        MockUser user = new MockUser("john@example.com", "pass123");

        return new Booking(
                "B123",
                user,
                3,
                10.0,
                "R101",
                "2025-01-01",
                "10:00",
                "13:00"
        );
    }
}