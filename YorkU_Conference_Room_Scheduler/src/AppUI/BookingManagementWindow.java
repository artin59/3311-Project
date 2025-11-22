package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BookingManagementWindow {
	private JPanel bookingManagementPane;
	private JTable bookingTable;

	private JTextField selectedBookingIdTextBox;
	private JTextField selectedRoomTextBox;
	private JTextField selectedDateTextBox;
	private JTextField selectedTimeTextBox;

	private JTextField newDateTextBox;
	private JTextField newStartTimeTextBox;
	private JTextField extendByHoursTextBox;

	private JButton btnApplyEdit;
	private JButton btnCancelBooking;
	private JButton btnExtendBooking;
	private JButton btnRefresh;
	private JButton btnBackToDashboard;

	public BookingManagementWindow() {
		buildWindow();
	}
	
	public void buildWindow() {
		
	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
		
	    bookingManagementPane = new JPanel();
	    bookingManagementPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    bookingManagementPane.setLayout(null);

	    // =====================================================
	    // 2. Header & section titles
	    // =====================================================

	    JLabel titleLabel = new JLabel("Booking Management");
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
	    titleLabel.setBounds(180, 10, 400, 40);
	    bookingManagementPane.add(titleLabel);

	    JLabel myBookingsTitleLabel = new JLabel("My Bookings");
	    myBookingsTitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    myBookingsTitleLabel.setBounds(20, 60, 200, 22);
	    bookingManagementPane.add(myBookingsTitleLabel);

	    JLabel bookingDetailsTitleLabel = new JLabel("Booking Details");
	    bookingDetailsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    bookingDetailsTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
	    bookingDetailsTitleLabel.setBounds(430, 60, 230, 22);
	    bookingManagementPane.add(bookingDetailsTitleLabel);

	    // =====================================================
	    // 3. Bookings table (left)
	    // =====================================================

	    JScrollPane bookingScrollPane = new JScrollPane();
	    bookingScrollPane.setBounds(20, 90, 380, 280);
	    bookingManagementPane.add(bookingScrollPane);

	    bookingTable = new JTable();
	    bookingTable.setModel(new DefaultTableModel(
	        new Object[][] {
	            // sample rows – replace with real data later
	            {"B001", "R101", "2025-03-10", "09:00", "10:00", "Reserved"},
	            {"B002", "R202", "2025-03-11", "13:00", "14:00", "Cancelled"},
	            {"B003", "R305", "2025-03-12", "10:00", "12:00", "In Use"},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	            {null, null, null, null, null, null},
	        },
	        new String[] {
	            "Booking ID", "Room #", "Date", "Start", "End", "Status"
	        }
	    ) {
	        boolean[] columnEditables = new boolean[] {
	            false, false, false, false, false, false
	        };
	        public boolean isCellEditable(int row, int column) {
	            return columnEditables[column];
	        }
	    });

	    // selection listener (mouse + keyboard)
	    bookingTable.getSelectionModel().addListSelectionListener(e -> {
	        if (!e.getValueIsAdjusting()) {
	            int row = bookingTable.getSelectedRow();
	            if (row != -1) {
	                DefaultTableModel model = (DefaultTableModel) bookingTable.getModel();
	                String bookingId = String.valueOf(model.getValueAt(row, 0));
	                String room = String.valueOf(model.getValueAt(row, 1));
	                String date = String.valueOf(model.getValueAt(row, 2));
	                String start = String.valueOf(model.getValueAt(row, 3));
	                String end   = String.valueOf(model.getValueAt(row, 4));

	                selectedBookingIdTextBox.setText(bookingId);
	                selectedRoomTextBox.setText(room);
	                selectedDateTextBox.setText(date);
	                selectedTimeTextBox.setText(start + " - " + end);

	                // pre-fill edit fields with current values
	                newDateTextBox.setText(date);
	                newStartTimeTextBox.setText(start);
	            }
	        }
	    });

	    bookingScrollPane.setViewportView(bookingTable);

	    // =====================================================
	    // 4. Selected booking summary (right, top)
	    // =====================================================

	    JLabel selectedBookingIdLabel = new JLabel("Booking ID:");
	    selectedBookingIdLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    selectedBookingIdLabel.setBounds(430, 95, 90, 20);
	    bookingManagementPane.add(selectedBookingIdLabel);

	    selectedBookingIdTextBox = new JTextField();
	    selectedBookingIdTextBox.setEditable(false);
	    selectedBookingIdTextBox.setBounds(530, 97, 130, 18);
	    bookingManagementPane.add(selectedBookingIdTextBox);

	    JLabel selectedRoomLabel = new JLabel("Room #:");
	    selectedRoomLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    selectedRoomLabel.setBounds(430, 120, 90, 20);
	    bookingManagementPane.add(selectedRoomLabel);

	    selectedRoomTextBox = new JTextField();
	    selectedRoomTextBox.setEditable(false);
	    selectedRoomTextBox.setBounds(530, 122, 130, 18);
	    bookingManagementPane.add(selectedRoomTextBox);

	    JLabel selectedDateLabel = new JLabel("Date:");
	    selectedDateLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    selectedDateLabel.setBounds(430, 145, 90, 20);
	    bookingManagementPane.add(selectedDateLabel);

	    selectedDateTextBox = new JTextField();
	    selectedDateTextBox.setEditable(false);
	    selectedDateTextBox.setBounds(530, 147, 130, 18);
	    bookingManagementPane.add(selectedDateTextBox);

	    JLabel selectedTimeLabel = new JLabel("Time:");
	    selectedTimeLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    selectedTimeLabel.setBounds(430, 170, 90, 20);
	    bookingManagementPane.add(selectedTimeLabel);

	    selectedTimeTextBox = new JTextField();
	    selectedTimeTextBox.setEditable(false);
	    selectedTimeTextBox.setBounds(530, 172, 130, 18);
	    bookingManagementPane.add(selectedTimeTextBox);

	    // =====================================================
	    // 5. Edit / extend section (right, middle)
	    // =====================================================

	    JLabel editSectionLabel = new JLabel("Modify / Extend Booking");
	    editSectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    editSectionLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
	    editSectionLabel.setBounds(430, 200, 230, 22);
	    bookingManagementPane.add(editSectionLabel);

	    JLabel newDateLabel = new JLabel("New Date:");
	    newDateLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    newDateLabel.setBounds(430, 230, 90, 20);
	    bookingManagementPane.add(newDateLabel);

	    newDateTextBox = new JTextField();
	    newDateTextBox.setBounds(530, 232, 130, 18);
	    bookingManagementPane.add(newDateTextBox);

	    JLabel newStartTimeLabel = new JLabel("New Start Time:");
	    newStartTimeLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    newStartTimeLabel.setBounds(430, 255, 110, 20);
	    bookingManagementPane.add(newStartTimeLabel);

	    newStartTimeTextBox = new JTextField();
	    newStartTimeTextBox.setBounds(550, 257, 110, 18);
	    bookingManagementPane.add(newStartTimeTextBox);

	    JLabel extendByLabel = new JLabel("Extend (hrs):");
	    extendByLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    extendByLabel.setBounds(430, 280, 100, 20);
	    bookingManagementPane.add(extendByLabel);

	    extendByHoursTextBox = new JTextField();
	    extendByHoursTextBox.setBounds(530, 282, 50, 18);
	    bookingManagementPane.add(extendByHoursTextBox);

	    // =====================================================
	    // 6. Action buttons (right, bottom)
	    // =====================================================

	    btnApplyEdit = new JButton("Apply Edit");
	    btnApplyEdit.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnApplyEdit.setBounds(430, 310, 120, 23);
	    bookingManagementPane.add(btnApplyEdit);

	    btnExtendBooking = new JButton("Extend");
	    btnExtendBooking.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnExtendBooking.setBounds(560, 310, 100, 23);
	    bookingManagementPane.add(btnExtendBooking);

	    btnCancelBooking = new JButton("Cancel");
	    btnCancelBooking.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnCancelBooking.setBounds(430, 340, 120, 23);
	    bookingManagementPane.add(btnCancelBooking);

	    btnRefresh = new JButton("Refresh");
	    btnRefresh.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnRefresh.setBounds(560, 340, 100, 23);
	    bookingManagementPane.add(btnRefresh);

	    // =====================================================
	    // 7. Beck to Dashboard
	    // =====================================================
	    
	    btnBackToDashboard = new JButton("Back");
	    btnBackToDashboard.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnBackToDashboard.setBounds(20, 10, 80, 23);
	    bookingManagementPane.add(btnBackToDashboard);
	}

	public JPanel getPane() {
		return bookingManagementPane;
	}

	public JTable getBookingTable() {
		return bookingTable;
	}

	public JTextField getSelectedBookingIdTextBox() {
		return selectedBookingIdTextBox;
	}

	public JTextField getSelectedRoomTextBox() {
		return selectedRoomTextBox;
	}

	public JTextField getSelectedDateTextBox() {
		return selectedDateTextBox;
	}

	public JTextField getSelectedTimeTextBox() {
		return selectedTimeTextBox;
	}

	public JTextField getNewDateTextBox() {
		return newDateTextBox;
	}

	public JTextField getNewStartTimeTextBox() {
		return newStartTimeTextBox;
	}

	public JTextField getExtendByHoursTextBox() {
		return extendByHoursTextBox;
	}

	public JButton getBtnApplyEdit() {
		return btnApplyEdit;
	}

	public JButton getBtnCancelBooking() {
		return btnCancelBooking;
	}

	public JButton getBtnExtendBooking() {
		return btnExtendBooking;
	}

	public JButton getBtnRefresh() {
		return btnRefresh;
	}

	public JButton getBtnBackToDashboard() {
		return btnBackToDashboard;
	}
}