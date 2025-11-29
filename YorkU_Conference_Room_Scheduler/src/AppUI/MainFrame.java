package AppUI;

import java.awt.EventQueue;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import Backend.ChiefEventCoordinator;

import Backend.Accounts;
import Backend.Booking;
import Backend.BookingController;
import Backend.BookingCSV;
import Backend.BookingTimeUtil;
import Backend.ReservationSystem;
import Backend.Room;
import Backend.RoomService;
import Backend.User;
import Backend.UserCSV;
import Backend.UserFactory;
import Backend.ValidationUtil;

public class MainFrame {
    private JFrame frame;
    
    IntroWindow introWindow = new IntroWindow();
    CheckInWindow checkInWindow = new CheckInWindow();
    LoginWindow loginWindow = new LoginWindow();
    RegisterWindow registerWindow = new RegisterWindow();
    DashboardWindow dashboardWindow = new DashboardWindow();
    
    ReserveRoomWindow reserveRoomWindow = new ReserveRoomWindow();
    BookingManagementWindow bookingManagementWindow = new BookingManagementWindow();
    AdminConsoleWindow adminConsoleWindow = new AdminConsoleWindow();
    CECWindow cecWindow = new CECWindow();
    
    // Backend references
    private UserCSV userCSV = UserCSV.getInstance();
    private UserFactory userFactory = new UserFactory();
    private RoomService roomService = new RoomService();
    private ReservationSystem reservationSystem = ReservationSystem.getInstance();
    private BookingCSV bookingCSV = BookingCSV.getInstance();
    
    // Currently logged in user
    private Accounts currentUser;
    
