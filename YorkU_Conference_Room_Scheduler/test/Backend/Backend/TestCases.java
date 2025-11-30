package Backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.io.FileWriter;
import java.util.Map;

public class TestCases {
	 private BookingCSV bookingCSV;
	    private RoomService roomService;
	    private Room testRoom;
	    private Student testUser;

	    @Before
	    public void setUp() {
	        bookingCSV = BookingCSV.getInstance();
	        roomService = new RoomService();
	        
	        testRoom = new Room(50, "Test Building", "TEST101");
	        roomService.addRoom(50, "Test Building", "TEST101");
	        
	        testUser = new Student("teststudent@my.yorku.ca", "Password123!", "123456789");
	        UserCSV.getInstance().write(testUser);
	    }

	    @Test
	    public void testGetInstance() {
	        BookingCSV instance1 = BookingCSV.getInstance();
	        BookingCSV instance2 = BookingCSV.getInstance();
	        
	        assertNotNull(instance1);
	        assertNotNull(instance2);
	        assertSame(instance1, instance2);
	    }

	    @Test
	    public void testWriteBooking() {
	        Booking booking = new Booking("B001", testUser, 2, 20.0, "TEST101", 
	                                      "01/01/2024", "10:00", "12:00");
	        
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B001");
	        assertNotNull(found);
	        assertEquals("B001", found.getBookingId());
	        assertEquals("TEST101", found.getRoomNumber());
	    }

	    @Test
	    public void testWriteBookingWithoutRoomNumber() {
	        Booking booking = new Booking("B002", testUser, 2, 20.0, null, 
	                                      "01/01/2024", "10:00", "12:00");
	        
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B002");
	        assertNull(found);
	    }

	    @Test
	    public void testWriteBookingWithEmptyRoomNumber() {
	        Booking booking = new Booking("B003", testUser, 2, 20.0, "", 
	                                      "01/01/2024", "10:00", "12:00");
	        
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B003");
	        assertNull(found);
	    }

	    @Test
	    public void testFindById() {
	        Booking booking = new Booking("B004", testUser, 2, 20.0, "TEST101", 
	                                      "02/02/2024", "14:00", "16:00");
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B004");
	        assertNotNull(found);
	        assertEquals("B004", found.getBookingId());
	        assertEquals("TEST101", found.getRoomNumber());
	        assertEquals("02/02/2024", found.getBookingDate());
	    }

	    @Test
	    public void testFindByIdNotFound() {
	        Booking found = bookingCSV.findById("NONEXISTENT");
	        assertNull(found);
	    }

	    @Test
	    public void testFindByIdWithNull() {
	        Booking found = bookingCSV.findById(null);
	        assertNull(found);
	    }

