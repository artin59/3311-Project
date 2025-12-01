package Backend;

import java.util.List;

/**
 * Command to edit a booking
 * REQ8: EditBookingCommand updates room/time details, recalculates price via the pricing strategies,
 * and adjusts payment as needed.
 */
public class EditBookingCommand implements Command {
    private String bookingId;
    private String newBuildingName;
    private String newRoomNumber;
    private String newDate;
    private String newStartTime;
    private String newEndTime;
    private BookingRepository repository;
    private PricingPolicyFactory pricingFactory;
    private PaymentService paymentService;
    private RoomService roomService;
    private List<BookingObserver> observers;
    
    // For undo
    private Booking originalBooking;
    private String originalRoomNumber;
    private String originalDate;
    private String originalStartTime;
    private String originalEndTime;
    private double originalCost;
    
    public EditBookingCommand(String bookingId, String newBuildingName, String newRoomNumber, 
                             String newDate, String newStartTime, String newEndTime,
                             BookingRepository repository, PricingPolicyFactory pricingFactory,
                             PaymentService paymentService, RoomService roomService,
                             List<BookingObserver> observers) {
        this.bookingId = bookingId;
        this.newBuildingName = newBuildingName;
        this.newRoomNumber = newRoomNumber;
        this.newDate = newDate;
        this.newStartTime = newStartTime;
        this.newEndTime = newEndTime;
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
            System.err.println("EditBookingCommand: Booking not found: " + bookingId);
            return false;
        }
        
        // Store original values for undo
        originalBooking = booking;
        originalRoomNumber = booking.getRoomNumber();
        originalDate = booking.getBookingDate();
        originalStartTime = booking.getBookingStartTime();
        originalEndTime = booking.getBookingEndTime();
        
        // Calculate original cost using the same pricing policy to ensure consistency
        // This ensures we compare apples to apples when calculating price difference
        int originalHours = calculateHours(originalStartTime, originalEndTime);
        PricingPolicy policy = pricingFactory.createPolicy(booking.getUser());
        double originalRate = policy.calculateRate(booking.getUser());
        originalCost = originalHours * originalRate;  // Calculate using same policy as new cost
        
        // Check if booking is in pre-start state (REQ8 requirement)
        if (!BookingTimeUtil.isPreStartState(booking)) {
            System.err.println("EditBookingCommand: Booking " + bookingId + 
                             " is not in pre-start state. Cannot edit.");
            return false;
        }
        
        // Check for time conflicts if room or time changed
        if ((newRoomNumber != null && !newRoomNumber.equals(originalRoomNumber)) ||
            (newDate != null && !newDate.equals(originalDate)) ||
            (newStartTime != null && !newStartTime.equals(originalStartTime))) {
            
            String roomToCheck = (newRoomNumber != null && !newRoomNumber.isEmpty()) ? newRoomNumber : originalRoomNumber;
            String dateToCheck = (newDate != null && !newDate.isEmpty()) ? newDate : originalDate;
            String startToCheck = (newStartTime != null && !newStartTime.isEmpty()) ? newStartTime : originalStartTime;
            String endToCheck = (newEndTime != null && !newEndTime.isEmpty()) ? newEndTime : originalEndTime;
            
            // Calculate end time if not provided
            if (endToCheck == null || endToCheck.isEmpty()) {
                endToCheck = calculateEndTime(startToCheck);
            }
            
            BookingCSV bookingCSV = BookingCSV.getInstance();
            // Exclude the current booking from conflict check
            if (bookingCSV.hasTimeConflict(roomToCheck, dateToCheck, startToCheck, endToCheck, bookingId)) {
                System.err.println("EditBookingCommand: Time conflict detected for new booking details");
                return false;
            }
        }
        
        // If room number changed, look up the new room to get building name
        if (newRoomNumber != null && !newRoomNumber.isEmpty() && !newRoomNumber.equals(originalRoomNumber)) {
            Room newRoom = findRoomByNumber(newRoomNumber);
            if (newRoom == null) {
                System.err.println("EditBookingCommand: New room not found: " + newRoomNumber);
                return false;
            }
            // Use the room's building name (ignore newBuildingName parameter - it should match the room)
            newBuildingName = newRoom.getBuildingName();
        }
        
        // Update booking details
        if (newRoomNumber != null && !newRoomNumber.isEmpty()) {
            booking.setRoomNumber(newRoomNumber);
        }
        if (newDate != null && !newDate.isEmpty()) {
            booking.setBookingDate(newDate);
        }
        if (newStartTime != null && !newStartTime.isEmpty()) {
            booking.setBookingStartTime(newStartTime);
        }
        if (newEndTime != null && !newEndTime.isEmpty()) {
            booking.setBookingEndTime(newEndTime);
        } else if (newStartTime != null && !newStartTime.isEmpty()) {
            // If start time changed but end time not provided, calculate end time
            booking.setBookingEndTime(calculateEndTime(newStartTime));
        }
        
        // Recalculate hours and price (REQ8)
        // Use the booking's current times (which have been updated above)
        int newHours = calculateHours(booking.getBookingStartTime(), booking.getBookingEndTime());
        
        booking.setHours(newHours);
        
