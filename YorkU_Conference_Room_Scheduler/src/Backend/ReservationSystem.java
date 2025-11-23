package Backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReservationSystem {
    
    private static ReservationSystem instance;
    private UserFactory userFactory;
    private PricingPolicyFactory pricingFactory;
    private Map<String, Booking> bookings;
    private BookingCSV bookingCSV;
    
    private ReservationSystem() {
        this.userFactory = new UserFactory();
        this.pricingFactory = new PricingPolicyFactory();
        this.bookings = new HashMap<>();
        this.bookingCSV = BookingCSV.getInstance();
        // Load existing bookings from database
        loadBookingsFromDatabase();
    }
    
    public static ReservationSystem getInstance() {
        if (instance == null) {
            instance = new ReservationSystem();
        }
        return instance;
    }
    
    public User createUserForLogin(String name, String type) {
        // Look up user by email (name) from database
        UserCSV userCSV = UserCSV.getInstance();
        Accounts account = userCSV.findByEmail(name);
        
        if (account != null && account instanceof User) {
            User user = (User) account;
            // Verify the user type matches
            String accountType = user.getAccountType();
            if (accountType.equals(type)) {
                return user;
            }
        }
        
        // If not found, throw exception - user should exist in database
        throw new IllegalArgumentException("User not found with email: " + name + " and type: " + type);
    }
    
    public double calculateHourlyRate(User user) {
        // Use Strategy pattern to get appropriate pricing strategy for user type
        PricingPolicy policy = pricingFactory.createPolicy(user);
        return policy.calculateRate(user);
    }
    
    public Booking createBooking(User user, int hours, double rate) {
        String bookingId = generateBookingId();
        Booking booking = new Booking(bookingId, user, hours, rate, 
                                     null, null, null, null);
        
        // Store in memory
        bookings.put(bookingId, booking);
        
        // Note: This method creates a booking without room information
        // It will not be saved to CSV until room information is added
        // Use createBooking(user, hours, rate, roomNumber, date, startTime, endTime) instead
        
        return booking;
    }
    
    public Booking createBooking(User user, int hours, double rate, 
                                String roomNumber, String bookingDate, 
                                String bookingStartTime, String bookingEndTime) {
        // Validate room information is provided
        if (roomNumber == null || roomNumber.isEmpty()) {
            throw new IllegalArgumentException("Room number is required to create a booking");
        }
        
        // First, book the room through RoomService to validate and update RoomDatabase.csv
        RoomService roomService = new RoomService();
        Room room = findRoomByNumber(roomNumber);
        
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomNumber);
        }
        
        // Check if room is enabled
        if (!room.getStatus().equals("ENABLED")) {
            throw new IllegalStateException("Room is not enabled for booking: " + roomNumber);
        }
        
        // Check for time conflicts with existing bookings
        if (bookingCSV.hasTimeConflict(roomNumber, bookingDate, bookingStartTime, bookingEndTime)) {
            throw new IllegalStateException("Room " + roomNumber + " is already reserved for the time slot " + 
                                          bookingStartTime + " - " + bookingEndTime + " on " + bookingDate);
        }
        
        // Generate booking ID first
        String bookingId = generateBookingId();
        
        // Book the room (this updates RoomDatabase.csv with BookingID)
        // Note: We allow booking even if room is in Reserved state, as long as times don't conflict
        boolean roomBooked = roomService.bookRoom(room.getRoomId(), bookingId, user.getAccountId(), 
                                                  bookingDate, bookingStartTime, bookingEndTime);
        
        if (!roomBooked) {
            throw new IllegalStateException("Failed to book room: " + roomNumber);
        }
        
        // Create booking object
        Booking booking = new Booking(bookingId, user, hours, rate, 
                                     roomNumber, bookingDate, bookingStartTime, bookingEndTime);
        
        // Store in memory
        bookings.put(bookingId, booking);
        
        // Save booking to BookingDatabase.csv
        // Note: RoomDatabase.csv is already updated by RoomService.bookRoom() above
        bookingCSV.write(booking);
        
        return booking;
    }
    
    private Room findRoomByNumber(String roomNumber) {
        RoomService roomService = new RoomService();
        List<Room> allRooms = roomService.getAllRooms();
        for (Room room : allRooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }
    
    public Booking findBooking(String id) {
        // First check in-memory cache
        if (bookings.containsKey(id)) {
            return bookings.get(id);
        }
        
        // If not in memory, load from database
        Booking booking = bookingCSV.findById(id);
        if (booking != null) {
            bookings.put(id, booking);
        }
        
        return booking;
    }
    
    public void updateBooking(Booking booking) {
        // Update in memory
        bookings.put(booking.getBookingId(), booking);
        
        // Update in database
        bookingCSV.update(booking);
    }
    
    public boolean checkIn(String bookingId, String email) {
        Booking booking = findBooking(bookingId);
        
        if (booking == null) {
            return false;
        }
        
        // Verify email matches
        if (!booking.getUser().getEmail().equalsIgnoreCase(email)) {
            return false;
        }
        
        // Update booking status
        booking.setStatus("InUse");
        updateBooking(booking);
        
        // Update room state if room exists
        // Note: Room number format might be "Building - RoomNumber" or just "RoomNumber"
        // For now, we'll search all rooms and match by room number
        if (booking.getRoomNumber() != null && !booking.getRoomNumber().isEmpty()) {
            RoomService roomService = new RoomService();
            List<Room> allRooms = roomService.getAllRooms();
            for (Room room : allRooms) {
                if (room.getRoomNumber().equals(booking.getRoomNumber()) || 
                    room.getLocation().contains(booking.getRoomNumber())) {
                    room.checkIn();
                    RoomCSV roomCSV = RoomCSV.getInstance();
                    roomCSV.update(room);
                    break;
                }
            }
        }
        
        return true;
    }
    
    private void loadBookingsFromDatabase() {
        // Load all bookings from database into memory
        // This is called during initialization
        // For large datasets, you might want to load on-demand instead
    }
    
    private String generateBookingId() {
        return "B" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    
    // Getter methods for factories (if needed)
    public UserFactory getUserFactory() {
        return userFactory;
    }
    
    public PricingPolicyFactory getPricingFactory() {
        return pricingFactory;
    }
}

