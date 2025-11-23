package AppUI;

import java.awt.EventQueue;
import java.time.LocalTime;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import Backend.Accounts;
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
    
    // Currently logged in user
    private Accounts currentUser;
    
    // Currently selected room in admin console
    private Room selectedRoom = null;

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
            frame.setContentPane(introWindow.getPane());
            refreshFrame();
        });

        checkInWindow.getBtnSimulateScan().addActionListener(e -> {
            String bookingId = checkInWindow.getBookingIdTextBox().getText();
            String email = checkInWindow.getEmailTextBox().getText();
            String occupants = checkInWindow.getOccupantsTextBox().getText();

            System.out.println("Simulate Scan");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Email: " + email);
            System.out.println("Occupants: " + occupants);

            checkInWindow.getRoomNumberTextBox().setText("Room 101");
            checkInWindow.getBookingTimeTextBox().setText("09:00 - 10:00");
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
            
            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getEmailRequirements(), 
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
            
            if (!ValidationUtil.isValidOrgId(orgId)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getOrgIdRequirements(), 
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
            frame.setContentPane(reserveRoomWindow.getPane());
            refreshFrame();
        });
        
        dashboardWindow.getBtnDashMyBookings().addActionListener(e -> {
            System.out.println("Dashboard: Manage Bookings clicked");
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
        
        reserveRoomWindow.getBtnCalculate().addActionListener(e -> {
            LocalTime time = LocalTime.now();
            Double rate = 7711.0 * time.getSecond();
            System.out.println("Reserve Room: Calculate clicked, Rate = " + rate);
            reserveRoomWindow.getHourlyRateTextBox().setText(rate.toString());
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
            System.out.println("Apply Edit clicked for booking " + 
                bookingManagementWindow.getSelectedBookingIdTextBox().getText());
        });
        
        bookingManagementWindow.getBtnExtendBooking().addActionListener(e -> {
            System.out.println("Extend clicked for booking " + 
                bookingManagementWindow.getSelectedBookingIdTextBox().getText()
                + ", extra hours: " + bookingManagementWindow.getExtendByHoursTextBox().getText());
        });
        
        bookingManagementWindow.getBtnCancelBooking().addActionListener(e -> {
            System.out.println("Cancel clicked for booking " + 
                bookingManagementWindow.getSelectedBookingIdTextBox().getText());
        });
        
        bookingManagementWindow.getBtnRefresh().addActionListener(e -> {
            System.out.println("Refresh clicked");
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
            } else {
                JOptionPane.showMessageDialog(frame,
                    "Cannot change condition from " + currentCondition + " to " + selectedCondition + ".",
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
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        cecWindow.getBtnGrantAdmin().addActionListener(e -> {
            System.out.println("CEC: Grant Admin clicked for " + 
                cecWindow.getAdminIdTextBox().getText());
        });
        
        cecWindow.getBtnDisableAdmin().addActionListener(e -> {
            System.out.println("CEC: Disable Admin clicked for " + 
                cecWindow.getAdminIdTextBox().getText());
        });
        
        cecWindow.getBtnRefreshAdmins().addActionListener(e -> {
            System.out.println("CEC: Refresh clicked (admins + logs)");
        });
    }
    
    private void wirePaymentWindowHandlers(PaymentWindow paymentWindow) {
        paymentWindow.getBtnConfirmPayment().addActionListener(e -> {
            String method = (String) paymentWindow.getPaymentMethodComboBox().getSelectedItem();
            System.out.println("Payment confirmed for " + paymentWindow.getAmountTextBox().getText()
                               + " via " + method);
            JOptionPane.showMessageDialog(frame,
                    "Payment processed for " + paymentWindow.getAmountTextBox().getText(),
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        paymentWindow.getBtnCancelPayment().addActionListener(e -> {
            System.out.println("Payment cancelled back to Reserve Room");
            frame.setContentPane(reserveRoomWindow.getPane());
            refreshFrame();
        });
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
                if (currentState.equals("Available")) {
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
}