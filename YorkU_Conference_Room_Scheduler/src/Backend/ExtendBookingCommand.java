package Backend;

import java.util.List;

/**
 * Command to extend a booking
 * ensures the end time has not passed, checks if room is free for extended period,
 * updates booking end time, applies hourly rate via Pricing Strategy, and charges additional amount.
 */
public class ExtendBookingCommand implements Command {
    private String bookingId;
    private int extraDuration; // in hours
    private BookingRepository repository;
    private PricingPolicyFactory pricingFactory;
    private PaymentService paymentService;
    private RoomService roomService;
    private List<BookingObserver> observers;
    
    // For undo
    private Booking originalBooking;
    private String originalEndTime;
    private int originalHours;
    private double originalCost;
    
    public ExtendBookingCommand(String bookingId, int extraDuration,
                                BookingRepository repository, PricingPolicyFactory pricingFactory,
                                PaymentService paymentService, RoomService roomService,
                                List<BookingObserver> observers) {
        this.bookingId = bookingId;
        this.extraDuration = extraDuration;
        this.repository = repository;
        this.pricingFactory = pricingFactory;
        this.paymentService = paymentService;
        this.roomService = roomService;
        this.observers = observers;
    }
    
    @Override
    public boolean execute() {
        // Find the booking
        Booking booking = repository.findById(bookingId);
        if (booking == null) {
            System.err.println("ExtendBookingCommand: Booking not found: " + bookingId);
            return false;
        }
        
        // Debug: Print booking details
        System.out.println("ExtendBookingCommand: Found booking - ID: " + bookingId);
        System.out.println("ExtendBookingCommand: Room: " + booking.getRoomNumber());
        System.out.println("ExtendBookingCommand: Date: " + booking.getBookingDate());
        System.out.println("ExtendBookingCommand: Start: " + booking.getBookingStartTime());
        System.out.println("ExtendBookingCommand: Current End: " + booking.getBookingEndTime());
        System.out.println("ExtendBookingCommand: Status: " + booking.getStatus());
        
        // Store original values for undo
        originalBooking = booking;
        originalEndTime = booking.getBookingEndTime();
        originalHours = booking.getHours();
        originalCost = booking.getTotalCost();
        
        // REQ9: Check booking's current state (InUseState or ReservedState) - case-insensitive
        String status = booking.getStatus();
        if (status == null || (!status.trim().equalsIgnoreCase("InUse") && !status.trim().equalsIgnoreCase("Reserved"))) {
            System.err.println("ExtendBookingCommand: Booking " + bookingId + 
                             " is not in InUse or Reserved state. Current state: '" + status + "'");
            return false;
        }
        
        // REQ9: Ensure the end time has not passed
        if (BookingTimeUtil.hasEndTimePassed(booking.getBookingDate(), booking.getBookingEndTime())) {
            System.err.println("ExtendBookingCommand: Booking end time has already passed. Cannot extend.");
            System.err.println("ExtendBookingCommand: Date: '" + booking.getBookingDate() + 
                             "', EndTime: '" + booking.getBookingEndTime() + "'");
            return false;
        }
        
        // Calculate new end time
        String newEndTime = calculateNewEndTime(booking.getBookingEndTime(), extraDuration);
        System.out.println("ExtendBookingCommand: Current end time: '" + booking.getBookingEndTime() + 
                         "', New end time: '" + newEndTime + "', Extra duration: " + extraDuration + " hours");
        
        // REQ9: Check if the EXTENDED time slot is free (from current end time to new end time)
        // We only need to check if the extended portion conflicts with other bookings
        String currentEndTime = booking.getBookingEndTime();
        if (currentEndTime == null || currentEndTime.trim().isEmpty()) {
            System.err.println("ExtendBookingCommand: Current booking end time is not set");
            return false;
        }
        
        // Check if the extended time slot (currentEndTime to newEndTime) conflicts with other bookings
        // Note: We exclude the current booking ID so it doesn't conflict with itself
        BookingCSV bookingCSV = BookingCSV.getInstance();
        System.out.println("ExtendBookingCommand: Checking for conflicts in extended slot: " + currentEndTime + " to " + newEndTime);
        System.out.println("ExtendBookingCommand: Excluding booking ID: " + bookingId);
        
        if (bookingCSV.hasTimeConflict(booking.getRoomNumber(), booking.getBookingDate(),
                                       currentEndTime, newEndTime, bookingId)) {
            System.err.println("ExtendBookingCommand: Extended time slot is already reserved");
            System.err.println("ExtendBookingCommand: Room: '" + booking.getRoomNumber() + 
                             "', Date: '" + booking.getBookingDate() + 
                             "', Extended time slot: '" + currentEndTime + "' to '" + newEndTime + "'");
            System.err.println("ExtendBookingCommand: Please check the console output above for conflict details");
            return false;
        }
        
        System.out.println("ExtendBookingCommand: No conflicts found in extended time slot");
        
        // Update booking end time
        System.out.println("ExtendBookingCommand: Setting booking end time from '" + booking.getBookingEndTime() + "' to '" + newEndTime + "'");
        booking.setBookingEndTime(newEndTime);
        System.out.println("ExtendBookingCommand: Booking end time after set: '" + booking.getBookingEndTime() + "'");
        
        // Update hours
        int newHours = originalHours + extraDuration;
        booking.setHours(newHours);
        System.out.println("ExtendBookingCommand: Updated hours from " + originalHours + " to " + newHours);
        
        // REQ9: Apply correct hourly rate via Pricing Strategy
        // Use Strategy pattern to get appropriate pricing strategy for user type
        PricingPolicy policy = pricingFactory.createPolicy(booking.getUser());
        double hourlyRate = policy.calculateRate(booking.getUser());
        booking.setRate(hourlyRate);
        
        // Calculate additional amount to charge
        double additionalAmount = extraDuration * hourlyRate;
        
        // REQ9: Charge additional amount through PaymentService
        if (additionalAmount > 0) {
            boolean chargeSuccess = paymentService.chargeAdditional(additionalAmount);
            if (!chargeSuccess) {
                System.err.println("ExtendBookingCommand: Failed to charge additional amount");
                return false;
            }
            System.out.println("ExtendBookingCommand: Charged additional $" + additionalAmount + 
                             " for " + extraDuration + " extra hours");
        }
        
        // Update room booking end time in RoomDatabase.csv
        Room room = findRoomByNumber(booking.getRoomNumber());
        if (room != null) {
            room.getRoomContext().setBookingInfo(
                booking.getBookingId(),
                booking.getUser().getAccountId(),
                booking.getBookingDate(),
                booking.getBookingStartTime(),
                newEndTime
            );
            RoomCSV roomCSV = RoomCSV.getInstance();
            roomCSV.update(room);
        }
        
        // REQ9: Save new timeslots in CSV
        System.out.println("ExtendBookingCommand: Updating booking in repository with new end time: " + newEndTime);
        repository.update(booking);
        System.out.println("ExtendBookingCommand: Booking updated in repository");
        
        // REQ9: Notify observers (room list, booking history, admin view)
        System.out.println("ExtendBookingCommand: Notifying observers...");
        notifyObservers(booking);
        System.out.println("ExtendBookingCommand: Observers notified");
        
        System.out.println("ExtendBookingCommand: Successfully extended booking " + bookingId + 
                         " by " + extraDuration + " hours. New end time: " + newEndTime);
        return true;
    }
    
