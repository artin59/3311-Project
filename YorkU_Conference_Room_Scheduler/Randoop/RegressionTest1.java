import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest1 {

    public static boolean debug = false;

    @Test
    public void test501() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test501");
        Backend.Room room3 = new Backend.Room((int) (byte) -1, "", "Organization ID must be exactly 9 characters.");
        java.lang.String str4 = room3.getBookingId();
        room3.cancelBooking();
        java.lang.String str6 = room3.toString();
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(str4);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "Room [ - Room Organization ID must be exactly 9 characters., Capacity: -1, Status: ENABLED, Condition: Available]" + "'", str6.equals("Room [ - Room Organization ID must be exactly 9 characters., Capacity: -1, Status: ENABLED, Condition: Available]"));
    }

    @Test
    public void test502() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test502");
        Backend.User user1 = null;
        Backend.Booking booking8 = new Backend.Booking("", user1, (int) ' ', 0.0d, "Please enter a valid email address.", "hi!", "Organization ID must be exactly 9 characters.", "");
        booking8.setBookingEndTime("B8EB7DE1A");
    }

    @Test
    public void test503() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test503");
        Backend.RoomService roomService0 = new Backend.RoomService();
        java.util.List<Backend.Room> roomList2 = roomService0.getRoomsByCondition("hi!");
        java.util.List<Backend.Room> roomList4 = roomService0.getRoomsByStatus("Organization ID must be exactly 9 characters.");
        Backend.RoomService roomService5 = new Backend.RoomService();
        java.util.List<Backend.Room> roomList7 = roomService5.getRoomsByStatus("Please enter a valid email address.");
        Backend.RoomService roomService8 = new Backend.RoomService();
        java.util.List<Backend.Room> roomList10 = roomService8.getRoomsByStatus("Please enter a valid email address.");
        java.util.UUID uUID11 = null;
        boolean boolean12 = roomService8.processNoShow(uUID11);
        java.util.UUID uUID13 = null;
        java.util.UUID uUID15 = null;
        boolean boolean19 = roomService8.bookRoom(uUID13, "Please enter a valid email address.", uUID15, "", "", "Available");
        java.util.List<Backend.Room> roomList20 = roomService8.getAllRooms();
        Backend.ExternalPartnerPricingStrategy externalPartnerPricingStrategy21 = new Backend.ExternalPartnerPricingStrategy();
        Backend.Student student25 = new Backend.Student("Please enter a valid email address.", "Please enter a valid email address.", "");
        double double26 = externalPartnerPricingStrategy21.calculateRate((Backend.User) student25);
        Backend.ReservationSystem reservationSystem27 = Backend.ReservationSystem.getInstance();
        Backend.User user28 = null;
        Backend.Booking booking31 = reservationSystem27.createBooking(user28, 100, (double) 10.0f);
        Backend.User user32 = null;
        Backend.Booking booking35 = reservationSystem27.createBooking(user32, (int) (short) 100, (double) 'a');
        Backend.ExternalPartnerPricingStrategy externalPartnerPricingStrategy36 = new Backend.ExternalPartnerPricingStrategy();
        Backend.Student student40 = new Backend.Student("Please enter a valid email address.", "Please enter a valid email address.", "");
        double double41 = externalPartnerPricingStrategy36.calculateRate((Backend.User) student40);
        double double42 = reservationSystem27.calculateHourlyRate((Backend.User) student40);
        double double43 = externalPartnerPricingStrategy21.calculateRate((Backend.User) student40);
        java.util.UUID uUID44 = student40.getAccountId();
        Backend.ExternalPartnerPricingStrategy externalPartnerPricingStrategy51 = new Backend.ExternalPartnerPricingStrategy();
        Backend.Student student55 = new Backend.Student("Please enter a valid email address.", "Please enter a valid email address.", "");
        double double56 = externalPartnerPricingStrategy51.calculateRate((Backend.User) student55);
        Backend.ReservationSystem reservationSystem57 = Backend.ReservationSystem.getInstance();
        Backend.User user58 = null;
        Backend.Booking booking61 = reservationSystem57.createBooking(user58, 100, (double) 10.0f);
        Backend.User user62 = null;
        Backend.Booking booking65 = reservationSystem57.createBooking(user62, (int) (short) 100, (double) 'a');
        Backend.ExternalPartnerPricingStrategy externalPartnerPricingStrategy66 = new Backend.ExternalPartnerPricingStrategy();
        Backend.Student student70 = new Backend.Student("Please enter a valid email address.", "Please enter a valid email address.", "");
        double double71 = externalPartnerPricingStrategy66.calculateRate((Backend.User) student70);
        double double72 = reservationSystem57.calculateHourlyRate((Backend.User) student70);
        double double73 = externalPartnerPricingStrategy51.calculateRate((Backend.User) student70);
        java.util.UUID uUID74 = student70.getAccountId();
        Backend.Room room78 = new Backend.Room(uUID44, 10, "", "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.", "Please enter a valid email address.", "BFAF06322", "", uUID74, "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.", "Chief Event Coordinator", "Admin");
        java.util.UUID uUID79 = null;
        Backend.AvailableState availableState80 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext81 = null;
        availableState80.checkOut(roomContext81);
        Backend.RoomContext roomContext83 = new Backend.RoomContext(uUID79, (Backend.RoomState) availableState80);
        Backend.RoomContext roomContext84 = new Backend.RoomContext(uUID74, (Backend.RoomState) availableState80);
        Backend.Room room85 = roomService8.getRoomById(uUID74);
        boolean boolean86 = roomService5.enableRoom(uUID74);
        Backend.Room room87 = roomService0.getRoomById(uUID74);
        Backend.Room room89 = roomService0.getRoomByNumber("Please enter a valid email address. - Room Organization ID must be exactly 9 characters.");
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(roomList2);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(roomList4);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(roomList7);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(roomList10);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", boolean12 == false);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", boolean19 == false);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(roomList20);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double26 + "' != '" + 50.0d + "'", double26 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(reservationSystem27);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(booking31);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(booking35);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double41 + "' != '" + 50.0d + "'", double41 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double42 + "' != '" + 20.0d + "'", double42 == 20.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double43 + "' != '" + 50.0d + "'", double43 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(uUID44);
        // Regression assertion (captures the current behavior of the code)
