package Backend;

import java.util.List;

/**
 * Command to cancel a booking
 * REQ8: CancelBookingCommand marks the booking as cancelled, changes the booking/room state,
 * and asks PaymentService to handle refunds.
 */
public class CancelBookingCommand implements Command {
    private String bookingId;
    private BookingRepository repository;
    private PaymentService paymentService;
    private RoomService roomService;
    private List<BookingObserver> observers;
    private Booking originalBooking; // For undo
    
    public CancelBookingCommand(String bookingId, BookingRepository repository, 
                                PaymentService paymentService, RoomService roomService,
                                List<BookingObserver> observers) {
        this.bookingId = bookingId;
        this.repository = repository;
        this.paymentService = paymentService;
        this.roomService = roomService;
        this.observers = observers;
    }
    
    @Override
    public boolean execute() {
        // Find the booking
        Booking booking = repository.findById(bookingId);
        if (booking == null) {
            System.err.println("CancelBookingCommand: Booking not found: " + bookingId);
            return false;
        }
        
        // Store original booking for undo
        originalBooking = booking;
        
        // Check if booking can be cancelled (hasn't been checked in yet)
        // This is more lenient than isPreStartState - allows cancellation even if start time has passed
        // as long as the booking hasn't been checked in
        if (!BookingTimeUtil.canCancelBooking(booking)) {
            System.err.println("CancelBookingCommand: Booking " + bookingId + 
                             " cannot be cancelled (may be in use or already completed).");
            return false;
        }
        
        // Mark booking as cancelled
        booking.setStatus("Cancelled");
        
        // Update room state - clear booking info and set to Available
        if (booking.getRoomNumber() != null && !booking.getRoomNumber().isEmpty()) {
            RoomService rs = new RoomService();
            Room room = findRoomByNumber(booking.getRoomNumber());
            if (room != null) {
                room.getRoomContext().clearBookingInfo();
                room.getRoomContext().setState(AvailableState.getInstance());
                RoomCSV roomCSV = RoomCSV.getInstance();
                roomCSV.update(room);
            }
        }
        
        // Handle refund via PaymentService (REQ8)
        double refundAmount = booking.getTotalCost();
        if (refundAmount > 0) {
            boolean refundSuccess = paymentService.refund(refundAmount);
            if (!refundSuccess) {
                System.err.println("CancelBookingCommand: Refund failed for booking " + bookingId);
                // Continue with cancellation even if refund fails
            } else {
                System.out.println("CancelBookingCommand: Refunded $" + refundAmount + " for booking " + bookingId);
            }
        }
        
        // Delete booking from repository (BookingDatabase.csv)
        boolean deleted = repository.delete(bookingId);
        if (!deleted) {
            System.err.println("CancelBookingCommand: Failed to delete booking from repository");
            return false;
        }
        
        // Notify observers (REQ8)
        notifyObserversCancelled(bookingId);
        
        System.out.println("CancelBookingCommand: Successfully cancelled booking " + bookingId);
        return true;
    }
    
    @Override
    public boolean undo() {
        // Undo cancellation - restore booking
        if (originalBooking == null) {
            return false;
        }
        
        // Restore booking status
        originalBooking.setStatus("Reserved");
        
        // Save booking back to repository
        repository.save(originalBooking);
        
        // Restore room state
        if (originalBooking.getRoomNumber() != null) {
            Room room = findRoomByNumber(originalBooking.getRoomNumber());
            if (room != null) {
                room.getRoomContext().setBookingInfo(
                    originalBooking.getBookingId(),
                    originalBooking.getUser().getAccountId(),
                    originalBooking.getBookingDate(),
                    originalBooking.getBookingStartTime(),
                    originalBooking.getBookingEndTime()
                );
                room.getRoomContext().setState(ReservedState.getInstance());
                RoomCSV roomCSV = RoomCSV.getInstance();
                roomCSV.update(room);
            }
        }
        
        // Charge back the refunded amount
        paymentService.charge(originalBooking.getTotalCost());
        
        // Notify observers
        notifyObserversCreated(originalBooking);
        
        return true;
    }
    
    private Room findRoomByNumber(String roomNumber) {
        RoomService rs = new RoomService();
        List<Room> allRooms = rs.getAllRooms();
        for (Room room : allRooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }
    
    private void notifyObserversCancelled(String bookingId) {
        if (observers != null) {
            for (BookingObserver observer : observers) {
                observer.onBookingCancelled(bookingId);
            }
        }
    }
    
    private void notifyObserversCreated(Booking booking) {
        if (observers != null) {
            for (BookingObserver observer : observers) {
                observer.onBookingCreated(booking);
            }
        }
    }
}

