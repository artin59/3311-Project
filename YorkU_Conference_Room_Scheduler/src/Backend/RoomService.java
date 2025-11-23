package Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoomService {
    
    private RoomCSV roomCSV;
    
    public RoomService() {
        this.roomCSV = RoomCSV.getInstance();
    }
    
    // Create a new room (Admin function)
    public Room addRoom(int capacity, String buildingName, String roomNumber) {
        // Check if room already exists at this location
        if (roomCSV.roomExists(buildingName, roomNumber)) {
            System.out.println("Room already exists at " + buildingName + " - " + roomNumber);
            return null;
        }
        
        Room newRoom = new Room(capacity, buildingName, roomNumber);
        roomCSV.write(newRoom);
        System.out.println("Room added: " + newRoom);
        return newRoom;
    }
    
    // Get all rooms
    public List<Room> getAllRooms() {
        return roomCSV.findAll();
    }
    
    // Get room by ID
    public Room getRoomById(UUID roomId) {
        return roomCSV.findById(roomId);
    }
    
    // Get room by location
    public Room getRoomByLocation(String buildingName, String roomNumber) {
        return roomCSV.findByLocation(buildingName, roomNumber);
    }
    
    // Get available rooms (enabled AND in Available state)
    public List<Room> getAvailableRooms() {
        List<Room> allRooms = roomCSV.findAll();
        List<Room> availableRooms = new ArrayList<>();
        
        for (Room room : allRooms) {
            if (room.isBookable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
    
    // Get available rooms filtered by capacity
    public List<Room> getAvailableRooms(int minCapacity) {
        List<Room> availableRooms = getAvailableRooms();
        List<Room> filteredRooms = new ArrayList<>();
        
        for (Room room : availableRooms) {
            if (room.getCapacity() >= minCapacity) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }
    
    // Get available rooms filtered by capacity and building
    public List<Room> getAvailableRooms(int minCapacity, String buildingName) {
        List<Room> filteredByCapacity = getAvailableRooms(minCapacity);
        List<Room> filteredRooms = new ArrayList<>();
        
        for (Room room : filteredByCapacity) {
            if (room.getBuildingName().equalsIgnoreCase(buildingName)) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }
    
    // Get available rooms with full filters (capacity, building, time range)
    // TODO: Time range filtering will be implemented when booking system supports multiple bookings
    public List<Room> getAvailableRooms(int minCapacity, String buildingName, 
                                         String date, String startTime, String endTime) {
        // For now, same as filtering by capacity and building
        // Time-based filtering will be added when booking system is enhanced
        return getAvailableRooms(minCapacity, buildingName);
    }
    
    // Book a room
    public boolean bookRoom(UUID roomId, UUID userId, String date, String startTime, String endTime) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        if (!room.isBookable()) {
            System.out.println("Room is not available for booking.");
            return false;
        }
        
        room.book(userId, date, startTime, endTime);
        roomCSV.update(room);
        return true;
    }
    
    // Check in to a room
    public boolean checkIn(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        if (!room.getCondition().equals("Reserved")) {
            System.out.println("Room is not in Reserved state. Cannot check in.");
            return false;
        }
        
        room.checkIn();
        roomCSV.update(room);
        return true;
    }
    
    // Check out of a room
    public boolean checkOut(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        if (!room.getCondition().equals("InUse")) {
            System.out.println("Room is not in use. Cannot check out.");
            return false;
        }
        
        room.checkOut();
        roomCSV.update(room);
        return true;
    }
    
    // Cancel a booking
    public boolean cancelBooking(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.cancelBooking();
        roomCSV.update(room);
        return true;
    }
    
    // Extend a booking
    public boolean extendBooking(UUID roomId, int additionalHours) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.extendBooking(additionalHours);
        roomCSV.update(room);
        return true;
    }
    
    // Admin: Enable a room
    public boolean enableRoom(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.enable();
        roomCSV.update(room);
        return true;
    }
    
    // Admin: Disable a room
    public boolean disableRoom(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.disable();
        roomCSV.update(room);
        return true;
    }
    
    // Admin: Set room to maintenance
    public boolean setRoomMaintenance(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.setMaintenance();
        roomCSV.update(room);
        return true;
    }
    
    // Admin: Clear room maintenance
    public boolean clearRoomMaintenance(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        room.clearMaintenance();
        roomCSV.update(room);
        return true;
    }
    
    // Process no-show (called by scheduler or manually)
    public boolean processNoShow(UUID roomId) {
        Room room = roomCSV.findById(roomId);
        
        if (room == null) {
            System.out.println("Room not found.");
            return false;
        }
        
        if (!room.getCondition().equals("Reserved")) {
            System.out.println("Room is not in Reserved state. Cannot process no-show.");
            return false;
        }
        
        // Trigger no-show through the Reserved state
        ReservedState.getInstance().triggerNoShow(room.getRoomContext());
        
        // Process the no-show state (transitions to Available)
        room.getRoomContext().handle();
        
        roomCSV.update(room);
        return true;
    }
    
    // Get rooms by status
    public List<Room> getRoomsByStatus(String status) {
        List<Room> allRooms = roomCSV.findAll();
        List<Room> filteredRooms = new ArrayList<>();
        
        for (Room room : allRooms) {
            if (room.getStatus().equalsIgnoreCase(status)) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }
    
    // Get rooms by condition
    public List<Room> getRoomsByCondition(String condition) {
        List<Room> allRooms = roomCSV.findAll();
        List<Room> filteredRooms = new ArrayList<>();
        
        for (Room room : allRooms) {
            if (room.getCondition().equalsIgnoreCase(condition)) {
                filteredRooms.add(room);
            }
        }
        return filteredRooms;
    }
}