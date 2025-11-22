package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminConsoleWindow {
	
	
	// Admin Console (Room Management) pane
	private JPanel adminConsolePane;

	private JTable roomTable;

	private JTextField roomIdTextBox;
	private JTextField buildingTextBox;
	private JTextField capacityTextBox;
	private JComboBox<String> conditionDropdown;
	private JTextField statusTextBox;

	private JButton btnAddRoom;
	private JButton btnSaveChanges;
	private JButton btnEnableRoom;
	private JButton btnDisableRoom;
	private JButton btnRefreshRooms;
	private JButton btnBackToDashboard;
	
	public AdminConsoleWindow() {
		buildWindow();
	}
	
	@SuppressWarnings("removal")
	public void buildWindow() {

	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    adminConsolePane = new JPanel();
	    adminConsolePane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    adminConsolePane.setLayout(null);

	    // =====================================================
	    // 2. Header & section titles
	    // =====================================================

	    JLabel adminConsoleTitleLabel = new JLabel("Admin Console - Room Management");
	    adminConsoleTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    adminConsoleTitleLabel.setBounds(149, 21, 513, 35);
	    adminConsolePane.add(adminConsoleTitleLabel);

	    JLabel roomListTitleLabel = new JLabel("Rooms");
	    roomListTitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    roomListTitleLabel.setBounds(20, 55, 200, 22);
	    adminConsolePane.add(roomListTitleLabel);

	    JLabel roomDetailsTitleLabel = new JLabel("Room Details");
	    roomDetailsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    roomDetailsTitleLabel.setFont(new Font("Tahoma", Font.BOLD, 17));
	    roomDetailsTitleLabel.setBounds(372, 65, 290, 22);
	    adminConsolePane.add(roomDetailsTitleLabel);

	    // =====================================================
	    // 3. Room table (left side)
	    // =====================================================

	    JScrollPane roomScrollPane = new JScrollPane();
	    roomScrollPane.setBounds(20, 85, 333, 280);
	    adminConsolePane.add(roomScrollPane);

	    roomTable = new JTable();
	    roomTable.setModel(new DefaultTableModel(
	    	new Object[][] {
	    		{"R101", "Building A", new Integer(20), "Available", "Enabled"},
	    		{"R202", "Building B", new Integer(40), "Closed for Maintenance", "Enabled"},
	    		{"R303", "Building C", new Integer(10), "Reserved", "Disabled"},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    		{null, null, null, null, null},
	    	},
	    	new String[] {
	    		"Room ID", "Building", "Cap.", "Condition", "Status"
	    	}
	    ) {
	    	@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
	    		String.class, String.class, Integer.class, String.class, String.class
	    	};
	    	@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
	    		return columnTypes[columnIndex];
	    	}
	    });
	    roomTable.getColumnModel().getColumn(0).setPreferredWidth(59);
	    roomTable.getColumnModel().getColumn(1).setPreferredWidth(70);
	    roomTable.getColumnModel().getColumn(2).setPreferredWidth(35);
	    roomTable.getColumnModel().getColumn(3).setPreferredWidth(119);

	    roomScrollPane.setViewportView(roomTable);

	    // Selection -> fill detail fields
	    roomTable.getSelectionModel().addListSelectionListener(e -> {
	        if (!e.getValueIsAdjusting()) {
	            int row = roomTable.getSelectedRow();
	            if (row != -1) {
	                DefaultTableModel model = (DefaultTableModel) roomTable.getModel();
	                roomIdTextBox.setText(String.valueOf(model.getValueAt(row, 0)));
	                buildingTextBox.setText(String.valueOf(model.getValueAt(row, 1)));
	                capacityTextBox.setText(String.valueOf(model.getValueAt(row, 2)));
	                String condition = String.valueOf(model.getValueAt(row, 3));
	                conditionDropdown.setSelectedItem(condition);
	                statusTextBox.setText(String.valueOf(model.getValueAt(row, 4)));
	            }
	        }
	    });

	    // =====================================================
	    // 4. Room details (right side)
	    // =====================================================

	    JLabel roomIdLabel = new JLabel("Room ID:");
	    roomIdLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    roomIdLabel.setBounds(372, 97, 90, 20);
	    adminConsolePane.add(roomIdLabel);

	    roomIdTextBox = new JTextField();
	    roomIdTextBox.setBounds(472, 99, 190, 18);
	    adminConsolePane.add(roomIdTextBox);

	    JLabel buildingLabel = new JLabel("Building:");
	    buildingLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    buildingLabel.setBounds(372, 127, 90, 20);
	    adminConsolePane.add(buildingLabel);

	    buildingTextBox = new JTextField();
	    buildingTextBox.setBounds(472, 129, 190, 18);
	    adminConsolePane.add(buildingTextBox);

	    JLabel capacityLabel = new JLabel("Capacity:");
	    capacityLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    capacityLabel.setBounds(372, 157, 90, 20);
	    adminConsolePane.add(capacityLabel);

	    capacityTextBox = new JTextField();
	    capacityTextBox.setBounds(472, 159, 80, 18);
	    adminConsolePane.add(capacityTextBox);

	    // Condition dropdown (replaces Equipment)
	    JLabel conditionLabel = new JLabel("Condition:");
	    conditionLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    conditionLabel.setBounds(372, 187, 90, 20);
	    adminConsolePane.add(conditionLabel);

	    conditionDropdown = new JComboBox<>();
	    conditionDropdown.setBounds(472, 189, 190, 22);
	    conditionDropdown.addItem("Available");
	    conditionDropdown.addItem("Reserved");
	    conditionDropdown.addItem("In Use");
	    conditionDropdown.addItem("Closed for Maintenance");
	    conditionDropdown.addItem("No-Show");
	    adminConsolePane.add(conditionDropdown);

	    JLabel statusLabel = new JLabel("Status:");
	    statusLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    statusLabel.setBounds(372, 217, 90, 20);
	    adminConsolePane.add(statusLabel);

	    statusTextBox = new JTextField();
	    statusTextBox.setEditable(false); // Enabled / Disabled
	    statusTextBox.setBounds(472, 219, 190, 18);
	    adminConsolePane.add(statusTextBox);

	    // =====================================================
	    // 5. Action buttons (right-bottom)
	    // =====================================================

	    btnAddRoom = new JButton("Add Room");
	    btnAddRoom.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnAddRoom.setBounds(372, 257, 135, 23);
	    adminConsolePane.add(btnAddRoom);

	    btnSaveChanges = new JButton("Save Changes");
	    btnSaveChanges.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnSaveChanges.setBounds(527, 257, 135, 23);
	    adminConsolePane.add(btnSaveChanges);

	    btnEnableRoom = new JButton("Enable Room");
	    btnEnableRoom.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnEnableRoom.setBounds(372, 287, 135, 23);
	    adminConsolePane.add(btnEnableRoom);

	    btnDisableRoom = new JButton("Disable Room");
	    btnDisableRoom.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnDisableRoom.setBounds(527, 287, 135, 23);
	    adminConsolePane.add(btnDisableRoom);

	    btnRefreshRooms = new JButton("Refresh");
	    btnRefreshRooms.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnRefreshRooms.setBounds(372, 317, 135, 23);
	    adminConsolePane.add(btnRefreshRooms);

	    // Back to dashboard
	    btnBackToDashboard = new JButton("Back");
	    btnBackToDashboard.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnBackToDashboard.setBounds(20, 10, 80, 23);
	    adminConsolePane.add(btnBackToDashboard);
	}

	public JPanel getPane() {
		return adminConsolePane;
	}

	public JTable getRoomTable() {
		return roomTable;
	}

	public JTextField getRoomIdTextBox() {
		return roomIdTextBox;
	}

	public JTextField getBuildingTextBox() {
		return buildingTextBox;
	}

	public JTextField getCapacityTextBox() {
		return capacityTextBox;
	}

	public JComboBox<String> getConditionDropdown() {
		return conditionDropdown;
	}

	public JTextField getStatusTextBox() {
		return statusTextBox;
	}

	public JButton getBtnAddRoom() {
		return btnAddRoom;
	}

	public JButton getBtnSaveChanges() {
		return btnSaveChanges;
	}

	public JButton getBtnEnableRoom() {
		return btnEnableRoom;
	}

	public JButton getBtnDisableRoom() {
		return btnDisableRoom;
	}

	public JButton getBtnRefreshRooms() {
		return btnRefreshRooms;
	}

	public JButton getBtnBackToDashboard() {
		return btnBackToDashboard;
	}

}
