package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DashboardWindow {
	private JPanel dashboardPane;

	private JLabel dashboardTitleLabel;
	private JLabel dashboardWelcomeLabel;

	private JButton btnDashReserve;
	private JButton btnDashMyBookings;
	private JButton btnDashAdminConsole;
	private JButton btnDashCECPanel;
	private JButton btnDashLogout;

	public DashboardWindow() {
		buildWindow();
	}
	
	public void buildWindow() {
	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    dashboardPane = new JPanel();
	    dashboardPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    dashboardPane.setLayout(null);

	    // =====================================================
	    // 2. Title & welcome text
	    // =====================================================

	    dashboardTitleLabel = new JLabel("Room Scheduler Dashboard");
	    dashboardTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
	    dashboardTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    dashboardTitleLabel.setBounds(139, 78, 400, 40);
	    dashboardPane.add(dashboardTitleLabel);

	    dashboardWelcomeLabel = new JLabel("Select an action to continue");
	    dashboardWelcomeLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    dashboardWelcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    dashboardWelcomeLabel.setBounds(159, 123, 360, 30);
	    dashboardPane.add(dashboardWelcomeLabel);

	    // =====================================================
	    // 3. Main actions (center buttons)
	    // =====================================================

	    // Reserve a Room
	    btnDashReserve = new JButton("Book Room");
	    btnDashReserve.setFont(new Font("Baskerville Old Face", Font.PLAIN, 22));
	    btnDashReserve.setBounds(78, 163, 225, 108);
	    dashboardPane.add(btnDashReserve);

	    // My Bookings
	    btnDashMyBookings = new JButton("Manage Bookings");
	    btnDashMyBookings.setFont(new Font("Baskerville Old Face", Font.PLAIN, 22));
	    btnDashMyBookings.setBounds(382, 163, 225, 108);
	    dashboardPane.add(btnDashMyBookings);

	    // =====================================================
	    // 4. Admin / CEC actions (right side)
	    // =====================================================

	    // Admin Console (room management)
	    btnDashAdminConsole = new JButton("Admin Console");
	    btnDashAdminConsole.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
	    btnDashAdminConsole.setBounds(529, 310, 150, 23);
	    dashboardPane.add(btnDashAdminConsole);

	    // CEC Panel
	    btnDashCECPanel = new JButton("CEC Panel");
	    btnDashCECPanel.setFont(new Font("Berlin Sans FB", Font.PLAIN, 15));
	    btnDashCECPanel.setBounds(529, 341, 150, 23);
	    dashboardPane.add(btnDashCECPanel);

	    // =====================================================
	    // 5. Logout
	    // =====================================================
	    btnDashLogout = new JButton("Logout");
	    btnDashLogout.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnDashLogout.setBounds(20, 10, 80, 23);
	    dashboardPane.add(btnDashLogout);
	}


	public JPanel getDashboardPane() {
		return dashboardPane;
	}

	public JButton getBtnDashReserve() {
		return btnDashReserve;
	}

	public JButton getBtnDashMyBookings() {
		return btnDashMyBookings;
	}

	public JButton getBtnDashAdminConsole() {
		return btnDashAdminConsole;
	}

	public JButton getBtnDashCECPanel() {
		return btnDashCECPanel;
	}

	public JButton getBtnDashLogout() {
		return btnDashLogout;
	}

	public JPanel getPane() {
		return dashboardPane;
	}

}
