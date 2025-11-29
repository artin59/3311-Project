package Backend;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class RoomCSV {
    
    private static RoomCSV instance = new RoomCSV();
    private final String PATH = "../RoomDatabase.csv";
    
    private RoomCSV() {
        try {
            File file = new File(PATH);
            
            if (!file.exists()) {
                // Create directory if it doesn't exist
                file.getParentFile().mkdirs();
                
                CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
                csvWrite.write("Room ID");
                csvWrite.write("Capacity");
                csvWrite.write("Building Name");
                csvWrite.write("Room Number");
                csvWrite.write("Status");
                csvWrite.write("Condition");
                csvWrite.write("Booking ID");
                csvWrite.write("Booking User ID");
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
    
    public static RoomCSV getInstance() {
        return instance;
    }
    
    public void write(Room room) {
        try {
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, true), ',');
            csvWrite.write(String.valueOf(room.getRoomId()));
            csvWrite.write(String.valueOf(room.getCapacity()));
            csvWrite.write(room.getBuildingName());
            csvWrite.write(room.getRoomNumber());
            csvWrite.write(room.getStatus());
            csvWrite.write(room.getCondition());
            csvWrite.write(room.getBookingId() != null ? room.getBookingId() : "");
            csvWrite.write(room.getBookingUserId() != null ? String.valueOf(room.getBookingUserId()) : "");
            csvWrite.write(room.getBookingDate() != null ? room.getBookingDate() : "");
            csvWrite.write(room.getBookingStartTime() != null ? room.getBookingStartTime() : "");
            csvWrite.write(room.getBookingEndTime() != null ? room.getBookingEndTime() : "");
            csvWrite.endRecord();
            csvWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Room findById(UUID roomId) {
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                if (csvRead.get("Room ID").equals(String.valueOf(roomId))) {
                    Room room = parseRoomFromRecord(csvRead);
                    csvRead.close();
                    return room;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Room findByLocation(String buildingName, String roomNumber) {
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                if (csvRead.get("Building Name").equalsIgnoreCase(buildingName) &&
                    csvRead.get("Room Number").equalsIgnoreCase(roomNumber)) {
                    Room room = parseRoomFromRecord(csvRead);
                    csvRead.close();
                    return room;
                }
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try {
            CsvReader csvRead = new CsvReader(PATH);
            csvRead.readHeaders();
            
            while (csvRead.readRecord()) {
                Room room = parseRoomFromRecord(csvRead);
                rooms.add(room);
            }
            csvRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public boolean roomExists(String buildingName, String roomNumber) {
        return findByLocation(buildingName, roomNumber) != null;
    }
    
    public void update(Room updatedRoom) {
        List<Room> allRooms = findAll();
        
        try {
            // Rewrite entire file with headers
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
            csvWrite.write("Room ID");
            csvWrite.write("Capacity");
            csvWrite.write("Building Name");
            csvWrite.write("Room Number");
            csvWrite.write("Status");
            csvWrite.write("Condition");
            csvWrite.write("Booking ID");
            csvWrite.write("Booking User ID");
            csvWrite.write("Booking Date");
            csvWrite.write("Booking Start Time");
            csvWrite.write("Booking End Time");
            csvWrite.endRecord();
            
            for (Room room : allRooms) {
                if (room.getRoomId().equals(updatedRoom.getRoomId())) {
                    // Write updated room
                    writeRoomRecord(csvWrite, updatedRoom);
                } else {
                    // Write existing room
                    writeRoomRecord(csvWrite, room);
                }
            }
            csvWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void delete(UUID roomId) {
        List<Room> allRooms = findAll();
        
        try {
            CsvWriter csvWrite = new CsvWriter(new FileWriter(PATH, false), ',');
            csvWrite.write("Room ID");
            csvWrite.write("Capacity");
            csvWrite.write("Building Name");
            csvWrite.write("Room Number");
            csvWrite.write("Status");
            csvWrite.write("Condition");
            csvWrite.write("Booking ID");
            csvWrite.write("Booking User ID");
            csvWrite.write("Booking Date");
            csvWrite.write("Booking Start Time");
            csvWrite.write("Booking End Time");
            csvWrite.endRecord();
            
            for (Room room : allRooms) {
                if (!room.getRoomId().equals(roomId)) {
                    writeRoomRecord(csvWrite, room);
                }
            }
            csvWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Room parseRoomFromRecord(CsvReader csvRead) throws Exception {
        UUID roomId = UUID.fromString(csvRead.get("Room ID"));
        int capacity = Integer.parseInt(csvRead.get("Capacity"));
        String buildingName = csvRead.get("Building Name");
        String roomNumber = csvRead.get("Room Number");
        String status = csvRead.get("Status");
        String condition = csvRead.get("Condition");
        
        // Try to read Booking ID (may not exist in old CSV files)
        String bookingId = null;
        try {
            bookingId = csvRead.get("Booking ID");
            if (bookingId != null && bookingId.trim().isEmpty()) {
                bookingId = null;
            }
        } catch (Exception e) {
            // Column doesn't exist in old CSV files, leave as null
        }
        
        String bookingUserIdStr = csvRead.get("Booking User ID");
        UUID bookingUserId = (bookingUserIdStr != null && !bookingUserIdStr.isEmpty()) 
                             ? UUID.fromString(bookingUserIdStr) : null;
        
        String bookingDate = csvRead.get("Booking Date");
        bookingDate = (bookingDate != null && !bookingDate.isEmpty()) ? bookingDate : null;
        
        String bookingStartTime = csvRead.get("Booking Start Time");
        bookingStartTime = (bookingStartTime != null && !bookingStartTime.isEmpty()) ? bookingStartTime : null;
        
        String bookingEndTime = csvRead.get("Booking End Time");
        bookingEndTime = (bookingEndTime != null && !bookingEndTime.isEmpty()) ? bookingEndTime : null;
        
        return new Room(roomId, capacity, buildingName, roomNumber, status, condition,
                        bookingId, bookingUserId, bookingDate, bookingStartTime, bookingEndTime);
    }
    
    private void writeRoomRecord(CsvWriter csvWrite, Room room) throws Exception {
        csvWrite.write(String.valueOf(room.getRoomId()));
        csvWrite.write(String.valueOf(room.getCapacity()));
        csvWrite.write(room.getBuildingName());
        csvWrite.write(room.getRoomNumber());
        csvWrite.write(room.getStatus());
        csvWrite.write(room.getCondition());
        csvWrite.write(room.getBookingId() != null ? room.getBookingId() : "");
        csvWrite.write(room.getBookingUserId() != null ? String.valueOf(room.getBookingUserId()) : "");
        csvWrite.write(room.getBookingDate() != null ? room.getBookingDate() : "");
        csvWrite.write(room.getBookingStartTime() != null ? room.getBookingStartTime() : "");
        csvWrite.write(room.getBookingEndTime() != null ? room.getBookingEndTime() : "");
        csvWrite.endRecord();
    }
}