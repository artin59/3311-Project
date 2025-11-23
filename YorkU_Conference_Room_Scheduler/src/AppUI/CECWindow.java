package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CECWindow {
	// CEC / Admin Management pane
	private JPanel cecPane;

	private JTable adminTable;
	private JTable logTable;
	
	private JTextField adminIdTextBox;
	private JButton btnGrantAdmin;
	// private JButton btnDisableAdmin; // Commented out as per requirements
	private JButton btnRefreshAdmins;
	private JButton btnBackToDashboard;
	private JTextField adminStatusTextBox;
	
	public CECWindow() {
		buildWindow();
	}
	
	public void buildWindow() {
	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    cecPane = new JPanel();
	    cecPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    cecPane.setLayout(null);

	    // =====================================================
	    // 2. Header & section titles
	    // =====================================================

	    JLabel cecTitleLabel = new JLabel("Chief Event Coordinator Console");
	    cecTitleLabel.setFont(new Font("Arial", Font.BOLD, 26));
	    cecTitleLabel.setBounds(130, 10, 480, 35);
	    cecPane.add(cecTitleLabel);

	    JLabel adminListTitleLabel = new JLabel("All Users");
	    adminListTitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    adminListTitleLabel.setBounds(20, 55, 200, 22);
	    cecPane.add(adminListTitleLabel);

	    // =====================================================
	    // 3. Admin users table (left) - Empty, populated from CSV
	    // =====================================================

	    JScrollPane adminScrollPane = new JScrollPane();
	    adminScrollPane.setBounds(20, 85, 300, 220);
	    cecPane.add(adminScrollPane);

	    adminTable = new JTable();
	    adminTable.setModel(new DefaultTableModel(
	        new Object[][] {},
	        new String[] {
	            "User ID", "Email", "Status"
	        }
	    ) {
	        private static final long serialVersionUID = 1L;
	        
	        boolean[] columnEditables = new boolean[] {
	            false, false, false
	        };
	        
	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return columnEditables[column];
	        }
	    });

	    adminScrollPane.setViewportView(adminTable);

	    // =====================================================
	    // 4. Selected user details (left, bottom)
	    // =====================================================

	    JLabel adminIdLabel = new JLabel("User ID:");
	    adminIdLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    adminIdLabel.setBounds(20, 315, 80, 20);
	    cecPane.add(adminIdLabel);

	    adminIdTextBox = new JTextField();
	    adminIdTextBox.setEditable(false);
	    adminIdTextBox.setBounds(105, 317, 215, 18);
	    cecPane.add(adminIdTextBox);

	    JLabel adminStatusLabel = new JLabel("Status:");
	    adminStatusLabel.setFont(new Font("Bell MT", Font.PLAIN, 16));
	    adminStatusLabel.setBounds(20, 345, 80, 20);
	    cecPane.add(adminStatusLabel);

	    adminStatusTextBox = new JTextField();
	    adminStatusTextBox.setEditable(false);
	    adminStatusTextBox.setBounds(105, 347, 215, 18);
	    cecPane.add(adminStatusTextBox);

	    // Table selection is handled in MainFrame

	    // =====================================================
	    // 6. CEC actions (right, bottom)
	    // =====================================================

	    // Grant Admin (convert user to admin)
	    btnGrantAdmin = new JButton("Grant Admin");
	    btnGrantAdmin.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnGrantAdmin.setBounds(340, 317, 150, 50);
	    cecPane.add(btnGrantAdmin);

	    // Disable Admin button - Commented out as per requirements
	    /*
	    btnDisableAdmin = new JButton("Disable Admin");
	    btnDisableAdmin.setFont(new Font("Baskerville Old Face", Font.ITALIC, 15));
	    btnDisableAdmin.setBounds(520, 315, 150, 23);
	    cecPane.add(btnDisableAdmin);
	    */

	    // =====================================================
	    // 7. Utility buttons (bottom-right)
	    // =====================================================

	    // Single Refresh for admins table
	    btnRefreshAdmins = new JButton("Refresh");
	    btnRefreshAdmins.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnRefreshAdmins.setBounds(520, 345, 150, 23);
	    cecPane.add(btnRefreshAdmins);

	    // Back to dashboard
	    btnBackToDashboard = new JButton("Back");
	    btnBackToDashboard.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    btnBackToDashboard.setBounds(20, 10, 80, 23);
	    cecPane.add(btnBackToDashboard);
	}

	public JPanel getPane() {
		return cecPane;
	}

	public JTable getAdminTable() {
		return adminTable;
	}

	public JTable getLogTable() {
		return logTable;
	}

	public JTextField getAdminIdTextBox() {
		return adminIdTextBox;
	}

	public JButton getBtnGrantAdmin() {
		return btnGrantAdmin;
	}

	/*
	public JButton getBtnDisableAdmin() {
		return btnDisableAdmin;
	}
	*/

	public JButton getBtnRefreshAdmins() {
		return btnRefreshAdmins;
	}

	public JButton getBtnBackToDashboard() {
		return btnBackToDashboard;
	}

	public JTextField getAdminStatusTextBox() {
		return adminStatusTextBox;
	}
}