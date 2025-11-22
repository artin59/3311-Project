package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RegisterWindow {
	
	private JPanel registerPane;
	private JTextField txtBoxEmail;
	private JPasswordField txtBoxPassword;
	private JComboBox<String> accountTypeComboBox;
	private JPasswordField txtBoxOrgID;
	private JButton btnRegister;
	private JButton btnBackToLogin;


	
	public RegisterWindow() {
		buildWindow();
	}
	
	public void buildWindow() {
		// =====================================================
		// 1. Base panel setup
		// =====================================================
		registerPane = new JPanel();
		registerPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		registerPane.setLayout(null);

		// =====================================================
		// 2. Title
		// =====================================================

		JLabel titleLabel = new JLabel("Register Account");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
		titleLabel.setBounds(200, 50, 350, 40);
		registerPane.add(titleLabel);

		// =====================================================
		// 3. Labels (Email, Password, Account Type, Org ID)
		// =====================================================

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblEmail.setBounds(170, 120, 80, 25);
		registerPane.add(lblEmail);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblPassword.setBounds(140, 160, 110, 25);
		registerPane.add(lblPassword);

		JLabel lblAccountType = new JLabel("Account Type:");
		lblAccountType.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblAccountType.setBounds(110, 200, 150, 25);
		registerPane.add(lblAccountType);

		JLabel lblOrganizationID = new JLabel("Organization ID:");
		lblOrganizationID.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblOrganizationID.setBounds(90, 240, 170, 25);
		registerPane.add(lblOrganizationID);

		// =====================================================
		// 4. Input Fields (Email, Password, Account Type, Org ID)
		// =====================================================

		txtBoxEmail = new JTextField();
		txtBoxEmail.setBounds(250, 120, 290, 28);
		txtBoxEmail.setColumns(10);
		registerPane.add(txtBoxEmail);

		txtBoxPassword = new JPasswordField();
		txtBoxPassword.setBounds(250, 160, 290, 28);
		registerPane.add(txtBoxPassword);

		accountTypeComboBox = new JComboBox<>();
		accountTypeComboBox.setBounds(250, 200, 290, 28);
		accountTypeComboBox.addItem("(Select Account Type)");
		accountTypeComboBox.addItem("Student");
		accountTypeComboBox.addItem("Faculty");
		accountTypeComboBox.addItem("Staff");
		accountTypeComboBox.addItem("Partner");
		registerPane.add(accountTypeComboBox);

		txtBoxOrgID = new JPasswordField();
		txtBoxOrgID.setBounds(250, 240, 290, 28);
		registerPane.add(txtBoxOrgID);

		// =====================================================
		// 5. Buttons (Register + Back)
		// =====================================================

		btnRegister = new JButton("Register");
		btnRegister.setFont(new Font("Baskerville Old Face", Font.PLAIN, 27));
		btnRegister.setBounds(210, 290, 280, 40);
		registerPane.add(btnRegister);

		// Back button
		btnBackToLogin = new JButton("Back");
		btnBackToLogin.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
		btnBackToLogin.setBounds(20, 10, 80, 23);
		registerPane.add(btnBackToLogin);
		
	}


	public JPanel getPane() {
		return registerPane;
	}

	public JTextField getTxtBoxEmail() {
		return txtBoxEmail;
	}

	public JPasswordField getTxtBoxPassword() {
		return txtBoxPassword;
	}

	public JComboBox<String> getAccountTypeComboBox() {
		return accountTypeComboBox;
	}

	public JPasswordField getTxtBoxOrgID() {
		return txtBoxOrgID;
	}

	public JButton getBtnRegister() {
		return btnRegister;
	}

	public JButton getBtnBackToLogin() {
		return btnBackToLogin;
	}

}
