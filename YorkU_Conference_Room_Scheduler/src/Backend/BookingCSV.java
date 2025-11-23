package Backend;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class BookingCSV {
    
    private static BookingCSV instance = new BookingCSV();
    // Use the same directory as RoomDatabase.csv for consistency
    private final String BOOKING_PATH = "a:\\EECS 3311\\EECS 3311\\BookingDatabase.csv";
    private final String ROOM_PATH = "a:\\EECS 3311\\EECS 3311\\RoomDatabase.csv";
    
    private BookingCSV() {
        try {
            File bookingFile = new File(BOOKING_PATH);
            
            if (!bookingFile.exists()) {
                // Create directory if it doesn't exist
                bookingFile.getParentFile().mkdirs();
                
                CsvWriter csvWrite = new CsvWriter(new FileWriter(BOOKING_PATH, false), ',');
                csvWrite.write("BookingID");
                csvWrite.write("RoomID");
                csvWrite.write("Building Name");
                csvWrite.write("Room Number");
                csvWrite.write("Booking UserID");
                csvWrite.write("Booking Date");
                csvWrite.write("Booking Start Time");
                csvWrite.write("Booking End Time");
                csvWrite.endRecord();
                csvWrite.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static BookingCSV getInstance() {
        return instance;
    }
    
    public void write(Booking booking) {
        // Only write if booking has room information
        if (booking.getRoomNumber() == null || booking.getRoomNumber().isEmpty()) {
            return;
        }
        
        try {
            // Find room to get Room ID and Building Name
            Room room = findRoomByNumber(booking.getRoomNumber());
            
            // Write to BookingDatabase.csv
            CsvWriter csvWrite = new CsvWriter(new FileWriter(BOOKING_PATH, true), ',');
            
            String bookingId = booking.getBookingId() != null ? booking.getBookingId() : "";
            String roomId = room != null ? room.getRoomId().toString() : "";
            String buildingName = room != null ? room.getBuildingName() : "";
            String userId = booking.getUser().getAccountId() != null ? 
                           booking.getUser().getAccountId().toString() : "";
            
            csvWrite.write(bookingId);
            csvWrite.write(roomId);
            csvWrite.write(buildingName);
            csvWrite.write(booking.getRoomNumber());
            csvWrite.write(userId);
            csvWrite.write(booking.getBookingDate() != null ? booking.getBookingDate() : "");
            csvWrite.write(booking.getBookingStartTime() != null ? booking.getBookingStartTime() : "");
            
            // Calculate end time as start time + 1 hour
            String endTime = calculateEndTime(booking.getBookingStartTime());
            csvWrite.write(endTime);
            
            csvWrite.endRecord();
            csvWrite.close();
            
            // Note: RoomDatabase.csv is updated by ReservationSystem through RoomService.bookRoom()
            // before this method is called, so we don't need to update it again here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Calculate end time as start time + 1 hour
     * @param startTime Start time in HH:MM format
     * @return End time in HH:MM format (start time + 1 hour)
     */
    private String calculateEndTime(String startTime) {
        if (startTime == null || startTime.trim().isEmpty()) {
            return "";
        }
        
        try {
            String[] parts = startTime.trim().split(":");
            if (parts.length >= 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                
                // Add 1 hour
                hours += 1;
                if (hours >= 24) {
                    hours = hours % 24;
                }
                
                return String.format("%02d:%02d", hours, minutes);
            }
        } catch (Exception e) {
            System.err.println("Error calculating end time from start time: " + startTime);
            e.printStackTrace();
        }
        
        return "";
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
    
    private void updateRoomDatabase(Room room, Booking booking) {
        try {
            RoomCSV roomCSV = RoomCSV.getInstance();
            // Update room with booking information
            room.getRoomContext().setBookingInfo(
                booking.getBookingId(),
                booking.getUser().getAccountId(),
                booking.getBookingDate(),
                booking.getBookingStartTime(),
                booking.getBookingEndTime()
            );
            room.getRoomContext().setState(ReservedState.getInstance());
            roomCSV.update(room);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Booking findById(String bookingId) {
        // Search for booking by ID
        try {
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                return null;
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                String recordBookingId = csvRead.get(0); // BookingID is first column
                if (recordBookingId != null && recordBookingId.trim().equals(bookingId)) {
                    Booking booking = parseBookingFromRecord(csvRead);
                    csvRead.close();
                    return booking;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            System.err.println("Error finding booking by ID: " + e.getMessage());
        }
        return null;
    }
    
    // Check if a time slot conflicts with existing bookings for a room
    public boolean hasTimeConflict(String roomNumber, String date, String startTime, String endTime) {
        return hasTimeConflict(roomNumber, date, startTime, endTime, null);
    }
    
    /**
     * Check if there's a time conflict for a room on a given date and time range
     * @param roomNumber The room number
     * @param date The booking date
     * @param startTime The start time
     * @param endTime The end time
     * @param excludeBookingId Optional booking ID to exclude from conflict check (e.g., when extending a booking)
     * @return true if there's a conflict, false otherwise
     */
    public boolean hasTimeConflict(String roomNumber, String date, String startTime, String endTime, String excludeBookingId) {
        System.out.println("hasTimeConflict: Checking for conflicts - Room: " + roomNumber + ", Date: " + date + 
                         ", Time: " + startTime + "-" + endTime + ", Exclude: " + excludeBookingId);
        try {
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                System.out.println("hasTimeConflict: No booking file found, no conflicts");
                return false; // No conflicts if no bookings exist
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            int recordCount = 0;
            int excludedCount = 0;
            while (csvRead.readRecord()) {
                recordCount++;
                // Get booking ID to check if we should exclude this booking
                String recordBookingId = null;
                try {
                    recordBookingId = csvRead.get(0); // BookingID is first column
                    if (recordBookingId != null) {
                        recordBookingId = recordBookingId.trim();
                    }
                } catch (Exception e) {
                    // Try by name as fallback
                    try {
                        recordBookingId = csvRead.get("BookingID");
                        if (recordBookingId != null) {
                            recordBookingId = recordBookingId.trim();
                        }
                    } catch (Exception e2) {
                        // Ignore
                    }
                }
                
                String recordRoomNumber = csvRead.get("Room Number");
                String recordDate = csvRead.get("Booking Date");
                String recordStartTime = csvRead.get("Booking Start Time");
                
                // Skip empty records
                if (recordRoomNumber == null || recordRoomNumber.trim().isEmpty() ||
                    recordDate == null || recordDate.trim().isEmpty()) {
                    System.out.println("hasTimeConflict: Record " + recordCount + " - Skipping empty record");
                    continue;
                }
                
                // Skip if this is the booking we're excluding (check AFTER getting room/date to ensure we have the record)
                if (excludeBookingId != null && recordBookingId != null) {
                    String trimmedExclude = excludeBookingId.trim();
                    String trimmedRecord = recordBookingId.trim();
                    if (trimmedRecord.equals(trimmedExclude)) {
                        excludedCount++;
                        System.out.println("hasTimeConflict: Record " + recordCount + " - Excluding booking " + recordBookingId + 
                                         " (matches exclude ID: " + excludeBookingId + ")");
                        continue;
                    }
                }
                
                // Check if same room and same date
                if (recordRoomNumber != null && recordRoomNumber.equals(roomNumber) && 
                    recordDate != null && recordDate.equals(date)) {
                    System.out.println("hasTimeConflict: Record " + recordCount + " - Found matching room/date for booking " + recordBookingId);
                    
                    // Get end time from BookingDatabase.csv first (this is the source of truth)
                    String recordEndTime = null;
                    boolean endTimeReadFromCSV = false;
                    try {
                        recordEndTime = csvRead.get("Booking End Time");
                        if (recordEndTime != null) {
                            recordEndTime = recordEndTime.trim();
                            // Only use it if it's not empty
                            if (!recordEndTime.isEmpty()) {
                                endTimeReadFromCSV = true;
                                System.out.println("hasTimeConflict: Record " + recordCount + " - End time from BookingDatabase.csv: " + recordEndTime);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("hasTimeConflict: Record " + recordCount + " - Could not read end time column from CSV");
                    }
                    
                    // Only fall back to RoomDatabase if we didn't successfully read from BookingDatabase.csv
                    if (!endTimeReadFromCSV) {
                        System.out.println("hasTimeConflict: Record " + recordCount + " - End time not in BookingDatabase.csv, trying RoomDatabase");
                        try {
                            String roomIdStr = csvRead.get("RoomID");
                            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                                RoomCSV roomCSV = RoomCSV.getInstance();
                                UUID roomId = UUID.fromString(roomIdStr.trim());
                                Room room = roomCSV.findById(roomId);
                                if (room != null && room.getBookingEndTime() != null && !room.getBookingEndTime().trim().isEmpty()) {
                                    recordEndTime = room.getBookingEndTime().trim();
                                    System.out.println("hasTimeConflict: Record " + recordCount + " - End time from RoomDatabase: " + recordEndTime);
                                }
                            }
                        } catch (Exception e2) {
                            System.out.println("hasTimeConflict: Record " + recordCount + " - Could not get end time from RoomDatabase");
                        }
                    }
                    
                    // If end time still not available, calculate from start time (start + 1 hour) as last resort
                    if (recordEndTime == null || recordEndTime.trim().isEmpty()) {
                        recordEndTime = calculateEndTime(recordStartTime);
                        System.out.println("hasTimeConflict: Record " + recordCount + " - Calculated end time from start: " + recordEndTime);
                    }
                    
                    // Check for time overlap
                    if (recordEndTime != null && !recordEndTime.trim().isEmpty()) {
                        boolean overlaps = timesOverlap(startTime, endTime, recordStartTime, recordEndTime);
                        System.out.println("hasTimeConflict: Record " + recordCount + " - Checking overlap - Requested: " + startTime + "-" + endTime + 
                                         " vs Existing: " + recordStartTime + "-" + recordEndTime + " = " + overlaps);
                        if (overlaps) {
                            csvRead.close();
                            System.out.println("hasTimeConflict: CONFLICT FOUND with booking " + recordBookingId + 
                                             " (Room: " + recordRoomNumber + ", Time: " + recordStartTime + "-" + recordEndTime + ")");
                            return true; // Conflict found
                        } else {
                            System.out.println("hasTimeConflict: Record " + recordCount + " - No overlap with booking " + recordBookingId);
                        }
                    } else if (recordStartTime != null && recordStartTime.equals(startTime)) {
                        // If end time not available, check if start times match
                        csvRead.close();
                        System.out.println("hasTimeConflict: CONFLICT FOUND (start time match) with booking " + recordBookingId + 
                                         " (Room: " + recordRoomNumber + ", StartTime: " + recordStartTime + ")");
                        return true; // Conflict found
                    }
                } else {
                    System.out.println("hasTimeConflict: Record " + recordCount + " - Room/Date mismatch - Room: " + recordRoomNumber + 
                                     " (expected " + roomNumber + "), Date: " + recordDate + " (expected " + date + ")");
                }
            }
            csvRead.close();
            System.out.println("hasTimeConflict: Checked " + recordCount + " records (" + excludedCount + " excluded), no conflicts found");
        } catch (Exception e) {
            System.err.println("Error checking time conflict: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // No conflicts found
    }
    
    // Helper method to check if two time ranges overlap
    // Two ranges overlap if they share any common time (excluding exact boundaries)
    // e.g., 16:00-17:00 and 17:00-18:00 do NOT overlap (adjacent is OK)
    // e.g., 16:00-18:00 and 17:00-19:00 DO overlap (17:00-18:00 is shared)
    private boolean timesOverlap(String start1, String end1, String start2, String end2) {
        try {
            // Parse times (assuming format HH:MM)
            int start1Minutes = parseTimeToMinutes(start1);
            int end1Minutes = parseTimeToMinutes(end1);
            int start2Minutes = parseTimeToMinutes(start2);
            int end2Minutes = parseTimeToMinutes(end2);
            
            // Check for overlap: two ranges overlap if start1 < end2 AND start2 < end1
            // This means they share some common time (excluding exact boundaries)
            // Adjacent slots (e.g., 17:00-18:00 and 18:00-19:00) do NOT overlap
            boolean overlaps = start1Minutes < end2Minutes && start2Minutes < end1Minutes;
            System.out.println("timesOverlap: Range1=" + start1 + "-" + end1 + " (" + start1Minutes + "-" + end1Minutes + 
                             "), Range2=" + start2 + "-" + end2 + " (" + start2Minutes + "-" + end2Minutes + 
                             "), Overlaps=" + overlaps);
            return overlaps;
        } catch (Exception e) {
            System.err.println("Error in timesOverlap: " + e.getMessage());
            // If parsing fails, do simple string comparison
            return start1.equals(start2) || (end1 != null && end1.equals(end2));
        }
    }
    
    // Helper method to parse time string (HH:MM) to minutes since midnight
    private int parseTimeToMinutes(String time) {
        if (time == null || time.trim().isEmpty()) {
            return 0;
        }
        String[] parts = time.trim().split(":");
        if (parts.length >= 2) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours * 60 + minutes;
        }
        return 0;
    }
    
    public List<Booking> findByUserEmail(String email) {
        List<Booking> bookings = new ArrayList<>();
        try {
            // Check if file exists
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists()) {
                System.out.println("BookingDatabase.csv does not exist at: " + BOOKING_PATH);
                return bookings;
            }
            
            UserCSV userCSV = UserCSV.getInstance();
            Accounts account = userCSV.findByEmail(email);
            if (account == null) {
                System.out.println("User not found for email: " + email);
                return bookings;
            }
            
            String userId = account.getAccountId().toString();
            System.out.println("Looking for bookings with UserID: " + userId);
            
            // Verify file is readable
            if (!bookingFile.canRead()) {
                System.out.println("BookingDatabase.csv exists but cannot be read: " + BOOKING_PATH);
                return bookings;
            }
            
            System.out.println("Reading from BookingDatabase.csv at: " + BOOKING_PATH);
            System.out.println("File size: " + bookingFile.length() + " bytes");
            
            // Check if file is empty (only headers)
            if (bookingFile.length() < 100) {
                System.out.println("WARNING: BookingDatabase.csv appears to be empty or very small");
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            // Debug: Print headers to verify column names
            String[] headers = csvRead.getHeaders();
            System.out.println("CSV Headers: " + java.util.Arrays.toString(headers));
            
            // Verify we have the expected columns
            boolean hasRequiredColumns = false;
            for (String header : headers) {
                if (header.equals("Booking UserID")) {
                    hasRequiredColumns = true;
                    break;
                }
            }
            if (!hasRequiredColumns) {
                System.err.println("ERROR: CSV file does not have 'Booking UserID' column!");
                System.err.println("Expected columns: RoomID, Building Name, Room Number, Booking UserID, Booking Date, Booking Start Time");
                csvRead.close();
                return bookings;
            }
            
            int recordCount = 0;
            int matchCount = 0;
            
            while (csvRead.readRecord()) {
                recordCount++;
                
                // Skip empty records
                String recordUserId = csvRead.get("Booking UserID");
                String roomNumber = csvRead.get("Room Number");
                
                // Skip if both are empty (empty row)
                if ((recordUserId == null || recordUserId.trim().isEmpty()) && 
                    (roomNumber == null || roomNumber.trim().isEmpty())) {
                    System.out.println("Record " + recordCount + " - Skipping empty record");
                    continue;
                }
                
                System.out.println("Record " + recordCount + " - UserID: " + recordUserId + ", Room: " + roomNumber);
                
                if (recordUserId != null && !recordUserId.trim().isEmpty() && recordUserId.equals(userId)) {
                    matchCount++;
                    System.out.println("Match found! Parsing booking record " + matchCount);
                    Booking booking = parseBookingFromRecord(csvRead);
                    if (booking != null) {
                        bookings.add(booking);
                        System.out.println("Successfully parsed booking: " + booking.getBookingId());
                    } else {
                        System.out.println("Failed to parse booking record");
                    }
                }
            }
            csvRead.close();
            
            System.out.println("Total records read: " + recordCount + ", Matches: " + matchCount + ", Bookings added: " + bookings.size());
        } catch (Exception e) {
            System.err.println("Error reading bookings from CSV: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    // Simple method to get all booking records directly from CSV without user lookup
    public List<Map<String, String>> getAllBookingRecords() {
        List<Map<String, String>> records = new ArrayList<>();
        try {
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                System.out.println("BookingDatabase.csv does not exist or cannot be read at: " + BOOKING_PATH);
                return records;
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            // Debug: Print headers to verify column names
            String[] headers = csvRead.getHeaders();
            System.out.println("BookingDatabase.csv Headers: " + java.util.Arrays.toString(headers));
            
            while (csvRead.readRecord()) {
                // Read BookingID from CSV - it's the first column (index 0)
                String bookingId = null;
                try {
                    // Read directly by index 0 (first column) to ensure we get the right value
                    bookingId = csvRead.get(0);
                    
                    // Clean up the value
                    if (bookingId != null) {
                        bookingId = bookingId.trim();
                        if (bookingId.isEmpty()) {
                            bookingId = null;
                        }
                    }
                    
                    System.out.println("Read BookingID from CSV (column index 0): '" + bookingId + "'");
                } catch (Exception e) {
                    System.err.println("Error reading BookingID from column 0: " + e.getMessage());
                    // Try by name as fallback
                    try {
                        bookingId = csvRead.get("BookingID");
                        if (bookingId != null) {
                            bookingId = bookingId.trim();
                            if (bookingId.isEmpty()) {
                                bookingId = null;
                            }
                        }
                        System.out.println("Read BookingID by name: '" + bookingId + "'");
                    } catch (Exception e2) {
                        System.err.println("Could not read BookingID by name either");
                    }
                }
                
                String roomNumber = csvRead.get("Room Number");
                String bookingDate = csvRead.get("Booking Date");
                String bookingStartTime = csvRead.get("Booking Start Time");
                String roomId = csvRead.get("RoomID");
                String buildingName = csvRead.get("Building Name");
                String userId = csvRead.get("Booking UserID");
                
                // Skip empty records
                if ((roomNumber == null || roomNumber.trim().isEmpty()) && 
                    (bookingDate == null || bookingDate.trim().isEmpty())) {
                    continue;
                }
                
                Map<String, String> record = new HashMap<>();
                
                // Debug: Print what we read
                System.out.println("DEBUG - bookingId: '" + bookingId + "', roomId: '" + roomId + "'");
                
                // Use BookingID from CSV, or fallback to RoomID substring if not available
                if (bookingId != null && !bookingId.trim().isEmpty() && !bookingId.equals("null")) {
                    String finalBookingId = bookingId.trim();
                    record.put("bookingId", finalBookingId);
                    System.out.println("✓ Using BookingID from CSV: '" + finalBookingId + "'");
                } else {
                    // Fallback: use RoomID substring only if BookingID is truly not available
                    String fallbackId = roomId != null && !roomId.trim().isEmpty() ? roomId.substring(0, Math.min(8, roomId.length())) : "N/A";
                    record.put("bookingId", fallbackId);
                    System.out.println("✗ WARNING: BookingID not found or empty, using RoomID fallback: '" + fallbackId + "'");
                    System.out.println("  bookingId was: '" + bookingId + "'");
                }
                record.put("roomId", roomId != null ? roomId : "N/A");
                record.put("buildingName", buildingName != null ? buildingName : "N/A");
                record.put("roomNumber", roomNumber != null ? roomNumber : "N/A");
                record.put("date", bookingDate != null ? bookingDate : "N/A");
                record.put("startTime", bookingStartTime != null ? bookingStartTime : "N/A");
                
                // Get end time from CSV first, then fallback to RoomDatabase.csv or calculate from start time
                String endTime = null;
                try {
                    endTime = csvRead.get("Booking End Time");
                    if (endTime != null) {
                        endTime = endTime.trim();
                    }
                } catch (Exception e) {
                    // Column might not exist in older CSV files
                }
                
                // If end time not in CSV, try to get from RoomDatabase.csv
                if ((endTime == null || endTime.isEmpty()) && roomId != null && !roomId.trim().isEmpty()) {
                    try {
                        RoomCSV roomCSV = RoomCSV.getInstance();
                        UUID roomUUID = UUID.fromString(roomId.trim());
                        Room room = roomCSV.findById(roomUUID);
                        if (room != null) {
                            endTime = room.getBookingEndTime();
                        }
                    } catch (Exception e) {
                        // If room lookup fails, continue
                    }
                }
                
                // If still no end time, calculate from start time (start time + 1 hour)
                if (endTime == null || endTime.isEmpty()) {
                    endTime = calculateEndTime(bookingStartTime);
                }
                
                record.put("endTime", endTime != null && !endTime.isEmpty() ? endTime : "N/A");
                
                // Get status from RoomDatabase.csv if roomId exists
                // Only use room condition if this booking's time slot matches the room's current booking
                String status = "Reserved";
                if (roomId != null && !roomId.trim().isEmpty()) {
                    try {
                        RoomCSV roomCSV = RoomCSV.getInstance();
                        UUID roomUUID = UUID.fromString(roomId.trim());
                        Room room = roomCSV.findById(roomUUID);
                        if (room != null) {
                            // Get the booking ID for this record (use the one we read earlier)
                            String currentBookingId = bookingId != null ? bookingId.trim() : null;
                            
                            // Check if this booking's time slot matches the room's current booking
                            String roomBookingId = room.getBookingId();
                            String roomBookingDate = room.getBookingDate();
                            String roomBookingStartTime = room.getBookingStartTime();
                            
                            // Only use room condition if this booking matches the room's current booking
                            boolean bookingMatchesRoom = (currentBookingId != null && roomBookingId != null && roomBookingId.equals(currentBookingId)) &&
                                                         (roomBookingDate != null && roomBookingDate.equals(bookingDate)) &&
                                                         (roomBookingStartTime != null && roomBookingStartTime.equals(bookingStartTime));
                            
                            if (bookingMatchesRoom) {
                                // This booking matches the room's current booking, use room condition
                                String roomCondition = room.getCondition();
                                if (roomCondition != null) {
                                    switch (roomCondition) {
                                        case "InUse": status = "InUse"; break;
                                        case "Available": status = "Completed"; break;
                                        case "Reserved": status = "Reserved"; break;
                                        default: status = "Reserved";
                                    }
                                }
                            } else {
                                // This booking doesn't match the room's current booking
                                // Room might be Available due to another booking being cancelled
                                // Default to Reserved for this booking
                                status = "Reserved";
                            }
                        }
                    } catch (Exception e) {
                        // If room lookup fails, use defaults
                    }
                }
                
                record.put("status", status);
                
                records.add(record);
            }
            csvRead.close();
        } catch (Exception e) {
            System.err.println("Error reading booking records from CSV: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }
    
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        try {
            // Check if file exists
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists()) {
                System.out.println("BookingDatabase.csv does not exist at: " + BOOKING_PATH);
                return bookings;
            }
            
            // Verify file is readable
            if (!bookingFile.canRead()) {
                System.out.println("BookingDatabase.csv exists but cannot be read: " + BOOKING_PATH);
                return bookings;
            }
            
            System.out.println("findAll: Reading from BookingDatabase.csv at: " + BOOKING_PATH);
            System.out.println("findAll: File size: " + bookingFile.length() + " bytes");
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            // Debug: Print headers
            String[] headers = csvRead.getHeaders();
            System.out.println("findAll: CSV Headers: " + java.util.Arrays.toString(headers));
            
            int recordCount = 0;
            while (csvRead.readRecord()) {
                recordCount++;
                
                // Skip empty records
                String recordUserId = csvRead.get("Booking UserID");
                String roomNumber = csvRead.get("Room Number");
                
                // Skip if both are empty (empty row)
                if ((recordUserId == null || recordUserId.trim().isEmpty()) && 
                    (roomNumber == null || roomNumber.trim().isEmpty())) {
                    System.out.println("findAll: Record " + recordCount + " - Skipping empty record");
                    continue;
                }
                
                System.out.println("findAll: Record " + recordCount + " - UserID: " + recordUserId + ", Room: " + roomNumber);
                
                Booking booking = parseBookingFromRecord(csvRead);
                if (booking != null) {
                    bookings.add(booking);
                    System.out.println("findAll: Successfully parsed booking: " + booking.getBookingId());
                } else {
                    System.out.println("findAll: Failed to parse booking record " + recordCount);
                }
            }
            csvRead.close();
            System.out.println("findAll: Read " + recordCount + " records, parsed " + bookings.size() + " bookings");
        } catch (Exception e) {
            System.err.println("Error in findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    public void update(Booking updatedBooking) {
        System.out.println("BookingCSV.update: Called with booking ID: " + updatedBooking.getBookingId());
        System.out.println("BookingCSV.update: Booking end time: " + updatedBooking.getBookingEndTime());
        try {
            // Read all bookings
            List<Map<String, String>> allBookings = new ArrayList<>();
            File bookingFile = new File(BOOKING_PATH);
            
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                System.err.println("BookingDatabase.csv does not exist or cannot be read for update");
                return;
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            boolean found = false;
            int recordCount = 0;
            while (csvRead.readRecord()) {
                recordCount++;
                String recordBookingId = csvRead.get(0); // BookingID is first column
                
                // Skip empty records
                if (recordBookingId == null || recordBookingId.trim().isEmpty()) {
                    System.out.println("BookingCSV.update: Record " + recordCount + " - Skipping empty booking ID");
                    continue;
                }
                
                System.out.println("BookingCSV.update: Record " + recordCount + " - Comparing '" + recordBookingId.trim() + "' with '" + updatedBooking.getBookingId() + "'");
                
                // If this is the booking to update, use updated values
                if (recordBookingId.trim().equals(updatedBooking.getBookingId())) {
                    System.out.println("BookingCSV.update: ✓ Found matching booking to update!");
                    found = true;
                    // Create updated record
                    Room room = findRoomByNumber(updatedBooking.getRoomNumber());
                    Map<String, String> booking = new HashMap<>();
                    booking.put("bookingId", updatedBooking.getBookingId());
                    booking.put("roomId", room != null ? room.getRoomId().toString() : "");
                    booking.put("buildingName", room != null ? room.getBuildingName() : "");
                    booking.put("roomNumber", updatedBooking.getRoomNumber());
                    booking.put("userId", updatedBooking.getUser().getAccountId().toString());
                    booking.put("date", updatedBooking.getBookingDate());
                    booking.put("startTime", updatedBooking.getBookingStartTime());
                    // Use the updated end time
                    String updatedEndTime = updatedBooking.getBookingEndTime() != null ? updatedBooking.getBookingEndTime() : "";
                    booking.put("endTime", updatedEndTime);
                    System.out.println("BookingCSV.update: Updating booking " + updatedBooking.getBookingId() + 
                                     " with new end time: " + updatedEndTime);
                    allBookings.add(booking);
                } else {
                    // Keep existing booking
                    Map<String, String> booking = new HashMap<>();
                    booking.put("bookingId", recordBookingId);
                    booking.put("roomId", csvRead.get("RoomID"));
                    booking.put("buildingName", csvRead.get("Building Name"));
                    booking.put("roomNumber", csvRead.get("Room Number"));
                    booking.put("userId", csvRead.get("Booking UserID"));
                    booking.put("date", csvRead.get("Booking Date"));
                    booking.put("startTime", csvRead.get("Booking Start Time"));
                    // Get end time from CSV, or calculate from start time if not present
                    String endTime = csvRead.get("Booking End Time");
                    if (endTime == null || endTime.trim().isEmpty()) {
                        endTime = calculateEndTime(csvRead.get("Booking Start Time"));
                    }
                    booking.put("endTime", endTime != null ? endTime : "");
                    allBookings.add(booking);
                }
            }
            csvRead.close();
            
            if (!found) {
                System.err.println("Booking not found for update: " + updatedBooking.getBookingId());
                return;
            }
            
            // Rewrite the file with updated booking
            System.out.println("BookingCSV.update: Writing to file: " + BOOKING_PATH);
            System.out.println("BookingCSV.update: Total bookings to write: " + allBookings.size());
            CsvWriter csvWrite = new CsvWriter(new FileWriter(BOOKING_PATH, false), ',');
            csvWrite.write("BookingID");
            csvWrite.write("RoomID");
            csvWrite.write("Building Name");
            csvWrite.write("Room Number");
            csvWrite.write("Booking UserID");
            csvWrite.write("Booking Date");
            csvWrite.write("Booking Start Time");
            csvWrite.write("Booking End Time");
            csvWrite.endRecord();

            for (Map<String, String> booking : allBookings) {
                String bookingId = booking.get("bookingId") != null ? booking.get("bookingId") : "";
                String endTime = booking.get("endTime") != null ? booking.get("endTime") : "";
                String startTime = booking.get("startTime") != null ? booking.get("startTime") : "";
                csvWrite.write(bookingId);
                csvWrite.write(booking.get("roomId") != null ? booking.get("roomId") : "");
                csvWrite.write(booking.get("buildingName") != null ? booking.get("buildingName") : "");
                csvWrite.write(booking.get("roomNumber") != null ? booking.get("roomNumber") : "");
                csvWrite.write(booking.get("userId") != null ? booking.get("userId") : "");
                csvWrite.write(booking.get("date") != null ? booking.get("date") : "");
                csvWrite.write(startTime);
                csvWrite.write(endTime); // Write the end time
                csvWrite.endRecord();
                if (bookingId.equals(updatedBooking.getBookingId())) {
                    System.out.println("BookingCSV.update: ✓ Wrote booking " + bookingId + " - Start: " + startTime + ", End: " + endTime);
                } else {
                    System.out.println("BookingCSV.update: Wrote booking " + bookingId + " - Start: " + startTime + ", End: " + endTime);
                }
            }
            csvWrite.close();
            System.out.println("BookingCSV.update: File write completed successfully");
            
            // Update RoomDatabase.csv when booking changes
            if (updatedBooking.getRoomNumber() != null && !updatedBooking.getRoomNumber().isEmpty()) {
                Room room = findRoomByNumber(updatedBooking.getRoomNumber());
                if (room != null) {
                    updateRoomDatabase(room, updatedBooking);
                }
            }
            
            System.out.println("Successfully updated booking in BookingDatabase.csv: " + updatedBooking.getBookingId());
        } catch (Exception e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Get bookings for a specific room and date
    public List<Map<String, String>> getBookingsForRoomAndDate(String roomNumber, String date) {
        List<Map<String, String>> bookings = new ArrayList<>();
        try {
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                return bookings;
            }
            
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                String recordRoomNumber = csvRead.get("Room Number");
                String recordDate = csvRead.get("Booking Date");
                String recordStartTime = csvRead.get("Booking Start Time");
                
                // Skip empty records
                if (recordRoomNumber == null || recordRoomNumber.trim().isEmpty() ||
                    recordDate == null || recordDate.trim().isEmpty()) {
                    continue;
                }
                
                // Debug output
                System.out.println("getBookingsForRoomAndDate: Checking record - Room: '" + recordRoomNumber + 
                                 "', Date: '" + recordDate + "', StartTime: '" + recordStartTime + "'");
                System.out.println("getBookingsForRoomAndDate: Looking for - Room: '" + roomNumber + 
                                 "', Date: '" + date + "'");
                
                // Check if this booking matches the room and date (trim for comparison)
                String trimmedRecordRoom = recordRoomNumber.trim();
                String trimmedRecordDate = recordDate.trim();
                String trimmedRoomNumber = roomNumber != null ? roomNumber.trim() : "";
                String trimmedDate = date != null ? date.trim() : "";
                
                if (trimmedRecordRoom.equals(trimmedRoomNumber) && trimmedRecordDate.equals(trimmedDate)) {
                    System.out.println("getBookingsForRoomAndDate: MATCH FOUND! Adding booking with startTime: " + recordStartTime);
                    Map<String, String> booking = new HashMap<>();
                    booking.put("startTime", recordStartTime != null ? recordStartTime.trim() : "");
                    
                    // Get end time from BookingDatabase.csv FIRST (this is the source of truth)
                    String recordEndTime = null;
                    try {
                        recordEndTime = csvRead.get("Booking End Time");
                        if (recordEndTime != null) {
                            recordEndTime = recordEndTime.trim();
                            System.out.println("getBookingsForRoomAndDate: Found endTime from BookingDatabase.csv: " + recordEndTime);
                        }
                    } catch (Exception e) {
                        System.out.println("getBookingsForRoomAndDate: Could not read end time from BookingDatabase.csv, trying RoomDatabase");
                    }
                    
                    // If end time not in BookingDatabase.csv, try RoomDatabase.csv as fallback
                    if ((recordEndTime == null || recordEndTime.isEmpty())) {
                        try {
                            String roomIdStr = csvRead.get("RoomID");
                            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                                RoomCSV roomCSV = RoomCSV.getInstance();
                                UUID roomId = UUID.fromString(roomIdStr.trim());
                                Room room = roomCSV.findById(roomId);
                                if (room != null) {
                                    recordEndTime = room.getBookingEndTime();
                                    System.out.println("getBookingsForRoomAndDate: Found endTime from RoomDatabase: " + recordEndTime);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("getBookingsForRoomAndDate: Error getting end time from RoomDatabase: " + e.getMessage());
                        }
                    }
                    
                    // If still no end time, calculate from start time (start + 1 hour)
                    if (recordEndTime == null || recordEndTime.isEmpty()) {
                        recordEndTime = calculateEndTime(recordStartTime);
                        System.out.println("getBookingsForRoomAndDate: Calculated endTime from start time: " + recordEndTime);
                    }
                    
                    booking.put("endTime", recordEndTime != null ? recordEndTime.trim() : "");
                    bookings.add(booking);
                }
            }
            csvRead.close();
        } catch (Exception e) {
            System.err.println("Error getting bookings for room and date: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
    
    private Booking parseBookingFromRecord(CsvReader csvRead) {
        try {
            String userIdStr = csvRead.get("Booking UserID");
            String roomNumber = csvRead.get("Room Number");
            String bookingDate = csvRead.get("Booking Date");
            String bookingStartTime = csvRead.get("Booking Start Time");
            String roomIdStr = csvRead.get("RoomID");
            
            // Validate required fields
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                System.out.println("parseBookingFromRecord: Missing Booking UserID");
                return null;
            }
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                System.out.println("parseBookingFromRecord: Missing Room Number");
                return null;
            }
            
            // Get user from database by ID
            UserCSV userCSV = UserCSV.getInstance();
            UUID userId;
            try {
                userId = UUID.fromString(userIdStr.trim());
            } catch (IllegalArgumentException e) {
                System.err.println("parseBookingFromRecord: Invalid UserID format: " + userIdStr);
                return null;
            }
            
            Accounts account = userCSV.find(userId);
            
            if (account == null || !(account instanceof User)) {
                System.err.println("parseBookingFromRecord: User not found for ID: " + userIdStr);
                System.err.println("parseBookingFromRecord: This booking cannot be displayed - user ID doesn't exist in user database");
                return null;
            }
            
            User user = (User) account;
            
            // Get end time from CSV first, then fallback to RoomDatabase.csv if not present
            String bookingEndTime = null;
            try {
                bookingEndTime = csvRead.get("Booking End Time");
                if (bookingEndTime != null) {
                    bookingEndTime = bookingEndTime.trim();
                }
            } catch (Exception e) {
                // Column might not exist in older CSV files
            }
            
            // If end time not in CSV, calculate from start time (start time + 1 hour)
            if (bookingEndTime == null || bookingEndTime.isEmpty()) {
                bookingEndTime = calculateEndTime(bookingStartTime);
            }
            
            // Get status from RoomDatabase.csv
            // Only use room condition if this booking's time slot matches the room's current booking
            String status = "Reserved"; // Default status
            
            if (roomIdStr != null && !roomIdStr.isEmpty()) {
                try {
                    RoomCSV roomCSV = RoomCSV.getInstance();
                    UUID roomId = UUID.fromString(roomIdStr);
                    Room room = roomCSV.findById(roomId);
                    
                    if (room != null) {
                        // Read booking ID from CSV to check if this booking matches the room's current booking
                        String recordBookingId = null;
                        try {
                            recordBookingId = csvRead.get(0); // BookingID is first column
                            if (recordBookingId != null) {
                                recordBookingId = recordBookingId.trim();
                            }
                        } catch (Exception e2) {
                            // Try by name as fallback
                            try {
                                recordBookingId = csvRead.get("BookingID");
                                if (recordBookingId != null) {
                                    recordBookingId = recordBookingId.trim();
                                }
                            } catch (Exception e3) {
                                // Ignore
                            }
                        }
                        
                        // Check if this booking's time slot matches the room's current booking
                        String roomBookingId = room.getBookingId();
                        String roomBookingDate = room.getBookingDate();
                        String roomBookingStartTime = room.getBookingStartTime();
                        
                        // Only use room condition if this booking matches the room's current booking
                        boolean bookingMatchesRoom = (recordBookingId != null && roomBookingId != null && roomBookingId.equals(recordBookingId)) &&
                                                     (roomBookingDate != null && roomBookingDate.equals(bookingDate)) &&
                                                     (roomBookingStartTime != null && roomBookingStartTime.equals(bookingStartTime));
                        
                        if (bookingMatchesRoom) {
                            // This booking matches the room's current booking, use room condition
                            String roomCondition = room.getCondition();
                            if (roomCondition != null) {
                                switch (roomCondition) {
                                    case "InUse":
                                        status = "InUse";
                                        break;
                                    case "Available":
                                        status = "Completed";
                                        break;
                                    case "Reserved":
                                        status = "Reserved";
                                        break;
                                    default:
                                        status = "Reserved";
                                }
                            }
                        } else {
                            // This booking doesn't match the room's current booking
                            // Room might be Available due to another booking being cancelled
                            // Default to Reserved for this booking
                            status = "Reserved";
                        }
                    }
                } catch (Exception e) {
                    // If room lookup fails, use defaults
                    System.err.println("Error looking up room for booking: " + e.getMessage());
                }
            }
            
            // Calculate hours from start and end time
            int hours = 1; // Default
            if (bookingStartTime != null && bookingEndTime != null && !bookingEndTime.isEmpty()) {
                try {
                    String[] startParts = bookingStartTime.split(":");
                    String[] endParts = bookingEndTime.split(":");
                    int startHour = Integer.parseInt(startParts[0]);
                    int startMin = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
                    int endHour = Integer.parseInt(endParts[0]);
                    int endMin = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 0;
                    
                    int startTotalMinutes = startHour * 60 + startMin;
                    int endTotalMinutes = endHour * 60 + endMin;
                    int diffMinutes = endTotalMinutes - startTotalMinutes;
                    
                    hours = (int) Math.ceil(diffMinutes / 60.0);
                    if (hours < 1) hours = 1;
                } catch (Exception e) {
                    // If parsing fails, use default
                    hours = 1;
                }
            }
            
            double rate = user.getHourlyRate();
            
            // Read BookingID from CSV (first column, index 0)
            String bookingId = null;
            try {
                bookingId = csvRead.get(0); // BookingID is first column
                if (bookingId != null) {
                    bookingId = bookingId.trim();
                }
            } catch (Exception e) {
                // Try by name as fallback
                try {
                    bookingId = csvRead.get("BookingID");
                    if (bookingId != null) {
                        bookingId = bookingId.trim();
                    }
                } catch (Exception e2) {
                    // If still can't read, generate one as fallback
                    bookingId = "B" + (roomIdStr != null && roomIdStr.length() >= 8 ? 
                                     roomIdStr.substring(0, 8).toUpperCase() : 
                                     userIdStr.substring(0, 8).toUpperCase());
                    System.err.println("parseBookingFromRecord: Could not read BookingID from CSV, generated: " + bookingId);
                }
            }
            
            // If booking ID is still null or empty, generate one as fallback
            if (bookingId == null || bookingId.isEmpty()) {
                bookingId = "B" + (roomIdStr != null && roomIdStr.length() >= 8 ? 
                                 roomIdStr.substring(0, 8).toUpperCase() : 
                                 userIdStr.substring(0, 8).toUpperCase());
                System.err.println("parseBookingFromRecord: BookingID was null/empty, generated: " + bookingId);
            }
            
            System.out.println("parseBookingFromRecord: Using BookingID: " + bookingId);
            
            Booking booking = new Booking(bookingId, user, hours, rate, 
                                         roomNumber, bookingDate, bookingStartTime, bookingEndTime);
            booking.setStatus(status);
            return booking;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Delete a booking by BookingID from BookingDatabase.csv
    public boolean deleteBooking(String bookingId) {
        try {
            File bookingFile = new File(BOOKING_PATH);
            if (!bookingFile.exists() || !bookingFile.canRead()) {
                System.err.println("BookingDatabase.csv does not exist or cannot be read");
                return false;
            }
            
            // Read all bookings
            List<Map<String, String>> allBookings = new ArrayList<>();
            CsvReader csvRead = new CsvReader(BOOKING_PATH);
            csvRead.readHeaders();
            
            String roomIdToClear = null;
            String roomNumberToClear = null;
            
            while (csvRead.readRecord()) {
                String recordBookingId = csvRead.get(0); // BookingID is first column
                
                // Skip empty records
                if (recordBookingId == null || recordBookingId.trim().isEmpty()) {
                    continue;
                }
                
                // If this is the booking to delete, remember the room info for clearing
                if (recordBookingId.trim().equals(bookingId.trim())) {
                    roomIdToClear = csvRead.get("RoomID");
                    roomNumberToClear = csvRead.get("Room Number");
                    System.out.println("Found booking to delete: " + bookingId + ", Room: " + roomNumberToClear);
                    continue; // Skip this booking (don't add to allBookings)
                }
                
                // Keep all other bookings
                Map<String, String> booking = new HashMap<>();
                booking.put("bookingId", recordBookingId);
                booking.put("roomId", csvRead.get("RoomID"));
                booking.put("buildingName", csvRead.get("Building Name"));
                booking.put("roomNumber", csvRead.get("Room Number"));
                booking.put("userId", csvRead.get("Booking UserID"));
                booking.put("date", csvRead.get("Booking Date"));
                booking.put("startTime", csvRead.get("Booking Start Time"));
                // Get end time from CSV, or calculate from start time if not present
                String endTime = csvRead.get("Booking End Time");
                if (endTime == null || endTime.trim().isEmpty()) {
                    endTime = calculateEndTime(csvRead.get("Booking Start Time"));
                }
                booking.put("endTime", endTime != null ? endTime : "");
                allBookings.add(booking);
            }
            csvRead.close();
            
            // Rewrite the file without the deleted booking
            CsvWriter csvWrite = new CsvWriter(new FileWriter(BOOKING_PATH, false), ',');
            csvWrite.write("BookingID");
            csvWrite.write("RoomID");
            csvWrite.write("Building Name");
            csvWrite.write("Room Number");
            csvWrite.write("Booking UserID");
            csvWrite.write("Booking Date");
            csvWrite.write("Booking Start Time");
            csvWrite.write("Booking End Time");
            csvWrite.endRecord();
            
            for (Map<String, String> booking : allBookings) {
                csvWrite.write(booking.get("bookingId") != null ? booking.get("bookingId") : "");
                csvWrite.write(booking.get("roomId") != null ? booking.get("roomId") : "");
                csvWrite.write(booking.get("buildingName") != null ? booking.get("buildingName") : "");
                csvWrite.write(booking.get("roomNumber") != null ? booking.get("roomNumber") : "");
                csvWrite.write(booking.get("userId") != null ? booking.get("userId") : "");
                csvWrite.write(booking.get("date") != null ? booking.get("date") : "");
                csvWrite.write(booking.get("startTime") != null ? booking.get("startTime") : "");
                
                // Calculate end time from start time if not already stored
                String endTime = booking.get("endTime");
                if (endTime == null || endTime.trim().isEmpty()) {
                    endTime = calculateEndTime(booking.get("startTime"));
                }
                csvWrite.write(endTime != null ? endTime : "");
                
                csvWrite.endRecord();
            }
            csvWrite.close();
            
            // Clear booking info from RoomDatabase.csv if this was the only booking for that room
            if (roomIdToClear != null && !roomIdToClear.trim().isEmpty()) {
                try {
                    RoomCSV roomCSV = RoomCSV.getInstance();
                    UUID roomId = UUID.fromString(roomIdToClear.trim());
                    Room room = roomCSV.findById(roomId);
                    if (room != null) {
                        // Check if there are other bookings for this room
                        boolean hasOtherBookings = false;
                        for (Map<String, String> booking : allBookings) {
                            if (booking.get("roomId") != null && booking.get("roomId").equals(roomIdToClear)) {
                                hasOtherBookings = true;
                                break;
                            }
                        }
                        
                        // If no other bookings, clear the booking info from the room
                        if (!hasOtherBookings) {
                            room.getRoomContext().clearBookingInfo();
                            // Set room state back to Available if it was Reserved
                            if (room.getCondition().equals("Reserved")) {
                                room.getRoomContext().setState(AvailableState.getInstance());
                            }
                            roomCSV.update(room);
                            System.out.println("Cleared booking info from room: " + roomNumberToClear);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error clearing room booking info: " + e.getMessage());
                    // Continue even if room update fails
                }
            }
            
            System.out.println("Successfully deleted booking: " + bookingId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}