        // Recalculate rate using pricing strategy (REQ8)
        // Use Strategy pattern to get appropriate pricing strategy for user type
        PricingPolicy policy1 = pricingFactory.createPolicy(booking.getUser());
        double newRate = policy1.calculateRate(booking.getUser());
        booking.setRate(newRate); // Update rate first
        booking.setHours(newHours); // This will recalculate totalCost based on new rate and hours
        double newTotalCost = booking.getTotalCost();
        
        // Calculate price difference
        double priceDifference = newTotalCost - originalCost;
        
        // Debug output
        System.out.println("EditBookingCommand: Original cost: " + originalCost);
        System.out.println("EditBookingCommand: New total cost: " + newTotalCost);
        System.out.println("EditBookingCommand: Price difference: " + priceDifference);
        
        // Adjust payment (REQ8)
        if (priceDifference > 0) {
            // Charge additional amount
            boolean chargeSuccess = paymentService.chargeAdditional(priceDifference);
            if (!chargeSuccess) {
                System.err.println("EditBookingCommand: Failed to charge additional amount");
                return false;
            }
        } else if (priceDifference < 0) {
            // Refund difference
            boolean refundSuccess = paymentService.refund(Math.abs(priceDifference));
            if (!refundSuccess) {
                System.err.println("EditBookingCommand: Failed to refund difference");
                // Continue with edit even if refund fails
            }
        }
        
        // Update room in RoomDatabase.csv
        if (newRoomNumber != null && !newRoomNumber.equals(originalRoomNumber)) {
            // Update old room - clear booking
            Room oldRoom = findRoomByNumber(originalRoomNumber);
            if (oldRoom != null) {
                oldRoom.getRoomContext().clearBookingInfo();
                oldRoom.getRoomContext().setState(AvailableState.getInstance());
                RoomCSV roomCSV = RoomCSV.getInstance();
                roomCSV.update(oldRoom);
            }
            
            // Update new room - set booking
            Room newRoom = findRoomByNumber(newRoomNumber);
            if (newRoom != null) {
                newRoom.getRoomContext().setBookingInfo(
                    booking.getBookingId(),
                    booking.getUser().getAccountId(),
                    booking.getBookingDate(),
                    booking.getBookingStartTime(),
                    booking.getBookingEndTime()
                );
                newRoom.getRoomContext().setState(ReservedState.getInstance());
                RoomCSV roomCSV = RoomCSV.getInstance();
                roomCSV.update(newRoom);
            }
        } else {
            // Same room, just update booking info
            Room room = findRoomByNumber(booking.getRoomNumber());
            if (room != null) {
                room.getRoomContext().setBookingInfo(
                    booking.getBookingId(),
                    booking.getUser().getAccountId(),
                    booking.getBookingDate(),
                    booking.getBookingStartTime(),
                    booking.getBookingEndTime()
                );
                RoomCSV roomCSV = RoomCSV.getInstance();
                roomCSV.update(room);
            }
        }
        
        // Persist changes through repository (REQ8)
        repository.update(booking);
        
        // Notify observers (REQ8)
        notifyObservers(booking);
        
        System.out.println("EditBookingCommand: Successfully edited booking " + bookingId);
        return true;
    }
    
    @Override
    public boolean undo() {
        if (originalBooking == null) {
            return false;
        }
        
        // Restore original values
        originalBooking.setRoomNumber(originalRoomNumber);
        originalBooking.setBookingDate(originalDate);
        originalBooking.setBookingStartTime(originalStartTime);
        originalBooking.setBookingEndTime(originalEndTime);
        originalBooking.setHours(calculateHours(originalStartTime, originalEndTime));
        
        // Restore original payment
        double currentCost = originalBooking.getTotalCost();
        double costDifference = originalCost - currentCost;
        if (costDifference > 0) {
            paymentService.charge(costDifference);
        } else if (costDifference < 0) {
            paymentService.refund(Math.abs(costDifference));
        }
        
        // Restore room states
        Room room = findRoomByNumber(originalBooking.getRoomNumber());
        if (room != null) {
            room.getRoomContext().setBookingInfo(
                originalBooking.getBookingId(),
                originalBooking.getUser().getAccountId(),
                originalDate,
                originalStartTime,
                originalEndTime
            );
            RoomCSV roomCSV = RoomCSV.getInstance();
            roomCSV.update(room);
        }
        
        repository.update(originalBooking);
        notifyObservers(originalBooking);
        
        return true;
    }
    
    private int calculateHours(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return 1; // Default
        }
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMin = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMin = Integer.parseInt(endParts[1]);
            
            int startMinutes = startHour * 60 + startMin;
            int endMinutes = endHour * 60 + endMin;
            int diffMinutes = endMinutes - startMinutes;
            
            // Round up to nearest hour
            return (int) Math.ceil(diffMinutes / 60.0);
        } catch (Exception e) {
            return 1; // Default
        }
    }
    
    private String calculateEndTime(String startTime) {
        if (startTime == null || startTime.trim().isEmpty()) {
            return null;
        }
        try {
            String[] parts = startTime.trim().split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            
            // Add 1 hour
            hours += 1;
            if (hours >= 24) {
                hours = hours % 24;
            }
            
            return String.format("%02d:%02d", hours, minutes);
        } catch (Exception e) {
            return null;
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

