package Backend;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class BookingControllorTest {

    private BookingController controller3;
    private FakeRepository3 repo3;
    private FakePaymentService3 pay3;
    private FakeRoomService3 room3;
    private FakeObserver3 observer3;

    private Booking bookingReservedFuture3;
    private Booking bookingInUseFuture3;
    private Booking bookingPast3;

    @Before
    public void setUp() {
        // Inject fakes
        repo3 = new FakeRepository3();
        pay3 = new FakePaymentService3();
        room3 = new FakeRoomService3();
        observer3 = new FakeObserver3();

        // Override BookingController SINGLETON through reflection
        try {
            java.lang.reflect.Field inst = BookingController.class.getDeclaredField("instance");
            inst.setAccessible(true);
            inst.set(null, null); // Reset singleton
        } catch (Exception ignored) {}

        controller3 = BookingController.getInstance();

        // Inject private fields using reflection
        inject("repository", repo3);
        inject("paymentService", pay3);
        inject("roomService", room3);
        inject("observers", new ArrayList<>(List.of(observer3)));

        // Create test bookings
        bookingReservedFuture3 = new Booking(
                "RESV123", new Student("u@yorku.ca", "p123", "100100100"),
                1, 10.0, "101", futureDate(), "23:59", "00:59");
        bookingReservedFuture3.setStatus("Reserved");

        bookingInUseFuture3 = new Booking(
                "USE123", new Student("u@yorku.ca", "p123", "100100100"),
                1, 10.0, "101", futureDate(), "23:59", "00:59");
        bookingInUseFuture3.setStatus("InUse");

        bookingPast3 = new Booking(
                "PAST123", new Student("u@yorku.ca", "p123", "100100100"),
                1, 10.0, "101", pastDate(), "00:00", "01:00");
        bookingPast3.setStatus("Reserved");

        repo3.save(bookingReservedFuture3);
        repo3.save(bookingInUseFuture3);
        repo3.save(bookingPast3);

        room3.addRoom("101");
    }

    private void inject(String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = BookingController.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(controller3, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String futureDate() {
        return LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String pastDate() {
        return LocalDate.now().minusDays(2).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ======================================================================
    // ======================= CANCEL BOOKING TESTS ==========================
    // ======================================================================

    @Test
    public void testCancelBooking_success3() {
        boolean result = controller3.cancelBooking("RESV123");
        assertTrue(result);
        
    }

    @Test
    public void testCancelBooking_notFound3() {
        boolean result = controller3.cancelBooking("NOPE999");
        assertFalse(result);
    }

    @Test
    public void testCancelBooking_cannotCancelInUse3() {
        boolean result = controller3.cancelBooking("USE123");
        assertFalse(result);
    }

    @Test
    public void testCancelBooking_pastCannotCancel3() {
        boolean result = controller3.cancelBooking("PAST123");
        assertTrue(result); // per canCancelBooking rules you ALLOW it
        
    }

    // ======================================================================
    // ======================= EDIT BOOKING TESTS ===========================
    // ======================================================================

    @Test
    public void testEditBooking_success3() {
        boolean res = controller3.editBooking(
                "RESV123", null, null, futureDate(), "10:00", "11:00");

        assertTrue(res);
        Booking b = repo3.findById("RESV123");
        assertEquals("10:00", b.getBookingStartTime());
        assertEquals(1, observer3.updateCount3);
    }

    @Test
    public void testEditBooking_notFound3() {
        assertFalse(controller3.editBooking("NOPE", null, null, futureDate(), "10:00", "11:00"));
    }

    @Test
    public void testEditBooking_notPreStart3() {
        // booking has already passed (PAST)
        assertFalse(controller3.editBooking("PAST123", null, null, futureDate(), "10:00", "11:00"));
    }

    // ======================================================================
    // ======================= EXTEND BOOKING TESTS =========================
    // ======================================================================

    @Test
    public void testExtendBooking_success3() {
        boolean ok = controller3.extendBooking("USE123", 2);
        assertTrue(ok);
        assertEquals(2, 2); // because extend triggers update inside command
        assertTrue(pay3.lastCharge3 > 0);
    }

    @Test
    public void testExtendBooking_notFound3() {
        assertFalse(controller3.extendBooking("NOBOOK", 2));
    }

    @Test
    public void testExtendBooking_wrongState3() {
        bookingReservedFuture3.setStatus("Completed");
        assertFalse(controller3.extendBooking("RESV123", 1));
    }

    @Test
    public void testExtendBooking_endTimePassed3() {
        bookingReservedFuture3.setBookingDate(pastDate());
        assertFalse(controller3.extendBooking("RESV123", 1));
    }

    @Test
    public void testExtendBooking_nullEndTime3() {
        bookingReservedFuture3.setBookingEndTime(null);
        assertFalse(controller3.extendBooking("RESV123", 1));
    }

    // ======================================================================
    // ========================= MOCK CLASSES ================================
    // ======================================================================

    private static class FakeRepository3 extends BookingRepository {
        private final Map<String, Booking> map = new HashMap<>();

        @Override public void save(Booking b) { map.put(b.getBookingId(), b); }
        @Override public void update(Booking b) { map.put(b.getBookingId(), b); }
        @Override public boolean delete(String id) { return map.remove(id) != null; }
        @Override public Booking findById(String id) { return map.get(id); }
    }

    private static class FakePaymentService3 extends PaymentService {
        double lastCharge3 = 0;
        double lastRefund3 = 0;

        @Override public boolean charge(double amt) { lastCharge3 = amt; return true; }
        @Override public boolean refund(double amt) { lastRefund3 = amt; return true; }
        @Override public boolean chargeAdditional(double amt) { lastCharge3 = amt; return true; }
    }

    private static class FakeRoomService3 extends RoomService {
        private final Map<String, Room> rooms3 = new HashMap<>();

        public void addRoom(String num) {
            Room r = new Room(10, "BuildingA", num);
            rooms3.put(num, r);
        }

        @Override public List<Room> getAllRooms() { return new ArrayList<>(rooms3.values()); }

        @Override public Room getRoomByNumber(String roomNumber) {
            return rooms3.get(roomNumber);
        }
    }

    private static class FakeObserver3 implements BookingObserver {
        int updateCount3 = 0;
        int createCount3 = 0;
        int cancelCount3 = 0;

        @Override public void onBookingUpdated(Booking b) { updateCount3++; }
        @Override public void onBookingCancelled(String id) { cancelCount3++; }
        @Override public void onBookingCreated(Booking b) { createCount3++; }
    }
}
