package Backend;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingController (Invoker) for the Command pattern
 * REQ8: Receives commands from UI and calls execute() only if booking is in pre-start state
 */
public class BookingController {
    private static BookingController instance;
    private BookingRepository repository;
    private PaymentService paymentService;
    private RoomService roomService;
    private PricingPolicyFactory pricingFactory;
    private List<BookingObserver> observers;
    
    private BookingController() {
        this.repository = BookingRepository.getInstance();
        this.paymentService = PaymentService.getInstance();
        this.roomService = new RoomService();
        this.pricingFactory = new PricingPolicyFactory();
        this.observers = new ArrayList<>();
    }
    
    public static BookingController getInstance() {
        if (instance == null) {
            instance = new BookingController();
        }
        return instance;
    }
    
    //Add an observer to be notified of booking changes

    public void addObserver(BookingObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
   //Remove an observer
    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }
    

  //Execute a cancel booking command

    public boolean cancelBooking(String bookingId) {
        // Check if booking can be cancelled
        Booking booking = repository.findById(bookingId);
        if (booking == null) {
            System.err.println("BookingController: Booking not found: " + bookingId);
            return false;
        }
        
        // Debug: Print booking details
        System.out.println("BookingController.cancelBooking: Booking ID: " + bookingId);
        System.out.println("BookingController.cancelBooking: Status: '" + booking.getStatus() + "'");
        System.out.println("BookingController.cancelBooking: Date: '" + booking.getBookingDate() + "'");
        System.out.println("BookingController.cancelBooking: StartTime: '" + booking.getBookingStartTime() + "'");
        
        // Check if booking can be cancelled (hasn't been checked in yet)
        if (!BookingTimeUtil.canCancelBooking(booking)) {
            System.err.println("BookingController: Booking " + bookingId + 
                             " cannot be cancelled (may be in use or already completed).");
            System.err.println("BookingController: Status='" + booking.getStatus() + 
                             "', Date='" + booking.getBookingDate() + 
                             "', StartTime='" + booking.getBookingStartTime() + "'");
            return false;
        }
        
        CancelBookingCommand command = new CancelBookingCommand(
            bookingId, repository, paymentService, roomService, observers);
        
        return command.execute();
    }
    
    //Execute an edit booking command
    //REQ8: Only executes if booking is in pre-start state
    public boolean editBooking(String bookingId, String newBuildingName, String newRoomNumber, 
                              String newDate, String newStartTime, String newEndTime) {
        // Check if booking is in pre-start state before creating command
        Booking booking = repository.findById(bookingId);
        if (booking == null) {
            System.err.println("BookingController: Booking not found: " + bookingId);
            return false;
        }
        
        // REQ8: Only execute if booking is in pre-start state
        if (!BookingTimeUtil.isPreStartState(booking)) {
            System.err.println("BookingController: Booking " + bookingId + 
                             " is not in pre-start state. Cannot edit.");
            return false;
        }
        
        EditBookingCommand command = new EditBookingCommand(
            bookingId, newBuildingName, newRoomNumber, newDate, newStartTime, newEndTime,
            repository, pricingFactory, paymentService, roomService, observers);
        
        return command.execute();
    }
    

  //Execute an extend booking command
 //REQ9: Checks booking state (InUseState or ReservedState) and ensures end time has not passed

    public boolean extendBooking(String bookingId, int extraDuration) {
        // Find the booking
        Booking booking = repository.findById(bookingId);
        if (booking == null) {
            System.err.println("BookingController: Booking not found: " + bookingId);
            return false;
        }
        
        // Debug: Print booking details
        System.out.println("BookingController.extendBooking: Booking ID: " + bookingId);
        System.out.println("BookingController.extendBooking: Status: '" + booking.getStatus() + "'");
        System.out.println("BookingController.extendBooking: Date: '" + booking.getBookingDate() + "'");
        System.out.println("BookingController.extendBooking: StartTime: '" + booking.getBookingStartTime() + "'");
        System.out.println("BookingController.extendBooking: EndTime: '" + booking.getBookingEndTime() + "'");
        System.out.println("BookingController.extendBooking: ExtraDuration: " + extraDuration + " hours");
        
        // REQ9: Check booking's current state (InUseState or ReservedState) - case-insensitive
        String status = booking.getStatus();
        if (status == null || (!status.trim().equalsIgnoreCase("InUse") && !status.trim().equalsIgnoreCase("Reserved"))) {
            System.err.println("BookingController: Booking " + bookingId + 
                             " is not in InUse or Reserved state. Current state: '" + status + "'");
            return false;
        }
        
        // REQ9: Ensure the end time has not passed
        String bookingDate = booking.getBookingDate();
        String bookingEndTime = booking.getBookingEndTime();
        
        if (bookingEndTime == null || bookingEndTime.trim().isEmpty()) {
            System.err.println("BookingController: Booking end time is null or empty. Cannot extend.");
            System.err.println("BookingController: Date: '" + bookingDate + "', EndTime: '" + bookingEndTime + "'");
            return false;
        }
        
        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            System.err.println("BookingController: Booking date is null or empty. Cannot extend.");
            return false;
        }
        
        boolean endTimePassed = BookingTimeUtil.hasEndTimePassed(bookingDate, bookingEndTime);
        System.out.println("BookingController: Checking if end time passed - Date: '" + bookingDate + 
                         "', EndTime: '" + bookingEndTime + "', Passed: " + endTimePassed);
        
        if (endTimePassed) {
            System.err.println("BookingController: Booking end time has already passed. Cannot extend.");
            System.err.println("BookingController: Date: '" + bookingDate + 
                             "', EndTime: '" + bookingEndTime + "'");
            return false;
        }
        
        ExtendBookingCommand command = new ExtendBookingCommand(
            bookingId, extraDuration, repository, pricingFactory, paymentService, roomService, observers);
        
        return command.execute();
    }
}