    @Override
    public boolean undo() {
        if (originalBooking == null) {
            return false;
        }
        
        // Restore original values
        originalBooking.setBookingEndTime(originalEndTime);
        originalBooking.setHours(originalHours);
        
        // Refund the additional charge
        double additionalAmount = (originalBooking.getTotalCost() - originalCost);
        if (additionalAmount > 0) {
            paymentService.refund(additionalAmount);
        }
        
        // Restore room booking end time
        Room room = findRoomByNumber(originalBooking.getRoomNumber());
        if (room != null) {
            room.getRoomContext().setBookingInfo(
                originalBooking.getBookingId(),
                originalBooking.getUser().getAccountId(),
                originalBooking.getBookingDate(),
                originalBooking.getBookingStartTime(),
                originalEndTime
            );
            RoomCSV roomCSV = RoomCSV.getInstance();
            roomCSV.update(room);
        }
        
        repository.update(originalBooking);
        notifyObservers(originalBooking);
        
        return true;
    }
    
    private String calculateNewEndTime(String currentEndTime, int extraHours) {
        if (currentEndTime == null || currentEndTime.trim().isEmpty()) {
            return currentEndTime;
        }
        
        try {
            String[] parts = currentEndTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            
            hours += extraHours;
            if (hours >= 24) {
                hours = hours % 24;
            }
            
            return String.format("%02d:%02d", hours, minutes);
        } catch (Exception e) {
            System.err.println("Error calculating new end time: " + e.getMessage());
            return currentEndTime;
        }
    }
    
    private Room findRoomByNumber(String roomNumber) {
        if (roomNumber == null) {
            return null;
        }
        List<Room> allRooms = roomService.getAllRooms();
        for (Room room : allRooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }
    
    private void notifyObservers(Booking booking) {
        if (observers != null) {
            for (BookingObserver observer : observers) {
                observer.onBookingUpdated(booking);
            }
        }
    }
}

