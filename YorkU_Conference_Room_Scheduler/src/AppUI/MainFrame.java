package AppUI;

import java.awt.EventQueue;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Backend.Accounts;
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
    
    // Currently logged in user
    private Accounts currentUser = null;

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
                clearLoginFields();
                // Functionality to hide buttons depending on account type
                String accountType = account.getAccountType();
		        if(accountType.equals("Admin")) {
		        	dashboardWindow.getBtnDashCECPanel().setVisible(false);
		        	dashboardWindow.getBtnDashAdminConsole().setLocation(dashboardWindow.getBtnDashCECPanel().getLocation());
		        }
		        else if (accountType.equals("Chief Event Coordinator")) {
		        	dashboardWindow.getBtnDashAdminConsole().setVisible(false);
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
            
            // Validate account type selection
            if (accountType.equals("(Select Account Type)")) {
                JOptionPane.showMessageDialog(frame, 
                    "Please select an account type.", 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate email
            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getEmailRequirements(), 
                    "Invalid Email", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if email already exists
            if (userCSV.emailExists(email)) {
                JOptionPane.showMessageDialog(frame, 
                    "An account with this email already exists. Please login instead.", 
                    "Email Already Registered", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate password
            if (!ValidationUtil.isValidPassword(password)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getPasswordRequirements(), 
                    "Invalid Password", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate org ID
            if (!ValidationUtil.isValidOrgId(orgId)) {
                JOptionPane.showMessageDialog(frame, 
                    ValidationUtil.getOrgIdRequirements(), 
                    "Invalid Organization ID", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create user using factory
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
            frame.setContentPane(dashboardWindow.getPane());
            refreshFrame();
        });
        
        adminConsoleWindow.getBtnAddRoom().addActionListener(e -> {
            System.out.println("Admin Console: Add Room clicked");
        });
        
        adminConsoleWindow.getBtnSaveChanges().addActionListener(e -> {
            System.out.println("Admin Console: Save Changes clicked for " + 
                adminConsoleWindow.getRoomIdTextBox().getText());
        });
        
        adminConsoleWindow.getBtnEnableRoom().addActionListener(e -> {
            System.out.println("Admin Console: Enable Room clicked for " + 
                adminConsoleWindow.getRoomIdTextBox().getText());
        });
        
        adminConsoleWindow.getBtnDisableRoom().addActionListener(e -> {
            System.out.println("Admin Console: Disable Room clicked for " + 
                adminConsoleWindow.getRoomIdTextBox().getText());
        });
        
        adminConsoleWindow.getBtnRefreshRooms().addActionListener(e -> {
            System.out.println("Admin Console: Refresh rooms clicked");
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

    private void refreshFrame() {
        frame.revalidate();
        frame.repaint();
    }
}