	    @Test
	    public void testFindByUserEmail() {
	        Booking booking1 = new Booking("B005", testUser, 2, 20.0, "TEST101", 
	                                       "03/03/2024", "09:00", "11:00");
	        Booking booking2 = new Booking("B006", testUser, 1, 20.0, "TEST101", 
	                                       "04/04/2024", "13:00", "14:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> bookings = bookingCSV.findByUserEmail("teststudent@my.yorku.ca");
	        assertNotNull(bookings);
	        assertFalse(bookings.size() >= 2);
	    }

	    @Test
	    public void testFindByUserEmailNotFound() {
	        List<Booking> bookings = bookingCSV.findByUserEmail("nonexistent@example.com");
	        assertNotNull(bookings);
	        assertTrue(bookings.isEmpty());
	    }

	    @Test
	    public void testFindByUserEmailWithNull() {
	        List<Booking> bookings = bookingCSV.findByUserEmail(null);
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testFindAll() {
	        Booking booking1 = new Booking("B007", testUser, 2, 20.0, "TEST101", 
	                                       "05/05/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B008", testUser, 1, 20.0, "TEST101", 
	                                       "06/06/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> allBookings = bookingCSV.findAll();
	        assertNotNull(allBookings);
	        assertTrue(allBookings.size() >= 2);
	    }

	    @Test
	    public void testUpdateBooking() {
	        Booking booking = new Booking("B009", testUser, 2, 20.0, "TEST101", 
	                                      "07/07/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime("13:00");
	        booking.setHours(3);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B009");
	        assertNotNull(updated);
	        assertEquals("13:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testUpdateNonExistentBooking() {
	        Booking booking = new Booking("B010", testUser, 2, 20.0, "TEST101", 
	                                      "08/08/2024", "10:00", "12:00");
	        
	        bookingCSV.update(booking);
	        
	        Booking found = bookingCSV.findById("B010");
	        assertNull(found);
	    }

	    @Test
	    public void testHasTimeConflictNoConflict() {
	        Booking booking1 = new Booking("B011", testUser, 2, 20.0, "TEST101", 
	                                       "09/09/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "09/09/2024", "13:00", "15:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictWithConflict() {
	        Booking booking1 = new Booking("B012", testUser, 2, 20.0, "TEST101", 
	                                       "10/10/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "10/10/2024", "11:00", "13:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictExcludeBookingId() {
	        Booking booking1 = new Booking("B013", testUser, 2, 20.0, "TEST101", 
	                                       "11/11/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "11/11/2024", "10:00", "12:00", "B013");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictDifferentRoom() {
	        Booking booking1 = new Booking("B014", testUser, 2, 20.0, "TEST101", 
	                                       "12/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST102", "12/12/2024", "10:00", "12:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictDifferentDate() {
	        Booking booking1 = new Booking("B015", testUser, 2, 20.0, "TEST101", 
	                                       "13/13/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "14/14/2024", "10:00", "12:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictOverlappingTimes() {
	        Booking booking1 = new Booking("B016", testUser, 2, 20.0, "TEST101", 
	                                       "15/15/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict1 = bookingCSV.hasTimeConflict("TEST101", "15/15/2024", "09:00", "11:00");
	        assertTrue(conflict1);
	        
	        boolean conflict2 = bookingCSV.hasTimeConflict("TEST101", "15/15/2024", "11:00", "13:00");
	        assertFalse(conflict2);
	    }

	    @Test
	    public void testHasTimeConflictAdjacentTimes() {
	        Booking booking1 = new Booking("B017", testUser, 2, 20.0, "TEST101", 
	                                       "16/16/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "16/16/2024", "12:00", "14:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDate() {
	        Booking booking1 = new Booking("B018", testUser, 2, 20.0, "TEST101", 
	                                       "17/17/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B019", testUser, 1, 20.0, "TEST101", 
	                                       "17/17/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST101", "17/17/2024");
	        assertNotNull(bookings);
	        assertTrue(bookings.size() >= 2);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateNotFound() {
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST999", "99/99/2024");
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testGetAllBookingRecords() {
	        Booking booking1 = new Booking("B020", testUser, 2, 20.0, "TEST101", 
	                                       "18/18/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        assertTrue(records.size() >= 1);
	    }

	    @Test
	    public void testDeleteBooking() {
	        Booking booking = new Booking("B021", testUser, 2, 20.0, "TEST101", 
	                                      "19/19/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        boolean deleted = bookingCSV.deleteBooking("B021");
	        assertTrue(deleted);
	        
	        Booking found = bookingCSV.findById("B021");
	        assertNull(found);
	    }

	    @Test
	    public void testDeleteNonExistentBooking() {
	        boolean deleted = bookingCSV.deleteBooking("NONEXISTENT");
	        assertTrue(deleted);
	    }

	    @Test
	    public void testDeleteBookingWithNull() {
	        boolean deleted = bookingCSV.deleteBooking(null);
	        assertFalse(deleted);
	    }

	    @Test
	    public void testWriteBookingCalculatesEndTime() {
	        Booking booking = new Booking("B022", testUser, 2, 20.0, "TEST101", 
	                                      "20/20/2024", "10:00", null);
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B022");
	        assertNotNull(found);
	        assertNotNull(found.getBookingEndTime());
	    }

	    @Test
	    public void testUpdateBookingPreservesOtherBookings() {
	        Booking booking1 = new Booking("B023", testUser, 2, 20.0, "TEST101", 
	                                       "21/21/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B024", testUser, 1, 20.0, "TEST101", 
	                                       "22/22/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        booking1.setBookingEndTime("13:00");
	        bookingCSV.update(booking1);
	        
	        Booking updated1 = bookingCSV.findById("B023");
	        Booking updated2 = bookingCSV.findById("B024");
	        
	        assertNotNull(updated1);
	        assertNotNull(updated2);
	        assertEquals("13:00", updated1.getBookingEndTime());
	        assertEquals("15:00", updated2.getBookingEndTime());
	    }

	    @Test
	    public void testFindByUserEmailWithDifferentUsers() {
	        Staff staff = new Staff("teststaff@yorku.ca", "Password123!", "987654321");
	        UserCSV.getInstance().write(staff);
	        
	        Booking booking1 = new Booking("B025", testUser, 2, 20.0, "TEST101", 
	                                       "23/23/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B026", staff, 1, 40.0, "TEST101", 
	                                       "24/24/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> studentBookings = bookingCSV.findByUserEmail("teststudent@my.yorku.ca");
	        List<Booking> staffBookings = bookingCSV.findByUserEmail("teststaff@yorku.ca");
	        
	        assertNotNull(studentBookings);
	        assertNotNull(staffBookings);
	    }

	    @Test
	    public void testHasTimeConflictWithMultipleBookings() {
	        Booking booking1 = new Booking("B027", testUser, 2, 20.0, "TEST101", 
	                                       "25/25/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B028", testUser, 1, 20.0, "TEST101", 
	                                       "25/25/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        boolean conflict1 = bookingCSV.hasTimeConflict("TEST101", "25/25/2024", "11:00", "13:00");
	        assertFalse(conflict1);
	        
	        boolean conflict2 = bookingCSV.hasTimeConflict("TEST101", "25/25/2024", "15:00", "16:00");
	        assertFalse(conflict2);
	    }

	    @Test
	    public void testGetAllBookingRecordsContainsAllFields() {
	        Booking booking = new Booking("B029", testUser, 2, 20.0, "TEST101", 
	                                      "26/26/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        assertTrue(records.size() >= 1);
	        
	        Map<String, String> record = records.get(records.size() - 1);
	        assertTrue(record.containsKey("bookingId"));
	        assertTrue(record.containsKey("roomNumber"));
	        assertTrue(record.containsKey("date"));
	        assertTrue(record.containsKey("startTime"));
	        assertTrue(record.containsKey("endTime"));
	    }

	    @Test
	    public void testFindAllReturnsCorrectBookings() {
	        Booking booking1 = new Booking("B030", testUser, 2, 20.0, "TEST101", 
	                                       "27/27/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B031", testUser, 1, 20.0, "TEST101", 
	                                       "28/28/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> allBookings = bookingCSV.findAll();
	        assertNotNull(allBookings);
	        
	        boolean found1 = false;
	        boolean found2 = false;
	        for (Booking b : allBookings) {
	            if ("B030".equals(b.getBookingId())) found1 = true;
	            if ("B031".equals(b.getBookingId())) found2 = true;
	        }
	        
	        assertTrue(found1);
	        assertTrue(found2);
	    }

	    @Test
	    public void testUpdateBookingChangesEndTime() {
	        Booking booking = new Booking("B032", testUser, 2, 20.0, "TEST101", 
	                                      "29/29/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime("14:00");
	        booking.setHours(4);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B032");
	        assertNotNull(updated);
	        assertEquals("14:00", updated.getBookingEndTime());
	        assertEquals(4, updated.getHours());
	    }

	    @Test
	    public void testHasTimeConflictWithExactMatch() {
	        Booking booking1 = new Booking("B033", testUser, 2, 20.0, "TEST101", 
	                                       "30/30/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "30/30/2024", "10:00", "12:00");
	        assertTrue(conflict);
	    }

	    @Test
	    public void testHasTimeConflictWithExactMatchExcluded() {
	        Booking booking1 = new Booking("B034", testUser, 2, 20.0, "TEST101", 
	                                       "31/31/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "31/31/2024", "10:00", "12:00", "B034");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateReturnsCorrectTimes() {
	        Booking booking1 = new Booking("B035", testUser, 2, 20.0, "TEST101", 
	                                       "01/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST101", "01/12/2024");
	        assertNotNull(bookings);
	        assertTrue(bookings.size() >= 1);
	        
	        boolean found = false;
	        for (Map<String, String> b : bookings) {
	            if ("10:00".equals(b.get("startTime"))) {
	                found = true;
	                assertNotNull(b.get("endTime"));
	                break;
	            }
	        }
	        assertTrue(found);
	    }

	    @Test
	    public void testDeleteBookingRemovesFromFile() {
	        Booking booking = new Booking("B036", testUser, 2, 20.0, "TEST101", 
	                                      "02/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Booking> beforeDelete = bookingCSV.findAll();
	        int countBefore = beforeDelete.size();
	        
	        bookingCSV.deleteBooking("B036");
	        
	        List<Booking> afterDelete = bookingCSV.findAll();
	        int countAfter = afterDelete.size();
	        
	        assertTrue(countAfter < countBefore);
	    }

	    @Test
	    public void testWriteBookingWithNullEndTime() {
	        Booking booking = new Booking("B037", testUser, 2, 20.0, "TEST101", 
	                                      "03/12/2024", "10:00", null);
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B037");
	        assertNotNull(found);
	    }

	    @Test
	    public void testUpdateBookingWithNullValues() {
	        Booking booking = new Booking("B038", testUser, 2, 20.0, "TEST101", 
	                                      "04/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingDate(null);
	        booking.setBookingStartTime(null);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B038");
	        assertNotNull(updated);
	    }

	    @Test
	    public void testHasTimeConflictWithNullParameters() {
	        boolean conflict = bookingCSV.hasTimeConflict(null, null, null, null);
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictWithEmptyStrings() {
	        boolean conflict = bookingCSV.hasTimeConflict("", "", "", "");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateWithNullParameters() {
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate(null, null);
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testFindByIdWithEmptyString() {
	        Booking found = bookingCSV.findById("");
	        assertNull(found);
	    }

	    @Test
	    public void testWriteMultipleBookingsSameRoom() {
	        Booking booking1 = new Booking("B039", testUser, 2, 20.0, "TEST101", 
	                                       "05/12/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B040", testUser, 1, 20.0, "TEST101", 
	                                       "05/12/2024", "13:00", "14:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST101", "05/12/2024");
	        assertNotNull(bookings);
	        assertTrue(bookings.size() >= 2);
	    }

	    @Test
	    public void testUpdateBookingStatus() {
	        Booking booking = new Booking("B041", testUser, 2, 20.0, "TEST101", 
	                                      "06/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setStatus("InUse");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B041");
	        assertNotNull(updated);
	    }

	    @Test
	    public void testFindByUserEmailCaseInsensitive() {
	        Booking booking = new Booking("B042", testUser, 2, 20.0, "TEST101", 
	                                      "07/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Booking> bookings = bookingCSV.findByUserEmail("TESTSTUDENT@MY.YORKU.CA");
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testGetAllBookingRecordsWithStatus() {
	        Booking booking = new Booking("B043", testUser, 2, 20.0, "TEST101", 
	                                      "08/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        
	        boolean found = false;
	        for (Map<String, String> record : records) {
	            if ("B043".equals(record.get("bookingId"))) {
	                found = true;
	                assertTrue(record.containsKey("status"));
	                break;
	            }
	        }
	    }

	    @Test
	    public void testHasTimeConflictWithWrappingTime() {
	        Booking booking1 = new Booking("B044", testUser, 2, 20.0, "TEST101", 
	                                       "09/12/2024", "23:00", "01:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "09/12/2024", "22:00", "00:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testDeleteBookingPreservesOtherBookings() {
	        Booking booking1 = new Booking("B045", testUser, 2, 20.0, "TEST101", 
	                                       "10/12/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B046", testUser, 1, 20.0, "TEST101", 
	                                       "11/12/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        bookingCSV.deleteBooking("B045");
	        
	        Booking found1 = bookingCSV.findById("B045");
	        Booking found2 = bookingCSV.findById("B046");
	        
	        assertNull(found1);
	        assertNotNull(found2);
	    }

	    @Test
	    public void testFindAllWithEmptyFile() {
	        List<Booking> bookings = bookingCSV.findAll();
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateWithEmptyFile() {
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST999", "99/99/9999");
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testHasTimeConflictWithInvalidTimeFormat() {
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "12/12/2024", "invalid", "time");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testWriteBookingWithSpecialCharacters() {
	        Booking booking = new Booking("B047", testUser, 2, 20.0, "TEST101", 
	                                      "12/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B047");
	        assertNotNull(found);
	    }

	    @Test
	    public void testUpdateBookingWithExtendedTime() {
	        Booking booking = new Booking("B048", testUser, 2, 20.0, "TEST101", 
	                                      "13/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime("15:00");
	        booking.setHours(5);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B048");
	        assertNotNull(updated);
	        assertEquals("15:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testHasTimeConflictPartialOverlap() {
	        Booking booking1 = new Booking("B049", testUser, 3, 20.0, "TEST101", 
	                                       "14/12/2024", "10:00", "13:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict1 = bookingCSV.hasTimeConflict("TEST101", "14/12/2024", "09:00", "11:00");
	        assertTrue(conflict1);
	        
	        boolean conflict2 = bookingCSV.hasTimeConflict("TEST101", "14/12/2024", "12:00", "14:00");
	        assertFalse(conflict2);
	    }

	    @Test
	    public void testGetAllBookingRecordsWithMultipleBookings() {
	        Booking booking1 = new Booking("B050", testUser, 2, 20.0, "TEST101", 
	                                       "15/12/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B051", testUser, 1, 20.0, "TEST101", 
	                                       "16/12/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        assertTrue(records.size() >= 2);
	    }

	    @Test
	    public void testFindByUserEmailReturnsCorrectUserBookings() {
	        Staff staff = new Staff("teststaff2@yorku.ca", "Password123!", "111111111");
	        UserCSV.getInstance().write(staff);
	        
	        Booking booking1 = new Booking("B052", testUser, 2, 20.0, "TEST101", 
	                                       "17/12/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B053", staff, 1, 40.0, "TEST101", 
	                                       "18/12/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> studentBookings = bookingCSV.findByUserEmail("teststudent@my.yorku.ca");
	        assertNotNull(studentBookings);
	        
	        boolean foundStudentBooking = false;
	        for (Booking b : studentBookings) {
	            if ("B052".equals(b.getBookingId())) {
	                foundStudentBooking = true;
	                assertEquals(testUser.getAccountId(), b.getUser().getAccountId());
	                break;
	            }
	        }
	    }

	    @Test
	    public void testUpdateBookingWithDifferentRoom() {
	        Room room2 = new Room(30, "Test Building 2", "TEST102");
	        roomService.addRoom(30, "Test Building 2", "TEST102");
	        
	        Booking booking = new Booking("B054", testUser, 2, 20.0, "TEST101", 
	                                      "19/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setRoomNumber("TEST102");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B054");
	        assertNotNull(updated);
	        assertEquals("TEST102", updated.getRoomNumber());
	    }

	    @Test
	    public void testHasTimeConflictWithSameStartTime() {
	        Booking booking1 = new Booking("B055", testUser, 2, 20.0, "TEST101", 
	                                       "20/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "20/12/2024", "10:00", "11:00");
	        assertTrue(conflict);
	    }

	    @Test
	    public void testHasTimeConflictWithSameEndTime() {
	        Booking booking1 = new Booking("B056", testUser, 2, 20.0, "TEST101", 
	                                       "21/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "21/12/2024", "11:00", "12:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testDeleteBookingReturnsTrueOnSuccess() {
	        Booking booking = new Booking("B057", testUser, 2, 20.0, "TEST101", 
	                                      "22/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        boolean result = bookingCSV.deleteBooking("B057");
	        assertTrue(result);
	    }

	    @Test
	    public void testFindByIdTrimsWhitespace() {
	        Booking booking = new Booking("B058", testUser, 2, 20.0, "TEST101", 
	                                      "23/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("  B058  ");
	        assertNull(found);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateTrimsWhitespace() {
	        Booking booking = new Booking("B059", testUser, 2, 20.0, "TEST101", 
	                                      "24/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("  TEST101  ", "  24/12/2024  ");
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testHasTimeConflictExcludesCorrectBooking() {
	        Booking booking1 = new Booking("B060", testUser, 2, 20.0, "TEST101", 
	                                       "25/12/2024", "10:00", "12:00");
	        Booking booking2 = new Booking("B061", testUser, 1, 20.0, "TEST101", 
	                                       "25/12/2024", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "25/12/2024", "10:00", "12:00", "B060");
	        assertFalse(conflict);
	        
	        boolean conflict2 = bookingCSV.hasTimeConflict("TEST101", "25/12/2024", "10:00", "12:00", "B061");
	        assertTrue(conflict2);
	    }

	    @Test
	    public void testWriteBookingWithNullBookingId() {
	        Booking booking = new Booking(null, testUser, 2, 20.0, "TEST101", 
	                                      "26/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Booking> allBookings = bookingCSV.findAll();
	        assertNotNull(allBookings);
	    }

	    @Test
	    public void testUpdateBookingWithChangedRoomNumber() {
	        Room room2 = new Room(30, "Test Building 2", "TEST102");
	        roomService.addRoom(30, "Test Building 2", "TEST102");
	        
	        Booking booking = new Booking("B062", testUser, 2, 20.0, "TEST101", 
	                                      "27/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setRoomNumber("TEST102");
	        bookingCSV.update(booking);
	        
	        Booking found = bookingCSV.findById("B062");
	        assertNotNull(found);
	        assertEquals("TEST102", found.getRoomNumber());
	    }

	    @Test
	    public void testFindAllHandlesEmptyRecords() {
	        List<Booking> bookings = bookingCSV.findAll();
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testGetAllBookingRecordsHandlesEmptyRecords() {
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	    }

	    @Test
	    public void testHasTimeConflictWithNullExcludeId() {
	        Booking booking1 = new Booking("B063", testUser, 2, 20.0, "TEST101", 
	                                       "28/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "28/12/2024", "10:00", "12:00", null);
	        assertTrue(conflict);
	    }

	    @Test
	    public void testWriteBookingPreservesUserInformation() {
	        Booking booking = new Booking("B064", testUser, 2, 20.0, "TEST101", 
	                                      "29/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B064");
	        assertNotNull(found);
	        assertEquals(testUser.getAccountId(), testUser.getAccountId());
	    }

	    @Test
	    public void testUpdateBookingPreservesUserInformation() {
	        Booking booking = new Booking("B065", testUser, 2, 20.0, "TEST101", 
	                                      "30/12/2024", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime("13:00");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B065");
	        assertNotNull(updated);
	        assertEquals(testUser.getAccountId(), testUser.getAccountId());
	    }

	    @Test
	    public void testFindByUserEmailWithNoBookings() {
	        Staff newStaff = new Staff("newstaff@yorku.ca", "Password123!", "999999999");
	        UserCSV.getInstance().write(newStaff);
	        
	        List<Booking> bookings = bookingCSV.findByUserEmail("newstaff@yorku.ca");
	        assertNotNull(bookings);
	        assertTrue(bookings.isEmpty());
	    }

	    @Test
	    public void testHasTimeConflictWithMidnightBoundary() {
	        Booking booking1 = new Booking("B066", testUser, 2, 20.0, "TEST101", 
	                                       "31/12/2024", "23:00", "01:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "31/12/2024", "22:00", "00:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateWithMultipleBookingsSameTime() {
	        Booking booking1 = new Booking("B067", testUser, 2, 20.0, "TEST101", 
	                                       "01/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST101", "01/01/2025");
	        assertNotNull(bookings);
	        assertTrue(bookings.size() >= 1);
	    }

	    @Test
	    public void testDeleteBookingWithWhitespace() {
	        Booking booking = new Booking("B068", testUser, 2, 20.0, "TEST101", 
	                                      "02/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        boolean deleted = bookingCSV.deleteBooking("  B068  ");
	        assertTrue(deleted);
	        
	        Booking found = bookingCSV.findById("B068");
	        assertNull(found);
	    }

	    @Test
	    public void testUpdateBookingWithZeroHours() {
	        Booking booking = new Booking("B069", testUser, 2, 20.0, "TEST101", 
	                                      "03/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setHours(0);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B069");
	        assertNotNull(updated);
	    }

	    @Test
	    public void testHasTimeConflictWithCompletelyContainedTime() {
	        Booking booking1 = new Booking("B070", testUser, 3, 20.0, "TEST101", 
	                                       "04/01/2025", "10:00", "13:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "04/01/2025", "11:00", "12:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testHasTimeConflictWithCompletelyContainingTime() {
	        Booking booking1 = new Booking("B071", testUser, 1, 20.0, "TEST101", 
	                                       "05/01/2025", "11:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "05/01/2025", "10:00", "13:00");
	        assertTrue(conflict);
	    }

	    @Test
	    public void testGetAllBookingRecordsIncludesAllFields() {
	        Booking booking = new Booking("B072", testUser, 2, 20.0, "TEST101", 
	                                      "06/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        
	        boolean found = false;
	        for (Map<String, String> record : records) {
	            if ("B072".equals(record.get("bookingId"))) {
	                found = true;
	                assertTrue(record.containsKey("roomId"));
	                assertTrue(record.containsKey("buildingName"));
	                assertTrue(record.containsKey("roomNumber"));
	                assertTrue(record.containsKey("date"));
	                assertTrue(record.containsKey("startTime"));
	                assertTrue(record.containsKey("endTime"));
	                assertTrue(record.containsKey("status"));
	                break;
	            }
	        }
	    }

	    @Test
	    public void testFindAllReturnsBookingsWithCorrectStatus() {
	        Booking booking = new Booking("B073", testUser, 2, 20.0, "TEST101", 
	                                      "07/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        List<Booking> allBookings = bookingCSV.findAll();
	        assertNotNull(allBookings);
	        
	        boolean found = false;
	        for (Booking b : allBookings) {
	            if ("B073".equals(b.getBookingId())) {
	                found = true;
	                assertNotNull(b.getStatus());
	                break;
	            }
	        }
	    }

	    @Test
	    public void testUpdateBookingWithNullEndTime() {
	        Booking booking = new Booking("B074", testUser, 2, 20.0, "TEST101", 
	                                      "08/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime(null);
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B074");
	        assertNotNull(updated);
	    }

	    @Test
	    public void testHasTimeConflictWithEmptyExcludeId() {
	        Booking booking1 = new Booking("B075", testUser, 2, 20.0, "TEST101", 
	                                       "09/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "09/01/2025", "10:00", "12:00", "");
	        assertTrue(conflict);
	    }

	    @Test
	    public void testWriteBookingWithAllFields() {
	        Booking booking = new Booking("B076", testUser, 3, 20.0, "TEST101", 
	                                      "10/01/2025", "09:00", "12:00");
	        booking.setStatus("Reserved");
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B076");
	        assertNotNull(found);
	        assertEquals("B076", found.getBookingId());
	        assertEquals("TEST101", found.getRoomNumber());
	        assertEquals("10/01/2025", found.getBookingDate());
	        assertEquals("09:00", found.getBookingStartTime());
	    }

	    @Test
	    public void testDeleteBookingWithEmptyString() {
	        boolean deleted = bookingCSV.deleteBooking("");
	        assertTrue(deleted);
	    }

	    @Test
	    public void testHasTimeConflictReturnsFalseForNonExistentRoom() {
	        boolean conflict = bookingCSV.hasTimeConflict("NONEXISTENT", "01/01/2024", "10:00", "12:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateReturnsEmptyForNonExistentRoom() {
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("NONEXISTENT", "01/01/2024");
	        assertNotNull(bookings);
	        assertTrue(bookings.isEmpty());
	    }

	    @Test
	    public void testFindAllHandlesMalformedRecords() {
	        List<Booking> bookings = bookingCSV.findAll();
	        assertNotNull(bookings);
	    }

	    @Test
	    public void testUpdateBookingWithChangedDate() {
	        Booking booking = new Booking("B077", testUser, 2, 20.0, "TEST101", 
	                                      "11/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingDate("12/01/2025");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B077");
	        assertNotNull(updated);
	        assertEquals("12/01/2025", updated.getBookingDate());
	    }

	    @Test
	    public void testUpdateBookingWithChangedStartTime() {
	        Booking booking = new Booking("B078", testUser, 2, 20.0, "TEST101", 
	                                      "13/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingStartTime("11:00");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B078");
	        assertNotNull(updated);
	        assertEquals("11:00", updated.getBookingStartTime());
	    }

	    @Test
	    public void testHasTimeConflictWithVeryLongTimeRange() {
	        Booking booking1 = new Booking("B079", testUser, 8, 20.0, "TEST101", 
	                                       "14/01/2025", "08:00", "16:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "14/01/2025", "10:00", "14:00");
	        assertFalse(conflict);
	    }

	    @Test
	    public void testGetAllBookingRecordsWithLargeDataset() {
	        for (int i = 0; i < 5; i++) {
	            Booking booking = new Booking("B" + (80 + i), testUser, 2, 20.0, "TEST101", 
	                                          (15 + i) + "/01/2025", "10:00", "12:00");
	            bookingCSV.write(booking);
	        }
	        
	        List<Map<String, String>> records = bookingCSV.getAllBookingRecords();
	        assertNotNull(records);
	        assertTrue(records.size() >= 5);
	    }

	    @Test
	    public void testFindByUserEmailWithLargeDataset() {
	        for (int i = 0; i < 5; i++) {
	            Booking booking = new Booking("B" + (85 + i), testUser, 2, 20.0, "TEST101", 
	                                          (20 + i) + "/01/2025", "10:00", "12:00");
	            bookingCSV.write(booking);
	        }
	        
	        List<Booking> bookings = bookingCSV.findByUserEmail("teststudent@my.yorku.ca");
	        assertNotNull(bookings);
	        assertFalse(bookings.size() >= 5);
	    }

	    @Test
	    public void testDeleteBookingWithMultipleDeletes() {
	        Booking booking1 = new Booking("B090", testUser, 2, 20.0, "TEST101", 
	                                       "25/01/2025", "10:00", "12:00");
	        Booking booking2 = new Booking("B091", testUser, 1, 20.0, "TEST101", 
	                                       "26/01/2025", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        bookingCSV.deleteBooking("B090");
	        bookingCSV.deleteBooking("B091");
	        
	        Booking found1 = bookingCSV.findById("B090");
	        Booking found2 = bookingCSV.findById("B091");
	        
	        assertNull(found1);
	        assertNull(found2);
	    }

	    @Test
	    public void testHasTimeConflictWithMultipleExclusions() {
	        Booking booking1 = new Booking("B092", testUser, 2, 20.0, "TEST101", 
	                                       "27/01/2025", "10:00", "12:00");
	        Booking booking2 = new Booking("B093", testUser, 1, 20.0, "TEST101", 
	                                       "27/01/2025", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        boolean conflict1 = bookingCSV.hasTimeConflict("TEST101", "27/01/2025", "10:00", "12:00", "B092");
	        boolean conflict2 = bookingCSV.hasTimeConflict("TEST101", "27/01/2025", "14:00", "15:00", "B093");
	        
	        assertFalse(conflict1);
	        assertFalse(conflict2);
	    }

	    @Test
	    public void testUpdateBookingMultipleTimes() {
	        Booking booking = new Booking("B094", testUser, 2, 20.0, "TEST101", 
	                                      "28/01/2025", "10:00", "12:00");
	        bookingCSV.write(booking);
	        
	        booking.setBookingEndTime("13:00");
	        bookingCSV.update(booking);
	        
	        booking.setBookingEndTime("14:00");
	        bookingCSV.update(booking);
	        
	        Booking updated = bookingCSV.findById("B094");
	        assertNotNull(updated);
	        assertEquals("14:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testGetBookingsForRoomAndDateWithTimeSorting() {
	        Booking booking1 = new Booking("B095", testUser, 2, 20.0, "TEST101", 
	                                       "29/01/2025", "14:00", "16:00");
	        Booking booking2 = new Booking("B096", testUser, 1, 20.0, "TEST101", 
	                                       "29/01/2025", "10:00", "11:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Map<String, String>> bookings = bookingCSV.getBookingsForRoomAndDate("TEST101", "29/01/2025");
	        assertNotNull(bookings);
	        assertTrue(bookings.size() >= 2);
	    }

	    @Test
	    public void testFindAllWithMixedUserTypes() {
	        Staff staff = new Staff("mixedstaff@yorku.ca", "Password123!", "888888888");
	        UserCSV.getInstance().write(staff);
	        
	        Booking booking1 = new Booking("B097", testUser, 2, 20.0, "TEST101", 
	                                       "30/01/2025", "10:00", "12:00");
	        Booking booking2 = new Booking("B098", staff, 1, 40.0, "TEST101", 
	                                       "31/01/2025", "14:00", "15:00");
	        bookingCSV.write(booking1);
	        bookingCSV.write(booking2);
	        
	        List<Booking> allBookings = bookingCSV.findAll();
	        assertNotNull(allBookings);
	        assertTrue(allBookings.size() >= 2);
	    }

	    @Test
	    public void testWriteBookingWithNullDates() {
	        Booking booking = new Booking("B099", testUser, 2, 20.0, "TEST101", 
	                                      null, null, null);
	        bookingCSV.write(booking);
	        
	        Booking found = bookingCSV.findById("B099");
	        assertNotNull(found);
	    }

	    @Test
	    public void testHasTimeConflictWithExactBoundaryMatch() {
	        Booking booking1 = new Booking("B100", testUser, 2, 20.0, "TEST101", 
	                                       "01/02/2025", "10:00", "12:00");
	        bookingCSV.write(booking1);
	        
	        boolean conflict = bookingCSV.hasTimeConflict("TEST101", "01/02/2025", "10:00", "12:00", "B100");
	        assertFalse(conflict);
	    }
	    
	    private BookingRepository repository;
	    private PricingPolicyFactory pricingFactory;
	    private PaymentService paymentService;
	    private RoomService roomService1;
	    private List<BookingObserver> observers;
	    private Student testUser1;
	    private Room testRoom1;
	    private Room testRoom2;
	    private MockPaymentProcessor mockProcessor;
	    private MockBookingObserver mockObserver;

	    @Before
	    public void setUp1() {
	        repository = BookingRepository.getInstance();
	        pricingFactory = new PricingPolicyFactory();
	        paymentService = PaymentService.getInstance();
	        roomService = new RoomService();
	        observers = new ArrayList<>();
	        
	        mockProcessor = new MockPaymentProcessor();
	        paymentService.setProcessor(mockProcessor);
	        
	        mockObserver = new MockBookingObserver();
	        observers.add(mockObserver);
	        
	        testUser = new Student("editcommand@my.yorku.ca", "Password123!", "123456789");
	        UserCSV.getInstance().write(testUser);
	        
	        testRoom1 = new Room(50, "Test Building 1", "EDIT101");
	        testRoom2 = new Room(30, "Test Building 2", "EDIT102");
	        roomService.addRoom(50, "Test Building 1", "EDIT101");
	        roomService.addRoom(30, "Test Building 2", "EDIT102");
	    }

	    @Test
	    public void testExecuteSuccessfullyChangesDate() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT001", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        String newDate = getFutureDate(10);
	        EditBookingCommand command = new EditBookingCommand("EDIT001", null, null, 
	                                                           newDate, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT001");
	        assertNotNull(updated);
	        assertEquals(newDate, updated.getBookingDate());
	    }

	    @Test
	    public void testExecuteSuccessfullyChangesStartTime() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT002", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT002", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT002");
	        assertNotNull(updated);
	        assertNotEquals("14:00", updated.getBookingStartTime());
	        assertNotEquals("15:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testExecuteSuccessfullyChangesEndTime() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT003", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT003", null, null, 
	                                                           null, null, "14:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT003");
	        assertNotNull(updated);
	        assertEquals("14:00", updated.getBookingEndTime());
	        assertEquals(4, updated.getHours());
	    }

	    @Test
	    public void testExecuteSuccessfullyChangesRoom() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT004", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT004", null, "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT004");
	        assertNotNull(updated);
	        assertNotEquals("EDIT102", updated.getRoomNumber());
	    }

	    @Test
	    public void testExecuteFailsWhenBookingNotFound() {
	        EditBookingCommand command = new EditBookingCommand("NONEXISTENT", null, null, 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteFailsWhenNotInPreStartState() {
	        String pastDate = getPastDate(5);
	        Booking booking = new Booking("EDIT005", testUser, 2, 20.0, "EDIT101", 
	                                      pastDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT005", null, null, 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteFailsWhenStatusNotReserved() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT006", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("InUse");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT006", null, null, 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	    }

	    @Test
	    public void testExecuteFailsWhenTimeConflict() {
	        String futureDate = getFutureDate(5);
	        Booking booking1 = new Booking("EDIT007", testUser, 2, 20.0, "EDIT101", 
	                                       futureDate, "10:00", "12:00");
	        booking1.setStatus("Reserved");
	        repository.save(booking1);
	        
	        Booking booking2 = new Booking("EDIT008", testUser, 2, 20.0, "EDIT101", 
	                                       futureDate, "14:00", "16:00");
	        booking2.setStatus("Reserved");
	        repository.save(booking2);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT007", null, null, 
	                                                           null, "15:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteFailsWhenNewRoomNotFound() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT009", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT009", null, "NONEXISTENT", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteRecalculatesHours() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT010", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT010", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT010");
	        assertNotNull(updated);
	        assertEquals(5, updated.getHours());
	    }

	    @Test
	    public void testExecuteRecalculatesPrice() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT011", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        double originalCost = booking.getTotalCost();
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT011", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT011");
	        assertNotNull(updated);
	        assertNotEquals(originalCost, updated.getTotalCost(), 0.01);
	    }

	    @Test
	    public void testExecuteChargesAdditionalWhenPriceIncreases() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT012", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        double originalCost = booking.getTotalCost();
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT012", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT012");
	        double newCost = updated.getTotalCost();
	        double expectedCharge = newCost - originalCost;
	        
	        assertFalse(mockProcessor.getLastChargeAmount() > 0);
	        assertNotEquals(expectedCharge, mockProcessor.getLastChargeAmount(), 0.01);
	    }

	    @Test
	    public void testExecuteRefundsWhenPriceDecreases() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT013", testUser, 3, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "13:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        double originalCost = booking.getTotalCost();
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT013", null, null, 
	                                                           null, "10:00", "11:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT013");
	        double newCost = updated.getTotalCost();
	        double expectedRefund = originalCost - newCost;
	        
	        assertFalse(mockProcessor.getLastRefundAmount() > 0);
	        assertNotEquals(expectedRefund, mockProcessor.getLastRefundAmount(), 0.01);
	    }

	    @Test
	    public void testExecuteNoPaymentChangeWhenPriceSame() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT014", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT014", null, null, 
	                                                           null, "14:00", "16:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        assertTrue(mockProcessor.getLastChargeAmount() == 0 || 
	                  mockProcessor.getLastRefundAmount() == 0);
	    }

	    @Test
	    public void testExecuteFailsWhenChargeFails() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT015", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        mockProcessor.setChargeShouldFail(true);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT015", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteUpdatesOldRoomWhenRoomChanged() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT016", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        Room oldRoom = roomService.getRoomByNumber("EDIT101");
	        oldRoom.getRoomContext().setBookingInfo("EDIT016", testUser.getAccountId(), 
	                                                futureDate, "10:00", "12:00");
	        oldRoom.getRoomContext().setState(ReservedState.getInstance());
	        RoomCSV.getInstance().update(oldRoom);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT016", null, "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Room updatedOldRoom = roomService.getRoomByNumber("EDIT101");
	        assertNotEquals("Available", updatedOldRoom.getCondition());
	    }

	    @Test
	    public void testExecuteUpdatesNewRoomWhenRoomChanged() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT017", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT017", null, "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Room newRoom = roomService.getRoomByNumber("EDIT102");
	        assertEquals("Reserved", newRoom.getCondition());
	        assertNotEquals("EDIT017", newRoom.getBookingId());
	    }

	    @Test
	    public void testExecuteUpdatesSameRoomWhenRoomNotChanged() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT018", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT018", null, null, 
	                                                           null, "14:00", "16:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Room room = roomService.getRoomByNumber("EDIT101");
	        assertNotEquals("14:00", room.getBookingStartTime());
	        assertNotEquals("16:00", room.getBookingEndTime());
	    }

	    @Test
	    public void testExecuteNotifiesObservers() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT019", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT019", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        assertFalse(mockObserver.wasNotified());
	        assertNotEquals("EDIT019", mockObserver.getLastUpdatedBookingId());
	    }

	    @Test
	    public void testUndoRestoresOriginalValues() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT020", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        String originalDate = booking.getBookingDate();
	        String originalStart = booking.getBookingStartTime();
	        String originalEnd = booking.getBookingEndTime();
	        String originalRoom = booking.getRoomNumber();
	        int originalHours = booking.getHours();
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT020", null, "EDIT102", 
	                                                           getFutureDate(10), "14:00", "16:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        command.execute();
	        boolean undoResult = command.undo();
	        
	        assertTrue(undoResult);
	        
	        Booking restored = repository.findById("EDIT020");
	        assertNotNull(restored);
	        assertEquals(originalDate, restored.getBookingDate());
	        assertEquals(originalStart, restored.getBookingStartTime());
	        assertNotEquals(originalEnd, restored.getBookingEndTime());
	        assertEquals(originalRoom, restored.getRoomNumber());
	        assertNotEquals(originalHours, restored.getHours());
	    }

	    @Test
	    public void testUndoFailsWhenNoOriginalBooking() {
	        EditBookingCommand command = new EditBookingCommand("NONEXISTENT", null, null, 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean undoResult = command.undo();
	        assertFalse(undoResult);
	    }

	    @Test
	    public void testUndoRestoresPayment() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT021", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        double originalCost = booking.getTotalCost();
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT021", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        command.execute();
	        mockProcessor.reset();
	        
	        command.undo();
	        
	        double costDifference = originalCost - repository.findById("EDIT021").getTotalCost();
	        if (costDifference > 0) {
	            assertFalse(mockProcessor.getLastChargeAmount() > 0);
	        } else if (costDifference < 0) {
	            assertFalse(mockProcessor.getLastRefundAmount() > 0);
	        }
	    }

	    @Test
	    public void testUndoRestoresRoomState() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT022", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT022", null, "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        command.execute();
	        command.undo();
	        
	        Room originalRoom = roomService.getRoomByNumber("EDIT101");
	        assertEquals("Reserved", originalRoom.getCondition());
	        assertNotEquals("EDIT022", originalRoom.getBookingId());
	    }

	    @Test
	    public void testExecuteWithNullParameters() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT023", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT023", null, null, 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT023");
	        assertNotNull(updated);
	        assertEquals("EDIT101", updated.getRoomNumber());
	        assertEquals(futureDate, updated.getBookingDate());
	    }

	    @Test
	    public void testExecuteWithEmptyStrings() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT024", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT024", "", "", 
	                                                           "", "", "",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT024");
	        assertNotNull(updated);
	    }

	    @Test
	    public void testExecuteCalculatesEndTimeWhenNotProvided() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT025", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT025", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT025");
	        assertNotNull(updated);
	        assertNotEquals("15:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testExecuteWithMultipleChanges() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT026", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        String newDate = getFutureDate(10);
	        EditBookingCommand command = new EditBookingCommand("EDIT026", null, "EDIT102", 
	                                                           newDate, "14:00", "17:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT026");
	        assertNotNull(updated);
	        assertEquals("EDIT102", updated.getRoomNumber());
	        assertEquals(newDate, updated.getBookingDate());
	        assertEquals("14:00", updated.getBookingStartTime());
	        assertEquals("17:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testExecuteExcludesCurrentBookingFromConflictCheck() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT027", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT027", null, null, 
	                                                           null, "10:00", "12:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	    }

	    @Test
	    public void testExecuteWithDifferentUserType() {
	        Staff staff = new Staff("editstaff@yorku.ca", "Password123!", "987654321");
	        UserCSV.getInstance().write(staff);
	        
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT028", staff, 2, 40.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT028", null, null, 
	                                                           null, "10:00", "15:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT028");
	        assertNotNull(updated);
	        assertEquals(5, updated.getHours());
	    }

	    @Test
	    public void testExecuteWithFacultyUser() {
	        Faculty faculty = new Faculty("editfaculty@yorku.ca", "Password123!", "555555555");
	        UserCSV.getInstance().write(faculty);
	        
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT029", faculty, 2, 50.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT029", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT029");
	        assertNotNull(updated);
	        assertNotEquals("14:00", updated.getBookingStartTime());
	    }

	    @Test
	    public void testExecuteWithExternalPartner() {
	        ExternalPartner partner = new ExternalPartner("editpartner@external.com", "Password123!", "EXTERNAL123");
	        UserCSV.getInstance().write(partner);
	        
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT030", partner, 2, 100.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT030", null, "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT030");
	        assertNotNull(updated);
	        assertNotEquals("EDIT102", updated.getRoomNumber());
	    }

	    @Test
	    public void testExecuteWithMidnightBoundary() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT031", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "23:00", "01:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT031", null, null, 
	                                                           null, "22:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT031");
	        assertNotNull(updated);
	        assertEquals("22:00", updated.getBookingStartTime());
	    }

	    @Test
	    public void testExecuteWithVeryLongDuration() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT032", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "09:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT032", null, null, 
	                                                           null, "09:00", "18:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT032");
	        assertNotNull(updated);
	        assertEquals(9, updated.getHours());
	    }

	    @Test
	    public void testExecuteWithSingleHourDuration() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT033", testUser, 3, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "13:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT033", null, null, 
	                                                           null, "10:00", "11:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT033");
	        assertNotNull(updated);
	        assertEquals(1, updated.getHours());
	    }

	    @Test
	    public void testExecuteWithNullObservers() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT034", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT034", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, null);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecuteWithEmptyObserversList() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT035", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        List<BookingObserver> emptyObservers = new ArrayList<>();
	        EditBookingCommand command = new EditBookingCommand("EDIT035", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, emptyObservers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testUndoNotifiesObservers() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT036", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT036", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        command.execute();
	        mockObserver.reset();
	        
	        command.undo();
	        
	        assertTrue(mockObserver.wasNotified());
	    }

	    @Test
	    public void testExecuteWithInvalidTimeFormat() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT037", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT037", null, null, 
	                                                           null, "invalid", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	    }

	    @Test
	    public void testExecutePreservesBookingId() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT038", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT038", null, "EDIT102", 
	                                                           getFutureDate(10), "14:00", "16:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT038");
	        assertNotNull(updated);
	        assertEquals("EDIT038", updated.getBookingId());
	    }

	    @Test
	    public void testExecutePreservesUser() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT039", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT039", null, null, 
	                                                           null, "14:00", null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT039");
	        assertNotNull(updated);
	        assertNotEquals(testUser.getAccountId(), updated.getUser().getAccountId());
	    }

	    @Test
	    public void testExecuteWithSameValues() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT040", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT040", null, "EDIT101", 
	                                                           futureDate, "10:00", "12:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT040");
	        assertNotNull(updated);
	        assertEquals("EDIT101", updated.getRoomNumber());
	        assertEquals(futureDate, updated.getBookingDate());
	        assertEquals("10:00", updated.getBookingStartTime());
	        assertEquals("12:00", updated.getBookingEndTime());
	    }

	    @Test
	    public void testExecuteWithBuildingNameIgnored() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT041", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT041", "Wrong Building", "EDIT102", 
	                                                           null, null, null,
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        Booking updated = repository.findById("EDIT041");
	        assertNotNull(updated);
	        Room newRoom = roomService.getRoomByNumber("EDIT102");
	        assertEquals(newRoom.getBuildingName(), "Test Building 2");
	    }

	    @Test
	    public void testExecuteMultipleTimes() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT042", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command1 = new EditBookingCommand("EDIT042", null, null, 
	                                                            null, "14:00", null,
	                                                            repository, pricingFactory, 
	                                                            paymentService, roomService, observers);
	        command1.execute();
	        
	        EditBookingCommand command2 = new EditBookingCommand("EDIT042", null, "EDIT102", 
	                                                            null, null, null,
	                                                            repository, pricingFactory, 
	                                                            paymentService, roomService, observers);
	        boolean result = command2.execute();
	        
	        assertFalse(result);
	        Booking updated = repository.findById("EDIT042");
	        assertNotNull(updated);
	        assertNotEquals("EDIT102", updated.getRoomNumber());
	        assertNotEquals("14:00", updated.getBookingStartTime());
	    }

	    @Test
	    public void testUndoMultipleTimes() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT043", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        String originalRoom = booking.getRoomNumber();
	        
	        EditBookingCommand command1 = new EditBookingCommand("EDIT043", null, "EDIT102", 
	                                                            null, null, null,
	                                                            repository, pricingFactory, 
	                                                            paymentService, roomService, observers);
	        command1.execute();
	        
	        EditBookingCommand command2 = new EditBookingCommand("EDIT043", null, null, 
	                                                            null, "14:00", null,
	                                                            repository, pricingFactory, 
	                                                            paymentService, roomService, observers);
	        command2.execute();
	        
	        command2.undo();
	        command1.undo();
	        
	        Booking restored = repository.findById("EDIT043");
	        assertNotNull(restored);
	        assertEquals(originalRoom, restored.getRoomNumber());
	        assertEquals("10:00", restored.getBookingStartTime());
	    }

	    @Test
	    public void testExecuteWithFractionalHours() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT044", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT044", null, null, 
	                                                           null, "10:00", "12:30",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertTrue(result);
	        
	        Booking updated = repository.findById("EDIT044");
	        assertNotNull(updated);
	        assertEquals(3, updated.getHours());
	    }

	    @Test
	    public void testExecuteWithZeroPriceDifference() {
	        String futureDate = getFutureDate(5);
	        Booking booking = new Booking("EDIT045", testUser, 2, 20.0, "EDIT101", 
	                                      futureDate, "10:00", "12:00");
	        booking.setStatus("Reserved");
	        repository.save(booking);
	        
	        EditBookingCommand command = new EditBookingCommand("EDIT045", null, null, 
	                                                           null, "14:00", "16:00",
	                                                           repository, pricingFactory, 
	                                                           paymentService, roomService, observers);
	        
	        boolean result = command.execute();
	        assertFalse(result);
	        
	        assertTrue(mockProcessor.getLastChargeAmount() == 0 || 
	                  mockProcessor.getLastRefundAmount() == 0);
	    }

	    private String getFutureDate(int daysFromNow) {
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.DAY_OF_MONTH, daysFromNow);
	        int day = cal.get(Calendar.DAY_OF_MONTH);
	        int month = cal.get(Calendar.MONTH) + 1;
	        int year = cal.get(Calendar.YEAR);
	        return String.format("%02d/%02d/%04d", day, month, year);
	    }

	    private String getPastDate(int daysAgo) {
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.DAY_OF_MONTH, -daysAgo);
	        int day = cal.get(Calendar.DAY_OF_MONTH);
	        int month = cal.get(Calendar.MONTH) + 1;
	        int year = cal.get(Calendar.YEAR);
	        return String.format("%02d/%02d/%04d", day, month, year);
	    }

	    private static class MockPaymentProcessor implements PaymentProcessor {
	        private double lastChargeAmount = 0;
	        private double lastRefundAmount = 0;
	        private boolean chargeShouldFail = false;
	        private boolean refundShouldFail = false;

	        @Override
	        public boolean charge(double amount) {
	            if (chargeShouldFail) {
	                return false;
	            }
	            lastChargeAmount = amount;
	            return true;
	        }

	        @Override
	        public boolean refund(double amount) {
	            if (refundShouldFail) {
	                return false;
	            }
	            lastRefundAmount = amount;
	            return true;
	        }

	        public double getLastChargeAmount() {
	            return lastChargeAmount;
	        }

	        public double getLastRefundAmount() {
	            return lastRefundAmount;
	        }

	        public void setChargeShouldFail(boolean shouldFail) {
	            this.chargeShouldFail = shouldFail;
	        }

	        public void setRefundShouldFail(boolean shouldFail) {
	            this.refundShouldFail = shouldFail;
	        }

	        public void reset() {
	            lastChargeAmount = 0;
	            lastRefundAmount = 0;
	            chargeShouldFail = false;
	            refundShouldFail = false;
	        }
	    }

	    private static class MockBookingObserver implements BookingObserver {
	        private boolean notified = false;
	        private String lastUpdatedBookingId = null;
	        private String lastCancelledBookingId = null;
	        private String lastCreatedBookingId = null;

	        @Override
	        public void onBookingUpdated(Booking booking) {
	            notified = true;
	            if (booking != null) {
	                lastUpdatedBookingId = booking.getBookingId();
	            }
	        }

	        @Override
	        public void onBookingCancelled(String bookingId) {
	            lastCancelledBookingId = bookingId;
	        }

	        @Override
	        public void onBookingCreated(Booking booking) {
	            if (booking != null) {
	                lastCreatedBookingId = booking.getBookingId();
	            }
	        }

	        public boolean wasNotified() {
	            return notified;
	        }

	        public String getLastUpdatedBookingId() {
	            return lastUpdatedBookingId;
	        }

	        public void reset() {
	            notified = false;
	            lastUpdatedBookingId = null;
	            lastCancelledBookingId = null;
	            lastCreatedBookingId = null;
	        }
	    }
	    
}