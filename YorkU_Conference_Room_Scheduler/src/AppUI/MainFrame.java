package AppUI;

import java.awt.EventQueue;
import java.time.LocalTime;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
	
	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
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
	        String email     = checkInWindow.getEmailTextBox().getText();
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
	        frame.setContentPane(introWindow.getPane());
	        refreshFrame();
	    });

	    loginWindow.getBtnLogin().addActionListener(e -> {
	        System.out.println("Login Button Clicked");

	        if (String.valueOf(loginWindow.getTxtBoxPassword().getPassword()).trim().equals("Hello")) {
	            JOptionPane.showMessageDialog(null, "Login Successful");
		        frame.setContentPane(dashboardWindow.getPane());
		        refreshFrame();
	        } else {
	            JOptionPane.showMessageDialog(null, "Login Failed");
	        }

	        loginWindow.getTxtBoxPassword().setText("");
	    });

	    loginWindow.getBtnRegister().addActionListener(e -> {
	        System.out.println("Registering new Account...");
	        frame.setContentPane(registerWindow.getPane());
	        refreshFrame();
	    });
	}
	
	
	private void wireRegisterHandlers() {
	    registerWindow.getBtnBackToLogin().addActionListener(e -> {
	        System.out.println("Back to Login clicked");
	        frame.setContentPane(loginWindow.getPane());
	        refreshFrame();
	    });

	    registerWindow.getBtnRegister().addActionListener(e -> {
	        System.out.println("Account Registered");
	        if (String.valueOf(registerWindow.getTxtBoxPassword().getPassword()).trim().equals("Hello")) {
	            JOptionPane.showMessageDialog(null, "Register Successful");
	        } else {
	            JOptionPane.showMessageDialog(null, "Invalid Info: Register Failed");
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
	        PaymentWindow paymentWindow = new PaymentWindow(Double.parseDouble(reserveRoomWindow.getHourlyRateTextBox().getText()));
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
	        System.out.println("Apply Edit clicked for booking " + bookingManagementWindow.getSelectedBookingIdTextBox().getText());
	        // TODO: call EditBookingCommand via your controller
	    });
	    
		bookingManagementWindow.getBtnExtendBooking().addActionListener(e -> {
	        System.out.println("Extend clicked for booking " + bookingManagementWindow.getSelectedBookingIdTextBox().getText()
	                + ", extra hours: " + bookingManagementWindow.getExtendByHoursTextBox().getText());
	        // TODO: call ExtendBookingCommand
	    });
	    
		bookingManagementWindow.getBtnCancelBooking().addActionListener(e -> {
	        System.out.println("Cancel clicked for booking " + bookingManagementWindow.getSelectedBookingIdTextBox().getText());
	        // TODO: call CancelBookingCommand
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
	        System.out.println("Admin Console: Save Changes clicked for " + adminConsoleWindow.getRoomIdTextBox().getText());
	        // Backend will sync dropdown + fields to table/CSV
	    });
	    
		adminConsoleWindow.getBtnEnableRoom().addActionListener(e -> {
	        System.out.println("Admin Console: Enable Room clicked for " + adminConsoleWindow.getRoomIdTextBox().getText());
	    });
	    
		adminConsoleWindow.getBtnDisableRoom().addActionListener(e -> {
	        System.out.println("Admin Console: Disable Room clicked for " + adminConsoleWindow.getRoomIdTextBox().getText());
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
	        System.out.println("CEC: Grant Admin clicked for " + cecWindow.getAdminIdTextBox().getText());
	        // UI only – backend will handle actual role change
	    });
	    
	    cecWindow.getBtnDisableAdmin().addActionListener(e -> {
	        System.out.println("CEC: Disable Admin clicked for " + cecWindow.getAdminIdTextBox().getText());
	        // UI only – backend will flip User <-> Admin
	    });
	    
	    cecWindow.getBtnRefreshAdmins().addActionListener(e -> {
	        System.out.println("CEC: Refresh clicked (admins + logs)");
	        // UI only – backend will reload both tables
	    });
	}
	
	private void wirePaymentWindowHandlers(PaymentWindow paymentWindow) {
        paymentWindow.getBtnConfirmPayment().addActionListener(e -> {
	        String method = (String) paymentWindow.getPaymentMethodComboBox().getSelectedItem();
	        System.out.println("Payment confirmed for " + paymentWindow.getAmountTextBox().getText()
	                           + " via " + method);
	        JOptionPane.showMessageDialog(null,
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

	private void refreshFrame() {
		frame.revalidate();
		frame.repaint();
	}

}
