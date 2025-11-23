package Backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for booking time operations
 */
public class BookingTimeUtil {
    
    /**
     * Check if a booking's start time has passed
     * @param bookingDate The booking date (DD/MM/YYYY format)
     * @param bookingStartTime The booking start time (HH:MM format)
     * @return true if start time has passed, false otherwise
     */
    public static boolean hasStartTimePassed(String bookingDate, String bookingStartTime) {
        try {
            // Parse date (DD/MM/YYYY)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(bookingDate, dateFormatter);
            
            // Parse time (HH:MM)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = LocalTime.parse(bookingStartTime, timeFormatter);
            
            // Get current date and time
            LocalDate now = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            
            // Check if date has passed or if same date but time has passed
            if (date.isBefore(now)) {
                return true; // Date has passed
            } else if (date.isEqual(now) && startTime.isBefore(currentTime)) {
                return true; // Same date but time has passed
            }
            
            return false; // Start time has not passed
        } catch (Exception e) {
            System.err.println("Error checking if start time passed: " + e.getMessage());
            // If parsing fails, assume time has not passed to be safe
            return false;
        }
    }
    
    /**
     * Check if a booking's end time has passed
     * @param bookingDate The booking date (DD/MM/YYYY format)
     * @param bookingEndTime The booking end time (HH:MM format)
     * @return true if end time has passed, false otherwise
     */
    public static boolean hasEndTimePassed(String bookingDate, String bookingEndTime) {
        if (bookingEndTime == null || bookingEndTime.trim().isEmpty()) {
            System.err.println("BookingTimeUtil.hasEndTimePassed: End time is null or empty");
            return false; // No end time, assume not passed
        }
        
        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            System.err.println("BookingTimeUtil.hasEndTimePassed: Booking date is null or empty");
            return false; // No date, assume not passed
        }
        
        try {
            // Parse date (DD/MM/YYYY)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate date = LocalDate.parse(bookingDate.trim(), dateFormatter);
            
            // Parse time (HH:MM)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime endTime = LocalTime.parse(bookingEndTime.trim(), timeFormatter);
            
            // Get current date and time
            LocalDate now = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            
            System.out.println("BookingTimeUtil.hasEndTimePassed: Comparing - " +
                             "Booking: " + date + " " + endTime + 
                             " vs Current: " + now + " " + currentTime);
            
            // Check if date has passed or if same date but time has passed
            if (date.isBefore(now)) {
                System.out.println("BookingTimeUtil.hasEndTimePassed: Date has passed");
                return true; // Date has passed
            } else if (date.isEqual(now) && endTime.isBefore(currentTime)) {
                System.out.println("BookingTimeUtil.hasEndTimePassed: Same date but time has passed");
                return true; // Same date but time has passed
            }
            
            System.out.println("BookingTimeUtil.hasEndTimePassed: End time has not passed");
            return false; // End time has not passed
        } catch (Exception e) {
            System.err.println("Error checking if end time passed: " + e.getMessage());
            System.err.println("BookingTimeUtil.hasEndTimePassed: Date='" + bookingDate + 
                             "', EndTime='" + bookingEndTime + "'");
            e.printStackTrace();
            return false; // On error, assume not passed to be safe
        }
    }
    
    /**
     * Check if a booking is in pre-start state
     * (Reserved state and start time has not passed)
     * @param booking The booking to check
     * @return true if booking is in pre-start state, false otherwise
     */
    public static boolean isPreStartState(Booking booking) {
        if (booking == null) {
            System.err.println("BookingTimeUtil.isPreStartState: Booking is null");
            return false;
        }
        
        String status = booking.getStatus();
        System.out.println("BookingTimeUtil.isPreStartState: Checking booking " + booking.getBookingId() + 
                         " with status: '" + status + "'");
        
        // Check if status is Reserved (case-insensitive check)
        if (status == null || !status.trim().equalsIgnoreCase("Reserved")) {
            System.err.println("BookingTimeUtil.isPreStartState: Booking status is '" + status + 
                             "', expected 'Reserved'");
            return false;
        }
        
        // Check if start time has not passed
        String bookingDate = booking.getBookingDate();
        String bookingStartTime = booking.getBookingStartTime();
        
        if (bookingDate == null || bookingStartTime == null) {
            System.err.println("BookingTimeUtil.isPreStartState: Missing date or start time. " +
                             "Date: '" + bookingDate + "', StartTime: '" + bookingStartTime + "'");
            return false;
        }
        
        boolean timePassed = hasStartTimePassed(bookingDate, bookingStartTime);
        System.out.println("BookingTimeUtil.isPreStartState: Date: '" + bookingDate + 
                         "', StartTime: '" + bookingStartTime + "', TimePassed: " + timePassed);
        
        return !timePassed;
    }
    
    /**
     * Check if a booking can be cancelled
     * More lenient than isPreStartState - allows cancellation if booking hasn't been checked in yet
     * (status is not "InUse" or "Completed")
     * @param booking The booking to check
     * @return true if booking can be cancelled, false otherwise
     */
    public static boolean canCancelBooking(Booking booking) {
        if (booking == null) {
            System.err.println("BookingTimeUtil.canCancelBooking: Booking is null");
            return false;
        }
        
        String status = booking.getStatus();
        System.out.println("BookingTimeUtil.canCancelBooking: Checking booking " + booking.getBookingId() + 
                         " with status: '" + status + "'");
        
        if (status == null) {
            System.err.println("BookingTimeUtil.canCancelBooking: Booking status is null");
            return false;
        }
        
        // Allow cancellation if booking hasn't been checked in yet
        // Status should not be "InUse" or "Completed"
        String normalizedStatus = status.trim();
        if (normalizedStatus.equalsIgnoreCase("InUse")) {
            System.err.println("BookingTimeUtil.canCancelBooking: Booking is in use, cannot cancel");
            return false;
        }
        
        if (normalizedStatus.equalsIgnoreCase("Completed")) {
            System.err.println("BookingTimeUtil.canCancelBooking: Booking is completed, cannot cancel");
            return false;
        }
        
        if (normalizedStatus.equalsIgnoreCase("Cancelled")) {
            System.err.println("BookingTimeUtil.canCancelBooking: Booking is already cancelled");
            return false;
        }
        
        // Allow cancellation for Reserved, NoShow, or any other status that isn't InUse/Completed/Cancelled
        System.out.println("BookingTimeUtil.canCancelBooking: Booking can be cancelled (status: '" + status + "')");
        return true;
    }
}