    // Currently selected room in admin console
    private Room selectedRoom = null;
    private Accounts selectedAccount = null;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame window = new MainFrame();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public MainFrame() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 703, 409);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setContentPane(introWindow.getPane());
        wireIntroHandlers();
        wireLoginHandlers();
        wireRegisterHandlers();
        wireCheckInHandlers();
        wireDashboardHandlers();
        wireReserveRoomHandlers();
        wireBookingManagementHandlers();
        wireAdminConsoleWindowHandlers();
        wireCECWindowHandlers();
        
        // Register observers for booking changes (REQ8, REQ9)
        BookingController controller = BookingController.getInstance();
        controller.addObserver(new BookingManagementObserver(this));
        controller.addObserver(new ReserveRoomObserver(this));
    }
    
    private void wireIntroHandlers() {
        introWindow.getBtnIntroCheckIn().addActionListener(e -> {
            System.out.println("Intro: Check In selected");
            frame.setContentPane(checkInWindow.getPane());
            refreshFrame();
        });

        introWindow.getBtnIntroReserve().addActionListener(e -> {
            System.out.println("Intro: Reserve selected");
            frame.setContentPane(loginWindow.getPane());
            refreshFrame();
        });
    }
    
    private void wireCheckInHandlers() {
        checkInWindow.getBtnBackToIntro().addActionListener(e -> {
            System.out.println("Back to Intro clicked");
            // Clear check-in fields
            checkInWindow.getBookingIdTextBox().setText("");
            checkInWindow.getEmailTextBox().setText("");
            checkInWindow.getOccupantsTextBox().setText("");
            checkInWindow.getRoomNumberTextBox().setText("");
            checkInWindow.getBookingTimeTextBox().setText("");
            frame.setContentPane(introWindow.getPane());
            refreshFrame();
        });

        checkInWindow.getBtnSimulateScan().addActionListener(e -> {
            String bookingId = checkInWindow.getBookingIdTextBox().getText().trim();
            String email = checkInWindow.getEmailTextBox().getText().trim();
            String occupants = checkInWindow.getOccupantsTextBox().getText().trim();

            System.out.println("Check in");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Email: " + email);
            System.out.println("Occupants: " + occupants);

            // Validate input
            if (bookingId.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter both Booking ID and Email.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Find booking from database using ReservationSystem
            Booking booking = reservationSystem.findBooking(bookingId);
            
            if (booking == null) {
                JOptionPane.showMessageDialog(frame,
                    "Booking not found. Please check the Booking ID.",
                    "Booking Not Found",
                    JOptionPane.ERROR_MESSAGE);
                checkInWindow.getRoomNumberTextBox().setText("");
                checkInWindow.getBookingTimeTextBox().setText("");
                return;
            }

            // Verify email matches
            if (!booking.getUser().getEmail().equalsIgnoreCase(email)) {
                JOptionPane.showMessageDialog(frame,
                    "Email does not match the booking. Please verify your email.",
                    "Email Mismatch",
                    JOptionPane.ERROR_MESSAGE);
                checkInWindow.getRoomNumberTextBox().setText("");
                checkInWindow.getBookingTimeTextBox().setText("");
                return;
            }

            // Display booking information
            String roomNumber = booking.getRoomNumber() != null ? booking.getRoomNumber() : "Not assigned";
            String bookingTime = "";
            if (booking.getBookingStartTime() != null && booking.getBookingEndTime() != null) {
                bookingTime = booking.getBookingStartTime() + " - " + booking.getBookingEndTime();
            } else if (booking.getBookingDate() != null) {
                bookingTime = booking.getBookingDate();
            } else {
                bookingTime = "Time not specified";
            }

            checkInWindow.getRoomNumberTextBox().setText(roomNumber);
            checkInWindow.getBookingTimeTextBox().setText(bookingTime);

            // Perform check-in
            boolean checkInSuccess = reservationSystem.checkIn(bookingId, email);
            
            if (checkInSuccess) {
                JOptionPane.showMessageDialog(frame,
                    "Check-in successful! Room: " + roomNumber,
                    "Check-In Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                    "Check-in failed. Please contact support.",
                    "Check-In Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void wireLoginHandlers() {
        loginWindow.getBtnBackToIntro().addActionListener(e -> {
            System.out.println("Back to Intro clicked");
            clearLoginFields();
            frame.setContentPane(introWindow.getPane());
            refreshFrame();
        });

        loginWindow.getBtnLogin().addActionListener(e -> {
            System.out.println("Login Button Clicked");
            
            String email = loginWindow.getTxtBoxEmail().getText().trim();
            String password = String.valueOf(loginWindow.getTxtBoxPassword().getPassword());
            
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Please enter both email and password.", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Accounts account = userCSV.findByEmail(email);
            
            if (account != null && account.getPassword().equals(password)) {
                currentUser = account;
                JOptionPane.showMessageDialog(frame, 
                    "Login Successful! Welcome, " + account.getAccountType() + ".", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                currentUser = account;
                clearLoginFields();
                String accountType = account.getAccountType();
                if(accountType.equals("Admin")) {
                    dashboardWindow.getBtnDashCECPanel().setVisible(false);
                    dashboardWindow.getBtnDashAdminConsole().setVisible(true);
                    dashboardWindow.getBtnDashAdminConsole().setLocation(dashboardWindow.getBtnDashCECPanel().getLocation());
                }
                else if (accountType.equals("Chief Event Coordinator")) {
                    dashboardWindow.getBtnDashAdminConsole().setVisible(true);
                    dashboardWindow.getBtnDashCECPanel().setVisible(true);
                }
                else {
                    dashboardWindow.getBtnDashAdminConsole().setVisible(false);
                    dashboardWindow.getBtnDashCECPanel().setVisible(false);
                } 
                frame.setContentPane(dashboardWindow.getPane());
                refreshFrame();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Invalid email or password.", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
                loginWindow.getTxtBoxPassword().setText("");
            }
        });

        loginWindow.getBtnRegister().addActionListener(e -> {
            System.out.println("Registering new Account...");
            clearLoginFields();
            frame.setContentPane(registerWindow.getPane());
            refreshFrame();
        });
    }
    
    private void wireRegisterHandlers() {
        registerWindow.getBtnBackToLogin().addActionListener(e -> {
            System.out.println("Back to Login clicked");
            clearRegisterFields();
            frame.setContentPane(loginWindow.getPane());
            refreshFrame();
        });

        registerWindow.getBtnRegister().addActionListener(e -> {
            System.out.println("Register Button Clicked");
            
            String email = registerWindow.getTxtBoxEmail().getText().trim();
            String password = String.valueOf(registerWindow.getTxtBoxPassword().getPassword());
            String accountType = (String) registerWindow.getAccountTypeComboBox().getSelectedItem();
            String orgId = String.valueOf(registerWindow.getTxtBoxOrgID().getPassword()).trim();
            
            if (accountType.equals("(Select Account Type)")) {
                JOptionPane.showMessageDialog(frame, 
                    "Please select an account type.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
         // Validate email (account type specific)
            if (!ValidationUtil.isValidEmail(email, accountType)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getEmailRequirements(accountType), 
                    "Invalid Email", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (userCSV.emailExists(email)) {
                JOptionPane.showMessageDialog(frame, 
                    "An account with this email already exists. Please login instead.", 
                    "Email Already Registered", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!ValidationUtil.isValidPassword(password)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getPasswordRequirements(), 
                    "Invalid Password", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
         // Validate org ID (account type specific)
            if (!ValidationUtil.isValidOrgId(orgId, accountType)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getOrgIdRequirements(accountType), 
                    "Invalid Organization ID", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                User newUser = userFactory.createUser(email, password, accountType, orgId);
                userCSV.write(newUser);
                
                JOptionPane.showMessageDialog(frame, 
                    "Registration Successful! Please login with your credentials.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                clearRegisterFields();
                frame.setContentPane(loginWindow.getPane());
                refreshFrame();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Registration failed: " + ex.getMessage(), 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
    
    private void wireDashboardHandlers() {
        dashboardWindow.getBtnDashReserve().addActionListener(e -> {
            System.out.println("Dashboard: Book Room clicked");
            refreshReserveRoomTable();
            frame.setContentPane(reserveRoomWindow.getPane());
            refreshFrame();
        });
        
        dashboardWindow.getBtnDashMyBookings().addActionListener(e -> {
            System.out.println("Dashboard: Manage Bookings clicked");
            refreshBookingTable();
            frame.setContentPane(bookingManagementWindow.getPane());
            refreshFrame();
        });
        
        dashboardWindow.getBtnDashAdminConsole().addActionListener(e -> {
            System.out.println("Dashboard: Admin Console clicked");
            refreshRoomTable();
            clearAdminConsoleFields();
            frame.setContentPane(adminConsoleWindow.getPane());
            refreshFrame();
        });
        
        dashboardWindow.getBtnDashCECPanel().addActionListener(e -> {
            System.out.println("Dashboard: CEC Panel clicked");
            refreshUserTable();
            clearCECFields();
            frame.setContentPane(cecWindow.getPane());
            refreshFrame();
        });
        dashboardWindow.getBtnDashLogout().addActionListener(e -> {
            System.out.println("Dashboard: Logout clicked");
            currentUser = null;
            frame.setContentPane(introWindow.getPane());
            refreshFrame();
        });
    }
    
    private void wireReserveRoomHandlers() {
        reserveRoomWindow.getBtnBackToDashboard().addActionListener(e -> {
            System.out.println("Reserve Room: Back to dashboard clicked");
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        // Add listener to room table selection to update time slot table
        reserveRoomWindow.getRoomTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = reserveRoomWindow.getRoomTable().getSelectedRow();
                String roomNumber = null;
                if (selectedRow >= 0) {
                    Object roomValue = reserveRoomWindow.getRoomTable().getValueAt(selectedRow, 0);
                    if (roomValue != null) {
                        roomNumber = String.valueOf(roomValue).trim();
                        System.out.println("Room selected: '" + roomNumber + "'");
                    }
                }
                String date = reserveRoomWindow.getBookDateTextBox().getText().trim();
                System.out.println("Date from text box: '" + date + "'");
                refreshTimeSlotTable(roomNumber, date);
            }
        });
        
        // Add listener to date text box to update time slot table when date changes
        reserveRoomWindow.getBookDateTextBox().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTimeSlotTable();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTimeSlotTable();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTimeSlotTable();
            }
            
            private void updateTimeSlotTable() {
                String roomNumber = reserveRoomWindow.getRoomTextBox().getText().trim();
                String date = reserveRoomWindow.getBookDateTextBox().getText().trim();
                refreshTimeSlotTable(roomNumber, date);
            }
        });
        
        reserveRoomWindow.getBtnCalculate().addActionListener(e -> {
            // Validate user is logged in
            if (currentUser == null || !(currentUser instanceof User)) {
                JOptionPane.showMessageDialog(frame,
                    "Please login to calculate rates.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Calculate hourly rate based on user type
            User user = (User) currentUser;
            double rate = reservationSystem.calculateHourlyRate(user);
            
            System.out.println("Reserve Room: Calculate clicked, Rate = " + rate);
            reserveRoomWindow.getHourlyRateTextBox().setText(String.valueOf(rate));
            refreshFrame();
        });
        
        reserveRoomWindow.getBtnBook().addActionListener(e -> {
            System.out.println("Reserve Room: Book and Pay clicked");
            PaymentWindow paymentWindow = new PaymentWindow(
                Double.parseDouble(reserveRoomWindow.getHourlyRateTextBox().getText()));
            frame.setContentPane(paymentWindow.getPane());
            wirePaymentWindowHandlers(paymentWindow);
            refreshFrame();
        });
    }
    
    private void wireBookingManagementHandlers() {
        bookingManagementWindow.getBtnBackToDashboard().addActionListener(e -> {
            System.out.println("Booking management: Back to dashboard clicked");
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        bookingManagementWindow.getBtnApplyEdit().addActionListener(e -> {
            String bookingId = bookingManagementWindow.getSelectedBookingIdTextBox().getText().trim();
            
            if (bookingId == null || bookingId.isEmpty() || bookingId.equals("null")) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a booking to edit.",
                    "No Booking Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get new values from text fields
            String newBuilding = bookingManagementWindow.getNewBuildingTextBox().getText().trim();
            String newRoomNumber = bookingManagementWindow.getNewRoomNumberTextBox().getText().trim();
            String newDate = bookingManagementWindow.getNewDateTextBox().getText().trim();
            String newStartTime = bookingManagementWindow.getNewStartTimeTextBox().getText().trim();
            String newEndTime = bookingManagementWindow.getNewEndTimeTextBox().getText().trim();
            
            if (newBuilding.isEmpty() && newRoomNumber.isEmpty() && newDate.isEmpty() && 
                newStartTime.isEmpty() && newEndTime.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter at least one field to update.",
                    "No Changes Specified",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // If room number is provided, look up the building name from the room
                if (!newRoomNumber.isEmpty()) {
                    Room room = roomService.getRoomByNumber(newRoomNumber);
                    if (room != null) {
                        // Use the room's building name if building name is not provided
                        if (newBuilding.isEmpty()) {
                            newBuilding = room.getBuildingName();
                        }
                        // Validate that the provided building name matches the room's building
                        else if (!newBuilding.equals(room.getBuildingName())) {
                            JOptionPane.showMessageDialog(frame,
                                "Building name does not match the room. Room " + newRoomNumber + 
                                " is in " + room.getBuildingName() + ".",
                                "Invalid Building",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "Room " + newRoomNumber + " not found.",
                            "Room Not Found",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Use BookingController to execute edit command
                BookingController controller = BookingController.getInstance();
                boolean success = controller.editBooking(bookingId, 
                    newBuilding.isEmpty() ? null : newBuilding,
                    newRoomNumber.isEmpty() ? null : newRoomNumber,
                    newDate.isEmpty() ? null : newDate,
                    newStartTime.isEmpty() ? null : newStartTime,
                    newEndTime.isEmpty() ? null : newEndTime);
                
                if (success) {
                    JOptionPane.showMessageDialog(frame,
                        "Booking " + bookingId + " has been updated successfully.",
                        "Booking Updated",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the booking table
                    refreshBookingTable();
                    
                    // Clear edit fields
                    bookingManagementWindow.getNewBuildingTextBox().setText("");
                    bookingManagementWindow.getNewRoomNumberTextBox().setText("");
                    bookingManagementWindow.getNewDateTextBox().setText("");
                    bookingManagementWindow.getNewStartTimeTextBox().setText("");
                    bookingManagementWindow.getNewEndTimeTextBox().setText("");
                } else {
                    JOptionPane.showMessageDialog(frame,
                        "Failed to update booking. The booking may not be in a valid state for editing.",
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error updating booking: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        bookingManagementWindow.getBtnExtendBooking().addActionListener(e -> {
            String bookingId = bookingManagementWindow.getSelectedBookingIdTextBox().getText().trim();
            String extraHoursText = bookingManagementWindow.getExtendByHoursTextBox().getText().trim();
            
            if (bookingId == null || bookingId.isEmpty() || bookingId.equals("null")) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a booking to extend.",
                    "No Booking Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (extraHoursText.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter the number of hours to extend.",
                    "No Duration Specified",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int extraHours = Integer.parseInt(extraHoursText);
                if (extraHours <= 0) {
                    JOptionPane.showMessageDialog(frame,
                        "Please enter a positive number of hours.",
                        "Invalid Duration",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Use BookingController to execute extend command
                BookingController controller = BookingController.getInstance();
                boolean success = controller.extendBooking(bookingId, extraHours);
                
                if (success) {
                    JOptionPane.showMessageDialog(frame,
                        "Booking " + bookingId + " has been extended by " + extraHours + " hours.",
                        "Booking Extended",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the booking table
                    refreshBookingTable();
                    
                    // Clear extend field
                    bookingManagementWindow.getExtendByHoursTextBox().setText("");
                } else {
                    // Get more details about why extension failed
                    Booking booking = bookingCSV.findById(bookingId);
                    String errorMsg = "Failed to extend booking. ";
                    if (booking != null) {
                        errorMsg += "Booking status: '" + booking.getStatus() + "'. ";
                        String bookingDate = booking.getBookingDate();
                        String bookingEndTime = booking.getBookingEndTime();
                        
                        if (bookingEndTime == null || bookingEndTime.trim().isEmpty()) {
                            errorMsg += "Booking end time is not set.";
                        } else if (bookingDate == null || bookingDate.trim().isEmpty()) {
                            errorMsg += "Booking date is not set.";
                        } else if (BookingTimeUtil.hasEndTimePassed(bookingDate, bookingEndTime)) {
                            errorMsg += "Booking end time (" + bookingEndTime + " on " + bookingDate + ") has already passed.";
                        } else {
                            errorMsg += "The extended time slot may already be reserved by another booking.";
                        }
                    } else {
                        errorMsg += "Booking not found.";
                    }
                    
                    JOptionPane.showMessageDialog(frame,
                        errorMsg,
                        "Extension Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter a valid number of hours.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error extending booking: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        bookingManagementWindow.getBtnCancelBooking().addActionListener(e -> {
            String bookingId = bookingManagementWindow.getSelectedBookingIdTextBox().getText().trim();
            
            if (bookingId == null || bookingId.isEmpty() || bookingId.equals("null")) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a booking to cancel.",
                    "No Booking Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Confirm cancellation
            int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to cancel booking " + bookingId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Use BookingController to execute cancel command (REQ8)
                    BookingController controller = BookingController.getInstance();
                    boolean cancelled = controller.cancelBooking(bookingId);
                    
                    if (cancelled) {
                        JOptionPane.showMessageDialog(frame,
                            "Booking " + bookingId + " has been cancelled successfully.",
                            "Booking Cancelled",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh the booking table
                        refreshBookingTable();
                        
                        // Clear the selected booking fields
                        bookingManagementWindow.getSelectedBookingIdTextBox().setText("");
                        bookingManagementWindow.getSelectedRoomTextBox().setText("");
                        bookingManagementWindow.getSelectedDateTextBox().setText("");
                        bookingManagementWindow.getSelectedTimeTextBox().setText("");
                        bookingManagementWindow.getNewDateTextBox().setText("");
                        bookingManagementWindow.getNewStartTimeTextBox().setText("");
                    } else {
                        // Get more details about why cancellation failed
                        Booking booking = bookingCSV.findById(bookingId);
                        String errorMsg = "Failed to cancel booking. ";
                        if (booking != null) {
                            errorMsg += "Booking status: '" + booking.getStatus() + "'. ";
                            errorMsg += "Booking must be in 'Reserved' state and start time must not have passed.";
                        } else {
                            errorMsg += "Booking not found.";
                        }
                        
                        JOptionPane.showMessageDialog(frame,
                            errorMsg,
                            "Cancellation Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                        "Error cancelling booking: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        
        bookingManagementWindow.getBtnRefresh().addActionListener(e -> {
            System.out.println("Refresh clicked");
            refreshBookingTable();
            refreshFrame();
        });
    }
    
    private void wireAdminConsoleWindowHandlers() {
        adminConsoleWindow.getBtnBackToDashboard().addActionListener(e -> {
            System.out.println("Admin Console: Back to dashboard clicked");
            selectedRoom = null;
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        // Table row selection handler
        adminConsoleWindow.getRoomTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = adminConsoleWindow.getRoomTable().getSelectedRow();
                if (row != -1) {
                    String roomNumber = (String) adminConsoleWindow.getRoomTable().getValueAt(row, 0);
                    String building = (String) adminConsoleWindow.getRoomTable().getValueAt(row, 1);
                    
                    if (roomNumber != null && building != null) {
                        selectedRoom = roomService.getRoomByLocation(building, roomNumber);
                    }
                }
            }
        });
        
        // Add Room button
        adminConsoleWindow.getBtnAddRoom().addActionListener(e -> {
            System.out.println("Admin Console: Add Room clicked");
            
            String roomNumber = adminConsoleWindow.getRoomIdTextBox().getText().trim();
            String building = adminConsoleWindow.getBuildingTextBox().getText().trim();
            String capacityStr = adminConsoleWindow.getCapacityTextBox().getText().trim();
            
            if (roomNumber.isEmpty() || building.isEmpty() || capacityStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Please fill in Room ID, Building, and Capacity.",
                    "Missing Information",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Please enter a valid positive number for capacity.",
                    "Invalid Capacity",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Room newRoom = roomService.addRoom(capacity, building, roomNumber);
            
            if (newRoom != null) {
                JOptionPane.showMessageDialog(frame,
                    "Room added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshRoomTable();
                clearAdminConsoleFields();
            } else {
                JOptionPane.showMessageDialog(frame,
                    "Room already exists at this location.",
                    "Add Room Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
     // Save Changes button (update condition)
        adminConsoleWindow.getBtnSaveChanges().addActionListener(e -> {
            System.out.println("Admin Console: Save Changes clicked");
            
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a room from the table first.",
                    "No Room Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String selectedCondition = (String) adminConsoleWindow.getConditionDropdown().getSelectedItem();
            String currentCondition = selectedRoom.getCondition();
            
            // Map dropdown value to state name
            String targetState = mapDropdownToState(selectedCondition);
            
            if (targetState.equals(currentCondition)) {
                JOptionPane.showMessageDialog(frame,
                    "Room is already in " + selectedCondition + " condition.",
                    "No Change",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            boolean success = changeRoomCondition(selectedRoom, targetState);
            
            if (success) {
                JOptionPane.showMessageDialog(frame,
                    "Room condition updated to " + selectedCondition + ".",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                refreshRoomTable();
                refreshFrame();
            } else {
                JOptionPane.showMessageDialog(frame,
                    "Cannot change condition from " + currentCondition + " to " + selectedCondition + 
                    ". Please use the appropriate action or ensure the transition is valid.",
                    "Invalid Transition",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Enable Room button
        adminConsoleWindow.getBtnEnableRoom().addActionListener(e -> {
            System.out.println("Admin Console: Enable Room clicked");
            
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a room from the table first.",
                    "No Room Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (selectedRoom.getStatus().equals("ENABLED")) {
                JOptionPane.showMessageDialog(frame,
                    "Room is already enabled.",
                    "Already Enabled",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            roomService.enableRoom(selectedRoom.getRoomId());
            JOptionPane.showMessageDialog(frame,
                "Room enabled successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            refreshRoomTable();
            adminConsoleWindow.getStatusTextBox().setText("Enabled");
        });
        
        // Disable Room button
        adminConsoleWindow.getBtnDisableRoom().addActionListener(e -> {
            System.out.println("Admin Console: Disable Room clicked");
            
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a room from the table first.",
                    "No Room Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (selectedRoom.getStatus().equals("DISABLED")) {
                JOptionPane.showMessageDialog(frame,
                    "Room is already disabled.",
                    "Already Disabled",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            roomService.disableRoom(selectedRoom.getRoomId());
            JOptionPane.showMessageDialog(frame,
                "Room disabled successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            refreshRoomTable();
            adminConsoleWindow.getStatusTextBox().setText("Disabled");
        });
        
        // Refresh button
        adminConsoleWindow.getBtnRefreshRooms().addActionListener(e -> {
            System.out.println("Admin Console: Refresh rooms clicked");
            refreshRoomTable();
            clearAdminConsoleFields();
            selectedRoom = null;
        });
    }
    
    private void wireCECWindowHandlers() {
        cecWindow.getBtnBackToDashboard().addActionListener(e -> {
            System.out.println("CEC: Back to dashboard clicked");
            selectedAccount = null;
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
     // Table row selection handler
        cecWindow.getAdminTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = cecWindow.getAdminTable().getSelectedRow();
                if (row != -1) {
                    DefaultTableModel model = (DefaultTableModel) cecWindow.getAdminTable().getModel();
                    Object userIdObj = model.getValueAt(row, 0);
                    Object emailObj = model.getValueAt(row, 1);
                    
                    System.out.println("Row selected: " + row + ", UserID: " + userIdObj);
                    
                    if (userIdObj != null && emailObj != null) {
                        String email = emailObj.toString();
                        
                        // Find by email instead of UUID
                        selectedAccount = userCSV.findByEmail(email);
                        
                        if (selectedAccount != null) {
                            cecWindow.getAdminIdTextBox().setText(selectedAccount.getAccountId().toString());
                            cecWindow.getAdminStatusTextBox().setText(selectedAccount.getAccountType());
                            System.out.println("Selected account: " + selectedAccount.getEmail());
                        } else {
                            System.out.println("Account not found for email: " + email);
                        }
                    }
                }
            }
        });
        
        // Grant Admin button
        cecWindow.getBtnGrantAdmin().addActionListener(e -> {
            System.out.println("CEC: Grant Admin clicked");
            
            if (selectedAccount == null) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a user from the table first.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (selectedAccount.getAccountType().equals("Admin")) {
                JOptionPane.showMessageDialog(frame,
                    "User is already an Admin.",
                    "Already Admin",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if (selectedAccount.getAccountType().equals("Chief Event Coordinator")) {
                JOptionPane.showMessageDialog(frame,
                    "Cannot modify Chief Event Coordinator account.",
                    "Invalid Operation",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
         // Update account type to Admin in CSV directly
            userCSV.updateAccountTypeByEmail(selectedAccount.getEmail(), "Admin");

            JOptionPane.showMessageDialog(frame,
                "User " + selectedAccount.getEmail() + " has been granted Admin privileges.",
                "Admin Granted",
                JOptionPane.INFORMATION_MESSAGE);

            refreshUserTable();
            clearCECFields();
            selectedAccount = null;
            refreshFrame();
        });
        
        /*
        // Disable Admin button - Commented out as per requirements
        cecWindow.getBtnDisableAdmin().addActionListener(e -> {
            System.out.println("CEC: Disable Admin clicked for " + 
                cecWindow.getAdminIdTextBox().getText());
        });
        */
        
        // Refresh button
        cecWindow.getBtnRefreshAdmins().addActionListener(e -> {
            System.out.println("CEC: Refresh clicked (admins + logs)");
            refreshUserTable();
            clearCECFields();
            selectedAccount = null;
            refreshFrame();
        });
    }
    
    private void wirePaymentWindowHandlers(PaymentWindow paymentWindow) {
        paymentWindow.getBtnConfirmPayment().addActionListener(e -> {
            String method = (String) paymentWindow.getPaymentMethodComboBox().getSelectedItem();
            
            // Validate payment method selected
            if (method == null || method.equals("Select a method...")) {
                JOptionPane.showMessageDialog(frame,
                    "Please select a payment method.",
                    "Payment Method Required",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get booking information from ReserveRoomWindow
            String roomNumber = reserveRoomWindow.getRoomTextBox().getText().trim();
            String bookingDate = reserveRoomWindow.getBookDateTextBox().getText().trim();
            String bookingTime = reserveRoomWindow.getBookTimeTextBox().getText().trim();
            String rateText = reserveRoomWindow.getHourlyRateTextBox().getText().trim();
            
            // Validate required fields
            if (roomNumber.isEmpty() || bookingDate.isEmpty() || bookingTime.isEmpty() || rateText.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                    "Missing booking information. Please go back and complete the reservation form.",
                    "Incomplete Booking",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate current user
            if (currentUser == null || !(currentUser instanceof User)) {
                JOptionPane.showMessageDialog(frame,
                    "User session expired. Please login again.",
                    "Session Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                User user = (User) currentUser;
                double rate = Double.parseDouble(rateText);
                
                // Parse time range (e.g., "09:00 - 11:00" or just "09:00")
                String startTime = bookingTime;
                String endTime = null;
                int hours = 1; // Default to 1 hour
                
                if (bookingTime.contains(" - ")) {
                    String[] times = bookingTime.split(" - ");
                    startTime = times[0].trim();
                    endTime = times.length > 1 ? times[1].trim() : null;
                    
                    // Calculate hours from time difference
                    if (endTime != null) {
                        try {
                            String[] startParts = startTime.split(":");
                            String[] endParts = endTime.split(":");
                            int startHour = Integer.parseInt(startParts[0]);
                            int startMin = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
                            int endHour = Integer.parseInt(endParts[0]);
                            int endMin = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 0;
                            
                            int startTotalMinutes = startHour * 60 + startMin;
                            int endTotalMinutes = endHour * 60 + endMin;
                            int diffMinutes = endTotalMinutes - startTotalMinutes;
                            
                            // Round up to nearest hour
                            hours = (int) Math.ceil(diffMinutes / 60.0);
                            if (hours < 1) hours = 1;
                        } catch (Exception ex) {
                            // If parsing fails, default to 1 hour
                            hours = 1;
                        }
                    }
                }
                
                // If end time is not provided, calculate it as start time + 1 hour
                if (endTime == null || endTime.trim().isEmpty()) {
                    try {
                        String[] startParts = startTime.split(":");
                        int startHour = Integer.parseInt(startParts[0]);
                        int startMin = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 0;
                        
                        // Add 1 hour
                        startHour += 1;
                        if (startHour >= 24) {
                            startHour = startHour % 24;
                        }
                        
                        endTime = String.format("%02d:%02d", startHour, startMin);
                        hours = 1; // Default to 1 hour
                        System.out.println("Calculated end time from start time: " + startTime + " -> " + endTime);
                    } catch (Exception ex) {
                        System.err.println("Error calculating end time: " + ex.getMessage());
                        // If calculation fails, default to start time + 1 hour as string
                        endTime = startTime; // This will be handled by the backend
                    }
                }
                
                // Create booking using ReservationSystem
                Booking booking = reservationSystem.createBooking(
                    user,
                    hours,
                    rate,
                    roomNumber,
                    bookingDate,
                    startTime,
                    endTime
                );
                
                System.out.println("Payment confirmed for " + paymentWindow.getAmountTextBox().getText()
                                   + " via " + method);
                System.out.println("Booking created: " + booking.getBookingId());
                
                JOptionPane.showMessageDialog(frame,
                    "Payment processed and booking confirmed!\n" +
                    "Booking ID: " + booking.getBookingId() + "\n" +
                    "Room: " + roomNumber + "\n" +
                    "Date: " + bookingDate + "\n" +
                    "Time: " + bookingTime,
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear reservation form
                reserveRoomWindow.getRoomTextBox().setText("");
                reserveRoomWindow.getBookDateTextBox().setText("");
                reserveRoomWindow.getBookTimeTextBox().setText("");
                reserveRoomWindow.getHourlyRateTextBox().setText("");
                
                frame.setContentPane(dashboardWindow.getPane());
                refreshFrame();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                    "Invalid rate value. Please calculate the rate again.",
                    "Invalid Rate",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error creating booking: " + ex.getMessage(),
                    "Booking Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        paymentWindow.getBtnCancelPayment().addActionListener(e -> {
            System.out.println("Payment cancelled back to Reserve Room");
            frame.setContentPane(reserveRoomWindow.getPane());
            refreshFrame();
        });
    }
    
    // Helper method to refresh reserve room table with rooms from RoomDatabase.csv
    private void refreshReserveRoomTable() {
        // Get all rooms from RoomDatabase.csv
        List<Room> allRooms = roomService.getAllRooms();
        DefaultTableModel model = (DefaultTableModel) reserveRoomWindow.getRoomTable().getModel();
        
        // Clear existing rows
        model.setRowCount(0);
        
        // Add all rooms from RoomDatabase.csv
        for (Room room : allRooms) {
            // Determine status based on room status
            // Note: Time slot availability is checked separately when booking
            String status;
            if (room.getStatus().equals("ENABLED")) {
                // Room is enabled - show current condition
                // Time slot availability will be checked when user selects a date/time
                status = room.getCondition(); // Show current condition (Available, Reserved, InUse, etc.)
            } else {
                status = "Inactive";
            }
            
            model.addRow(new Object[]{
                room.getRoomNumber(),
                room.getBuildingName(),
                room.getCapacity(),
                status
            });
        }
        
        // Add empty rows to fill table if needed
        int emptyRows = Math.max(0, 15 - allRooms.size());
        for (int i = 0; i < emptyRows; i++) {
            model.addRow(new Object[]{null, null, null, null});
        }
    }
    
    // Helper method to refresh time slot table for currently selected room and date
    // Made public so observers can call it
    public void refreshTimeSlotTableForSelectedRoom() {
        String roomNumber = reserveRoomWindow.getRoomTextBox().getText().trim();
        String date = reserveRoomWindow.getBookDateTextBox().getText().trim();
        refreshTimeSlotTable(roomNumber, date);
    }
    
    // Helper method to refresh time slot table based on selected room and date
    private void refreshTimeSlotTable(String roomNumber, String date) {
        DefaultTableModel model = (DefaultTableModel) reserveRoomWindow.getTimeSlotTable().getModel();
        
        // Time slots from 09:00 to 16:00 in 30-minute intervals
        String[] timeSlots = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00"
        };
        
        // Clear existing rows
        model.setRowCount(0);
        
        // Normalize inputs
        String normalizedRoomNumber = (roomNumber != null) ? roomNumber.trim() : "";
        String normalizedDate = (date != null) ? date.trim() : "";
        
        System.out.println("refreshTimeSlotTable called with Room='" + normalizedRoomNumber + "', Date='" + normalizedDate + "'");
        
        // If no room or date selected, show all as available
        if (normalizedRoomNumber.isEmpty() || normalizedDate.isEmpty()) {
            System.out.println("refreshTimeSlotTable: No room or date, showing all available");
            for (String time : timeSlots) {
                model.addRow(new Object[]{time, "Available"});
            }
            return;
        }
        
        // Get existing bookings for this room on this date from BookingDatabase.csv
        List<Map<String, String>> existingBookings = bookingCSV.getBookingsForRoomAndDate(normalizedRoomNumber, normalizedDate);
        
        System.out.println("refreshTimeSlotTable: Room='" + normalizedRoomNumber + "', Date='" + normalizedDate + "', Found " + existingBookings.size() + " bookings");
        
        // Debug: Print all bookings found
        for (Map<String, String> booking : existingBookings) {
            System.out.println("  Booking: startTime='" + booking.get("startTime") + "', endTime='" + booking.get("endTime") + "'");
        }
        
        // Populate time slot table
        for (String timeSlot : timeSlots) {
            String availability = "Available";
            
            // Check if this time slot conflicts with any existing booking
            for (Map<String, String> booking : existingBookings) {
                String bookingStartTime = booking.get("startTime");
                String bookingEndTime = booking.get("endTime");
                
                if (bookingStartTime == null || bookingStartTime.trim().isEmpty()) {
                    continue; // Skip if no start time
                }
                
                bookingStartTime = bookingStartTime.trim();
                
                // First check: if the time slot exactly matches the booking start time, it's reserved
                if (timeSlot.equals(bookingStartTime)) {
                    availability = "Reserved";
                    System.out.println("Time slot " + timeSlot + " is RESERVED (matches start time: " + bookingStartTime + ")");
                    break;
                }
                
                // Second check: if we have an end time, check if time slot falls within the booking range
                if (bookingEndTime != null && !bookingEndTime.trim().isEmpty()) {
                    bookingEndTime = bookingEndTime.trim();
                    if (isTimeInRange(timeSlot, bookingStartTime, bookingEndTime)) {
                        availability = "Reserved";
                        System.out.println("Time slot " + timeSlot + " is RESERVED (within range: " + bookingStartTime + " - " + bookingEndTime + ")");
                        break;
                    }
                }
            }
            
            model.addRow(new Object[]{timeSlot, availability});
        }
    }
    
    // Helper method to check if a time is within a range
    private boolean isTimeInRange(String time, String startTime, String endTime) {
        try {
            int timeMinutes = parseTimeToMinutes(time);
            int startMinutes = parseTimeToMinutes(startTime);
            int endMinutes = parseTimeToMinutes(endTime);
            
            // Check if time is within the range (inclusive start, inclusive end for time slots)
            // A time slot is reserved if it falls within or exactly matches the booking range
            return timeMinutes >= startMinutes && timeMinutes <= endMinutes;
        } catch (Exception e) {
            // If parsing fails, do simple string comparison
            System.err.println("Error parsing time: " + e.getMessage());
            return time.equals(startTime) || time.equals(endTime);
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
    
    // Helper method to refresh booking table from BookingDatabase.csv
    // Made public so observers can call it
    public void refreshBookingTable() {
        System.out.println("refreshBookingTable: Loading all bookings from BookingDatabase.csv");
        
        DefaultTableModel model = (DefaultTableModel) bookingManagementWindow.getBookingTable().getModel();
        
        // Clear existing rows
        model.setRowCount(0);
        
        // Get all booking records directly from CSV (no user lookup needed)
        List<Map<String, String>> bookingRecords = bookingCSV.getAllBookingRecords();
        
        System.out.println("refreshBookingTable: Found " + bookingRecords.size() + " booking records in CSV");
        
        // Debug: Print all end times
        for (Map<String, String> record : bookingRecords) {
            System.out.println("refreshBookingTable: Booking " + record.get("bookingId") + 
                             " - End time: " + record.get("endTime"));
        }
        
        // Add all booking records to table
        for (Map<String, String> record : bookingRecords) {
            String bookingId = record.get("bookingId");
            String roomId = record.get("roomId");
            String buildingName = record.get("buildingName");
            String roomNumber = record.get("roomNumber");
            String date = record.get("date");
            String startTime = record.get("startTime");
            String endTime = record.get("endTime");
            String status = record.get("status");
            
            System.out.println("Adding booking to table: " + bookingId + " - " + roomNumber + " - " + date);
            
            model.addRow(new Object[]{
                bookingId,
                roomId,
                buildingName,
                roomNumber,
                date,
                startTime,
                endTime != null && !endTime.equals("N/A") ? endTime : "",
                status
            });
        }
        
        // Add empty rows to fill table if needed
        int emptyRows = Math.max(0, 10 - bookingRecords.size());
        for (int i = 0; i < emptyRows; i++) {
            model.addRow(new Object[]{null, null, null, null, null, null, null});
        }
        
        // Force table to repaint and revalidate
        bookingManagementWindow.getBookingTable().repaint();
        bookingManagementWindow.getBookingTable().revalidate();
        
        // Fire table data changed event to ensure UI updates
        model.fireTableDataChanged();
        
        System.out.println("Table refreshed. Total rows in table: " + model.getRowCount());
        System.out.println("Table model row count: " + model.getRowCount() + ", column count: " + model.getColumnCount());
    }
    
    // Helper method to refresh room table from CSV
    private void refreshRoomTable() {
        List<Room> rooms = roomService.getAllRooms();
        DefaultTableModel model = (DefaultTableModel) adminConsoleWindow.getRoomTable().getModel();
        
        // Clear existing rows
        model.setRowCount(0);
        
        // Add rooms from database
        for (Room room : rooms) {
            model.addRow(new Object[]{
                room.getRoomNumber(),
                room.getBuildingName(),
                room.getCapacity(),
                mapStateToDropdown(room.getCondition()),
                room.getStatus().equals("ENABLED") ? "Enabled" : "Disabled"
            });
        }
        
        // Add empty rows to fill table
        int emptyRows = 10 - rooms.size();
        for (int i = 0; i < emptyRows && i < 10; i++) {
            model.addRow(new Object[]{null, null, null, null, null});
        }
    }
    
    // Map state name to dropdown display value
    private String mapStateToDropdown(String stateName) {
        switch (stateName) {
            case "Available": return "Available";
            case "Reserved": return "Reserved";
            case "InUse": return "In Use";
            case "Maintenance": return "Closed for Maintenance";
            case "NoShow": return "No-Show";
            default: return stateName;
        }
    }
    
    // Map dropdown display value to state name
    private String mapDropdownToState(String dropdownValue) {
        switch (dropdownValue) {
            case "Available": return "Available";
            case "Reserved": return "Reserved";
            case "In Use": return "InUse";
            case "Closed for Maintenance": return "Maintenance";
            case "No-Show": return "NoShow";
            default: return dropdownValue;
        }
    }
    
 // Change room condition using RoomService
    private boolean changeRoomCondition(Room room, String targetState) {
        String currentState = room.getCondition();
        
        switch (targetState) {
            case "Available":
                if (currentState.equals("Maintenance")) {
                    return roomService.clearRoomMaintenance(room.getRoomId());
                } else if (currentState.equals("Reserved") || currentState.equals("NoShow")) {
                    return roomService.cancelBooking(room.getRoomId());
                } else if (currentState.equals("InUse")) {
                    return roomService.checkOut(room.getRoomId());
                }
                break;
            case "Maintenance":
                // Admin can set to maintenance from Available, Reserved, InUse, or NoShow
                if (currentState.equals("Available")) {
                    return roomService.setRoomMaintenance(room.getRoomId());
                } else if (currentState.equals("Reserved") || currentState.equals("InUse")) {
                    // Cancel existing booking first, then set to maintenance
                    roomService.cancelBooking(room.getRoomId());
                    return roomService.setRoomMaintenance(room.getRoomId());
                } else if (currentState.equals("NoShow")) {
                    // Process no-show to available first, then set to maintenance
                    roomService.cancelBooking(room.getRoomId());
                    return roomService.setRoomMaintenance(room.getRoomId());
                }
                break;
            case "Reserved":
                // Admin cannot manually set to Reserved - must go through booking
                return false;
            case "InUse":
                if (currentState.equals("Reserved")) {
                    return roomService.checkIn(room.getRoomId());
                }
                break;
            case "NoShow":
                if (currentState.equals("Reserved")) {
                    return roomService.processNoShow(room.getRoomId());
                }
                break;
        }
        return false;
    }
    
    private void clearLoginFields() {
        loginWindow.getTxtBoxEmail().setText("");
        loginWindow.getTxtBoxPassword().setText("");
    }
    
    private void clearRegisterFields() {
        registerWindow.getTxtBoxEmail().setText("");
        registerWindow.getTxtBoxPassword().setText("");
        registerWindow.getAccountTypeComboBox().setSelectedIndex(0);
        registerWindow.getTxtBoxOrgID().setText("");
    }
    
    private void clearAdminConsoleFields() {
        adminConsoleWindow.getRoomIdTextBox().setText("");
        adminConsoleWindow.getBuildingTextBox().setText("");
        adminConsoleWindow.getCapacityTextBox().setText("");
        adminConsoleWindow.getConditionDropdown().setSelectedIndex(0);
        adminConsoleWindow.getStatusTextBox().setText("");
    }

    private void refreshFrame() {
        frame.revalidate();
        frame.repaint();
    }
    private void clearCECFields() {
        cecWindow.getAdminIdTextBox().setText("");
        cecWindow.getAdminStatusTextBox().setText("");
    }

    // Helper method to refresh user table in CEC window from CSV
    private void refreshUserTable() {
        java.util.List<Accounts> accounts = userCSV.findAll();
        DefaultTableModel model = (DefaultTableModel) cecWindow.getAdminTable().getModel();
        
        // Clear existing rows
        model.setRowCount(0);
        
        // Add all accounts from database
        for (Accounts account : accounts) {
            model.addRow(new Object[]{
                account.getAccountId().toString(),
                account.getEmail(),
                account.getAccountType()
            });
        }
        
        // Notify table that data has changed
        model.fireTableDataChanged();
        cecWindow.getAdminTable().repaint();
        
        System.out.println("User table refreshed. Total accounts: " + accounts.size());
    }
}