// flaky:         org.junit.Assert.assertEquals(uUID44.toString(), "75a0787b-a774-44c8-8bcb-c927ce2ec7fb");
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double56 + "' != '" + 50.0d + "'", double56 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(reservationSystem57);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(booking61);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(booking65);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double71 + "' != '" + 50.0d + "'", double71 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double72 + "' != '" + 20.0d + "'", double72 == 20.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + double73 + "' != '" + 50.0d + "'", double73 == 50.0d);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(uUID74);
        // Regression assertion (captures the current behavior of the code)
// flaky:         org.junit.Assert.assertEquals(uUID74.toString(), "c94c2aac-5b60-4f3e-b813-b21686681244");
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState80);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(room85);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + false + "'", boolean86 == false);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(room87);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(room89);
    }

    @Test
    public void test504() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test504");
        Backend.AvailableState availableState0 = Backend.AvailableState.getInstance();
        Backend.AvailableState availableState1 = Backend.AvailableState.getInstance();
        java.util.UUID uUID2 = null;
        Backend.AvailableState availableState3 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext4 = null;
        availableState3.checkOut(roomContext4);
        Backend.RoomContext roomContext6 = new Backend.RoomContext(uUID2, (Backend.RoomState) availableState3);
        availableState1.cancelBooking(roomContext6);
        availableState0.checkOut(roomContext6);
        java.lang.String str9 = roomContext6.getBookingStartTime();
        roomContext6.cancelBooking();
        java.lang.String str11 = roomContext6.getBookingEndTime();
        java.util.UUID uUID13 = null;
        roomContext6.setBookingInfo("PENDING_VERIFICATION", uUID13, "BA9E19013", "BFAF06322", "Organization ID must be exactly 9 characters.");
        Backend.AvailableState availableState18 = Backend.AvailableState.getInstance();
        java.util.UUID uUID19 = null;
        Backend.AvailableState availableState20 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext21 = null;
        availableState20.checkOut(roomContext21);
        Backend.RoomContext roomContext23 = new Backend.RoomContext(uUID19, (Backend.RoomState) availableState20);
        java.lang.String str24 = roomContext23.getBookingEndTime();
        availableState18.checkIn(roomContext23);
        roomContext6.setState((Backend.RoomState) availableState18);
        Backend.AvailableState availableState27 = Backend.AvailableState.getInstance();
        java.util.UUID uUID28 = null;
        Backend.AvailableState availableState29 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext30 = null;
        availableState29.checkOut(roomContext30);
        Backend.RoomContext roomContext32 = new Backend.RoomContext(uUID28, (Backend.RoomState) availableState29);
        availableState27.cancelBooking(roomContext32);
        availableState18.setMaintenance(roomContext32);
        Backend.AvailableState availableState35 = Backend.AvailableState.getInstance();
        Backend.AvailableState availableState36 = Backend.AvailableState.getInstance();
        java.util.UUID uUID37 = null;
        Backend.AvailableState availableState38 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext39 = null;
        availableState38.checkOut(roomContext39);
        Backend.RoomContext roomContext41 = new Backend.RoomContext(uUID37, (Backend.RoomState) availableState38);
        availableState36.cancelBooking(roomContext41);
        availableState35.checkOut(roomContext41);
        Backend.AvailableState availableState44 = Backend.AvailableState.getInstance();
        java.util.UUID uUID45 = null;
        Backend.AvailableState availableState46 = Backend.AvailableState.getInstance();
        Backend.RoomContext roomContext47 = null;
        availableState46.checkOut(roomContext47);
        Backend.RoomContext roomContext49 = new Backend.RoomContext(uUID45, (Backend.RoomState) availableState46);
        availableState44.cancelBooking(roomContext49);
        roomContext49.handle();
        availableState35.handle(roomContext49);
        roomContext49.cancelBooking();
        java.lang.String str54 = roomContext49.getBookingEndTime();
        availableState18.setMaintenance(roomContext49);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState0);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState1);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState3);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(str9);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(str11);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState18);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState20);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(str24);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState27);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState29);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState35);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState36);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState38);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState44);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(availableState46);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNull(str54);
    }

    @Test
    public void test505() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test505");
        Backend.ReservationSystem reservationSystem0 = Backend.ReservationSystem.getInstance();
        Backend.User user1 = null;
        Backend.Booking booking4 = reservationSystem0.createBooking(user1, 100, (double) 10.0f);
        booking4.setBookingDate("Please enter a valid email address.");
        java.lang.String str7 = booking4.getBookingId();
        int int8 = booking4.getHours();
        booking4.setRate(40.0d);
        booking4.setBookingDate("BB8A7274D");
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(reservationSystem0);
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertNotNull(booking4);
        // Regression assertion (captures the current behavior of the code)
// flaky:         org.junit.Assert.assertTrue("'" + str7 + "' != '" + "B87C182AB" + "'", str7.equals("B87C182AB"));
        // Regression assertion (captures the current behavior of the code)
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 100 + "'", int8 == 100);
    }
}
