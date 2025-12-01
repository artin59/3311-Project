package Backend;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

public class CancelBookingCommandTest {
    private BookingRepository repository;
    private PaymentService paymentService;
    private RoomService roomService;
    private BookingCSV bookingCSV;
    private RoomCSV roomCSV;
    private UserCSV userCSV;
    private User testUser;
    private Room testRoom;
    private MockPaymentProcessor mockProcessor;
    
    static class MockPaymentProcessor implements PaymentProcessor {
        private double lastRefundAmount = 0;
        private boolean refundShouldFail = false;
        
        @Override
        public boolean charge(double amount) {
            return true;
        }
        
        @Override
        public boolean refund(double amount) {
            if (refundShouldFail) {
                return false;
            }
            lastRefundAmount = amount;
            return true;
        }
        
        public double getLastRefundAmount() {
            return lastRefundAmount;
        }
        
        public void setRefundShouldFail(boolean fail) {
            refundShouldFail = fail;
        }
        
        public void reset() {
            lastRefundAmount = 0;
            refundShouldFail = false;
        }
    }
    
    @Before
    public void setUp() throws Exception {
        repository = BookingRepository.getInstance();
        paymentService = PaymentService.getInstance();
        roomService = new RoomService();
        bookingCSV = BookingCSV.getInstance();
        roomCSV = RoomCSV.getInstance();
        userCSV = UserCSV.getInstance();
        
        mockProcessor = new MockPaymentProcessor();
        paymentService.setProcessor(mockProcessor);
        
        testUser = new Student("cancel@yorku.ca", "pass", "12345678");
        userCSV.write(testUser);
        
        testRoom = new Room(10, "BuildingCancel", "CANCEL001");
        roomCSV.write(testRoom);
    }
    
    @Test
    public void testExecute_BookingNotFound() {
        List<BookingObserver> observers = new ArrayList<>();
        CancelBookingCommand command = new CancelBookingCommand(
            "NONEXISTENT", repository, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertFalse("Should return false when booking not found", result);
    }
    
    @Test
    public void testExecute_Success() throws Exception {
        String futureDate = getFutureDate();
        Booking booking = new Booking("CANCEL001", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        bookingCSV.write(booking);
        
        List<BookingObserver> observers = new ArrayList<>();
        CancelBookingCommand command = new CancelBookingCommand(
            "CANCEL001", repository, paymentService, roomService, observers);
        
        boolean result = command.execute();
        assertTrue("Should successfully cancel booking", result);
        assertTrue("Should refund booking cost", mockProcessor.getLastRefundAmount() > 0);
        
        Booking cancelled = repository.findById("CANCEL001");
        assertNull("Booking should be deleted", cancelled);
    }
    
    @Test
    public void testUndo() throws Exception {
        String futureDate = getFutureDate();
        Booking booking = new Booking("CANCEL002", testUser, 1, 20.0, 
                                     testRoom.getRoomNumber(), futureDate, "10:00", "11:00");
        bookingCSV.write(booking);
        
        List<BookingObserver> observers = new ArrayList<>();
        CancelBookingCommand command = new CancelBookingCommand(
            "CANCEL002", repository, paymentService, roomService, observers);
        
        boolean executeResult = command.execute();
        assertTrue("Execute should succeed", executeResult);
        
        mockProcessor.reset();
        boolean undoResult = command.undo();
        assertTrue("Undo should succeed", undoResult);
        
        Booking restored = repository.findById("CANCEL002");
        assertNotNull("Booking should be restored", restored);
        assertEquals("Status should be Reserved", "Reserved", restored.getStatus());
    }
    
    private String getFutureDate() {
        java.time.LocalDate futureDate = java.time.LocalDate.now().plusDays(7);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return futureDate.format(formatter);
    }
}