package Backend;

import java.util.UUID;

public class Booking {
    private String bookingId;
    private User user;
    private int hours;
    private double rate;
    private double totalCost;
    private String roomNumber;
    private String bookingDate;
    private String bookingStartTime;
    private String bookingEndTime;
    private String status; // Reserved, InUse, Cancelled, Completed
    
    public Booking(String bookingId, User user, int hours, double rate, 
                   String roomNumber, String bookingDate, String bookingStartTime, String bookingEndTime) {
        this.bookingId = bookingId;
        this.user = user;
        this.hours = hours;
        this.rate = rate;
        this.totalCost = hours * rate;
        this.roomNumber = roomNumber;
        this.bookingDate = bookingDate;
        this.bookingStartTime = bookingStartTime;
        this.bookingEndTime = bookingEndTime;
        this.status = "Reserved";
    }
    
    // Getters
    public String getBookingId() {
        return bookingId;
    }
    
    public User getUser() {
        return user;
    }
    
    public int getHours() {
        return hours;
    }
    
    public double getRate() {
        return rate;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public String getBookingDate() {
        return bookingDate;
    }
    
    public String getBookingStartTime() {
        return bookingStartTime;
    }
    
    public String getBookingEndTime() {
        return bookingEndTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public void setBookingStartTime(String bookingStartTime) {
        this.bookingStartTime = bookingStartTime;
    }
    
    public void setBookingEndTime(String bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }
    
    public void setHours(int hours) {
        this.hours = hours;
        this.totalCost = hours * rate;
    }
    
    public void setRate(double rate) {
        this.rate = rate;
        this.totalCost = hours * rate;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}

