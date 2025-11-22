package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ReserveRoomWindow {
	

	private JPanel roomReservationPane;
	private JTextField bookTimeTextBox;
	private JTextField roomTextBox;
	private JButton btnBackToDashboard;
	private JTable roomTable;
	private JTable timeSlotTable;
	private JTextField bookDateTextBox;
	private JTextField userIDTextBox;
	private JButton btnCalculate;
	private JTextField hourlyRateTextBox;
	private JButton btnBook;
	
	public ReserveRoomWindow() {
		buildWindow();
	}
	
	private void buildWindow() {

	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    roomReservationPane = new JPanel();
	    roomReservationPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    roomReservationPane.setLayout(null);

	    // =====================================================
	    // 2. Header & section titles
	    // =====================================================

	    // Main title
	    JLabel titleLabel = new JLabel("Room Reservation");
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
	    titleLabel.setBounds(177, 10, 311, 47);
	    roomReservationPane.add(titleLabel);

	    // Left table title (rooms)
	    JLabel roomTableLabel = new JLabel("Select an active room:");
	    roomTableLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
	    roomTableLabel.setBounds(20, 58, 196, 21);
	    roomReservationPane.add(roomTableLabel);

	    // Date entry label
	    JLabel bookDateLabel = new JLabel("Enter a Date: (DD,MM,YYYY)");
	    bookDateLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
	    bookDateLabel.setBounds(237, 58, 250, 21);
	    roomReservationPane.add(bookDateLabel);

	    // Middle table title (time slots)
	    JLabel bookTimeLabel = new JLabel("Select Time Slot(s):");
	    bookTimeLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
	    bookTimeLabel.setBounds(237, 118, 185, 21);
	    roomReservationPane.add(bookTimeLabel);

	    // Right-side section title (summary)
	    JLabel infoTitleLabel = new JLabel("Reservation Summary");
	    infoTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    infoTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
	    infoTitleLabel.setBounds(454, 89, 211, 21);
	    roomReservationPane.add(infoTitleLabel);

	    // =====================================================
	    // 3. Date input fields (top middle)
	    // =====================================================

	    JTextField dayTextBox = new JTextField();
	    dayTextBox.setText("DD");
	    dayTextBox.setColumns(10);
	    dayTextBox.setBounds(237, 89, 28, 18);
	    roomReservationPane.add(dayTextBox);

	    JTextField monthTextBox = new JTextField();
	    monthTextBox.setText("MM");
	    monthTextBox.setColumns(10);
	    monthTextBox.setBounds(270, 89, 40, 18);
	    roomReservationPane.add(monthTextBox);

	    JTextField yearTextBox = new JTextField();
	    yearTextBox.setText("YYYY");
	    yearTextBox.setColumns(10);
	    yearTextBox.setBounds(315, 89, 50, 18);
	    roomReservationPane.add(yearTextBox);

	    JButton datePickedButton = new JButton(">");
	    datePickedButton.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    datePickedButton.setBounds(375, 88, 47, 20);
	    roomReservationPane.add(datePickedButton);

	    datePickedButton.addActionListener(e -> {
	        String date = dayTextBox.getText() + "/" +
	                      monthTextBox.getText() + "/" +
	                      yearTextBox.getText();
	        System.out.println(date + " date selected");
	        bookDateTextBox.setText(date);
	    });

	    // =====================================================
	    // 4. Room selection table (left)
	    // =====================================================

	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.setBounds(20, 89, 196, 273);
	    roomReservationPane.add(scrollPane);

	    roomTable = new JTable();
	    roomTable.setModel(new DefaultTableModel(
	        new Object[][] {
	            {"R101","BRG", 10,  "Active"},
	            {"R105","LAS", 20,  "Active"},
	            {"R201","ACE", 8,   "Inactive"},
	            {"R202", "CB", 17,  "Inactive"},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	            {null, null, null, null},
	        },
	        new String[] {
		    	"Room #", "Building", "Cap.", "Status"
	        }
	    ) {
	        Class<?>[] columnTypes = new Class<?>[] {
	    		String.class, String.class, Integer.class, String.class
	        };
	        public Class<?> getColumnClass(int columnIndex) {
	            return columnTypes[columnIndex];
	        }
	        boolean[] columnEditables = new boolean[] {
	            false, false, false, false
	        };
	        public boolean isCellEditable(int row, int column) {
	            return columnEditables[column];
	        }
	    });
	    roomTable.getColumnModel().getColumn(2).setPreferredWidth(45);

	    // Only one room can be selected at a time
	    roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    scrollPane.setViewportView(roomTable);

	    // =====================================================
	    // 5. Time slot table (middle)
	    // =====================================================

	    JScrollPane scrollPane_1 = new JScrollPane();
	    scrollPane_1.setBounds(237, 149, 185, 213);
	    roomReservationPane.add(scrollPane_1);

	    timeSlotTable = new JTable();
	    timeSlotTable.setModel(new DefaultTableModel(
	        new Object[][] {
	            {"09:00", "Available"},
	            {"09:30", "Available"},
	            {"10:00", "Available"},
	            {"10:30", "Available"},
	            {"11:00", "Available"},
	            {"11:30", "Available"},
	            {"12:00", "Available"},
	            {"12:30", "Available"},
	            {"13:00", "Available"},
	            {"13:30", "Available"},
	            {"14:00", "Available"},
	            {"14:30", "Available"},
	            {"15:00", "Available"},
	            {"15:30", "Available"},
	            {"16:00", "Available"},
	        },
	        new String[] {
	            "Time", "Availability"
	        }
	    ) {
	        boolean[] columnEditables = new boolean[] {
	            false, false
	        };
	        public boolean isCellEditable(int row, int column) {
	            return columnEditables[column];
	        }
	    });

	    // Allow selecting multiple slots (for range)
	    timeSlotTable.setRowSelectionAllowed(true);
	    timeSlotTable.setColumnSelectionAllowed(false);
	    timeSlotTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	    scrollPane_1.setViewportView(timeSlotTable);

	    // =====================================================
	    // 6. Reservation summary fields (right side, top)
	// =====================================================

	    JLabel selectedRoomLabel = new JLabel("Selected Room:");
	    selectedRoomLabel.setFont(new Font("Bell MT", Font.PLAIN, 17));
	    selectedRoomLabel.setBounds(454, 120, 120, 21);
	    roomReservationPane.add(selectedRoomLabel);

	    JLabel lblBookingDate = new JLabel("Booking Date:");
	    lblBookingDate.setFont(new Font("Bell MT", Font.PLAIN, 17));
	    lblBookingDate.setBounds(461, 149, 110, 21);
	    roomReservationPane.add(lblBookingDate);

	    JLabel bookingTimeLabel = new JLabel("Booking Time:");
	    bookingTimeLabel.setFont(new Font("Bell MT", Font.PLAIN, 17));
	    bookingTimeLabel.setBounds(457, 180, 110, 21);
	    roomReservationPane.add(bookingTimeLabel);

	    JLabel UserIdLabel = new JLabel("User ID:");
	    UserIdLabel.setFont(new Font("Bell MT", Font.PLAIN, 17));
	    UserIdLabel.setBounds(499, 211, 60, 21);
	    roomReservationPane.add(UserIdLabel);

	    roomTextBox = new JTextField();
	    roomTextBox.setEditable(false);
	    roomTextBox.setBounds(569, 122, 96, 18);
	    roomReservationPane.add(roomTextBox);
	    roomTextBox.setColumns(10);

	    bookDateTextBox = new JTextField();
	    bookDateTextBox.setEditable(false);
	    bookDateTextBox.setColumns(10);
	    bookDateTextBox.setBounds(569, 151, 96, 18);
	    roomReservationPane.add(bookDateTextBox);

	    bookTimeTextBox = new JTextField();
	    bookTimeTextBox.setEditable(false);
	    bookTimeTextBox.setColumns(10);
	    bookTimeTextBox.setBounds(569, 182, 96, 18);
	    roomReservationPane.add(bookTimeTextBox);

	    userIDTextBox = new JTextField();
	    userIDTextBox.setColumns(10);
	    userIDTextBox.setBounds(569, 213, 96, 18);
	    roomReservationPane.add(userIDTextBox);

	    // =====================================================
	    // 7. Hourly rate calculation section (right side, middle)
	    // =====================================================

	    JLabel CalculateLabel = new JLabel("Calculate Hourly Rate");
	    CalculateLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
	    CalculateLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    CalculateLabel.setBounds(454, 242, 211, 21);
	    roomReservationPane.add(CalculateLabel);

	    btnCalculate = new JButton("Calculate");
	    btnCalculate.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnCalculate.setBounds(491, 273, 140, 20);
	    roomReservationPane.add(btnCalculate);

	    JLabel lblHourlyRate = new JLabel("Hourly Rate:");
	    lblHourlyRate.setFont(new Font("Bell MT", Font.PLAIN, 17));
	    lblHourlyRate.setBounds(471, 303, 88, 21);
	    roomReservationPane.add(lblHourlyRate);

	    hourlyRateTextBox = new JTextField();
	    hourlyRateTextBox.setEditable(false);
	    hourlyRateTextBox.setColumns(10);
	    hourlyRateTextBox.setBounds(569, 305, 96, 18);
	    roomReservationPane.add(hourlyRateTextBox);

	    // =====================================================
	    // 8. Final action button (Book and Pay)
	    // =====================================================

	    btnBook = new JButton("Book and Pay");
	    btnBook.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnBook.setBounds(491, 334, 140, 20);
	    roomReservationPane.add(btnBook);

	    // =====================================================
	    // 9. Selection listeners (room + time slot)
	// =====================================================

	    // --- Room table: single selection, updates roomTextBox ---
	    roomTable.getSelectionModel().addListSelectionListener(e -> {
	        if (e.getValueIsAdjusting()) {
	            return; // ignore intermediate events
	        }

	        int selectedRow = roomTable.getSelectedRow();
	        if (selectedRow >= 0) {
	            Object roomValue = roomTable.getValueAt(selectedRow, 0); // "Room #"
	            if (roomValue != null) {
	                roomTextBox.setText(String.valueOf(roomValue));
	            } else {
	                roomTextBox.setText("");
	            }
	            System.out.println("Selected room row: " + selectedRow +
	                               ", value: " + roomTextBox.getText());
	        } else {
	            roomTextBox.setText("");
	        }
	    });

	    // --- Time slot table: multi selection, updates bookTimeTextBox ---
	    timeSlotTable.getSelectionModel().addListSelectionListener(e -> {
	        if (e.getValueIsAdjusting()) {
	            return; // ignore intermediate events
	        }

	        int[] selectedRows = timeSlotTable.getSelectedRows();

	        if (selectedRows.length == 0) {
	            bookTimeTextBox.setText("");
	            return;
	        }

	        // Find first and last selected row
	        int minRow = selectedRows[0];
	        int maxRow = selectedRows[0];
	        for (int r : selectedRows) {
	            if (r < minRow) minRow = r;
	            if (r > maxRow) maxRow = r;
	        }

	        Object startTime = timeSlotTable.getValueAt(minRow, 0); // column 0 = "Time"
	        Object endTime   = timeSlotTable.getValueAt(maxRow, 0);

	        if (startTime == null && endTime == null) {
	            bookTimeTextBox.setText("");
	        } else if (selectedRows.length == 1 || 
	                   (startTime != null && startTime.equals(endTime))) {
	            // Single time slot
	            bookTimeTextBox.setText(String.valueOf(startTime));
	        } else {
	            // Range
	            bookTimeTextBox.setText(startTime + " - " + endTime);
	        }

	        System.out.println("Selected time rows: " + selectedRows.length +
	                           ", range: " + bookTimeTextBox.getText());
	    });
	    
	    // Back to dashboard
	    btnBackToDashboard = new JButton("Back");
	    btnBackToDashboard.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnBackToDashboard.setBounds(20, 10, 80, 23);
	    btnBackToDashboard.addActionListener(e -> {
	        System.out.println("Back to dashboard clicked");
	        // e.g. buildDashboardPane();
	    });
	    roomReservationPane.add(btnBackToDashboard);
	}

	public JPanel getPane() {
		return roomReservationPane;
	}

	public JTextField getBookTimeTextBox() {
		return bookTimeTextBox;
	}

	public JTextField getRoomTextBox() {
		return roomTextBox;
	}

	public JButton getBtnBackToDashboard() {
		return btnBackToDashboard;
	}

	public JTable getRoomTable() {
		return roomTable;
	}

	public JTable getTimeSlotTable() {
		return timeSlotTable;
	}

	public JTextField getBookDateTextBox() {
		return bookDateTextBox;
	}

	public JTextField getUserIDTextBox() {
		return userIDTextBox;
	}

	public JButton getBtnCalculate() {
		return btnCalculate;
	}

	public JTextField getHourlyRateTextBox() {
		return hourlyRateTextBox;
	}

	public JButton getBtnBook() {
		return btnBook;
	}
}