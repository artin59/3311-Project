package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CheckInWindow {
	
	private JPanel checkInPane;

	private JLabel checkInTitleLabel;

	private JLabel bookingIdLabel;
	private JLabel emailLabel;
	private JLabel occupantsLabel;

	private JTextField bookingIdTextBox;
	private JTextField emailTextBox;
	private JTextField occupantsTextBox;

	private JButton btnSimulateScan;

	private JLabel roomNumberLabel;
	private JLabel bookingTimeLabel;

	private JTextField roomNumberTextBox;
	private JTextField bookingTimeTextBox;

	private JButton backToIntro;
	
	
	public CheckInWindow() {
		buildWindow();
	}
	
	public void buildWindow() {

	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    checkInPane = new JPanel();
	    checkInPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    checkInPane.setLayout(null);

	    // =====================================================
	    // 2. Title
	    // =====================================================

	    checkInTitleLabel = new JLabel("Room Check-In");
	    checkInTitleLabel.setFont(new Font("Arial", Font.BOLD, 28));
	    checkInTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    checkInTitleLabel.setBounds(120, 20, 450, 40);
	    checkInPane.add(checkInTitleLabel);

	    // =====================================================
	    // 3. Input section: Booking ID, Email, # Occupants
	    // =====================================================

	    bookingIdLabel = new JLabel("Booking ID:");
	    bookingIdLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    bookingIdLabel.setBounds(140, 90, 120, 25);
	    checkInPane.add(bookingIdLabel);

	    bookingIdTextBox = new JTextField();
	    bookingIdTextBox.setBounds(270, 92, 250, 22);
	    checkInPane.add(bookingIdTextBox);

	    emailLabel = new JLabel("Email:");
	    emailLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    emailLabel.setBounds(140, 125, 120, 25);
	    checkInPane.add(emailLabel);

	    emailTextBox = new JTextField();
	    emailTextBox.setBounds(270, 127, 250, 22);
	    checkInPane.add(emailTextBox);

	    occupantsLabel = new JLabel("# of Attendees:");
	    occupantsLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    occupantsLabel.setBounds(140, 160, 130, 25);
	    checkInPane.add(occupantsLabel);

	    occupantsTextBox = new JTextField();
	    occupantsTextBox.setBounds(270, 162, 80, 22);
	    checkInPane.add(occupantsTextBox);

	    // =====================================================
	    // 4. Simulate Scan button
	    // =====================================================

	    btnSimulateScan = new JButton("Simulate Scan");
	    btnSimulateScan.setFont(new Font("Baskerville Old Face", Font.PLAIN, 22));
	    btnSimulateScan.setBounds(220, 205, 260, 50);
	    checkInPane.add(btnSimulateScan);

	    // =====================================================
	    // 5. Output section: Room # and Booking Time
	    // =====================================================

	    roomNumberLabel = new JLabel("Room #:");
	    roomNumberLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    roomNumberLabel.setBounds(140, 280, 120, 25);
	    checkInPane.add(roomNumberLabel);

	    roomNumberTextBox = new JTextField();
	    roomNumberTextBox.setEditable(false);
	    roomNumberTextBox.setBounds(270, 282, 250, 22);
	    checkInPane.add(roomNumberTextBox);

	    bookingTimeLabel = new JLabel("Booking Time:");
	    bookingTimeLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    bookingTimeLabel.setBounds(140, 315, 130, 25);
	    checkInPane.add(bookingTimeLabel);

	    bookingTimeTextBox = new JTextField();
	    bookingTimeTextBox.setEditable(false);
	    bookingTimeTextBox.setBounds(270, 317, 250, 22);
	    checkInPane.add(bookingTimeTextBox);
	    
	    // Back to Intro
	    backToIntro = new JButton("Back");
	    backToIntro.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    backToIntro.setBounds(20, 10, 80, 23);
	    checkInPane.add(backToIntro);
	}

	public JPanel getPane() {
		return checkInPane;
	}

	public JTextField getBookingIdTextBox() {
		return bookingIdTextBox;
	}

	public JTextField getEmailTextBox() {
		return emailTextBox;
	}

	public JTextField getOccupantsTextBox() {
		return occupantsTextBox;
	}

	public JButton getBtnSimulateScan() {
		return btnSimulateScan;
	}

	public JTextField getRoomNumberTextBox() {
		return roomNumberTextBox;
	}

	public JTextField getBookingTimeTextBox() {
		return bookingTimeTextBox;
	}

	public JButton getBtnBackToIntro() {
		return backToIntro;
	}
}