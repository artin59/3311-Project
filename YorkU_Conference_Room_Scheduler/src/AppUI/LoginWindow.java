package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginWindow {
	
	private JPanel loginPane;

    private JTextField txtBoxEmail;
    private JPasswordField txtBoxPassword;
    private JButton btnLogin;
    private JButton btnRegister;

	private JButton backToIntro;
	
	public LoginWindow() {
		buildWindow();
	}
	
	public void buildWindow() {
		// =====================================================
		// 1. Base panel setup
		// =====================================================
		loginPane = new JPanel();
		loginPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		loginPane.setLayout(null);

		// =====================================================
		// 2. Header
		// =====================================================
		JLabel titleLabel = new JLabel("YorkU Conference Scheduler");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
		titleLabel.setBounds(90, 55, 550, 40);
		loginPane.add(titleLabel);

		// =====================================================
		// 3. Email & Password Labels
		// =====================================================

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblEmail.setBounds(170, 130, 80, 25);
		loginPane.add(lblEmail);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Bell MT", Font.PLAIN, 21));
		lblPassword.setBounds(140, 180, 110, 25);
		loginPane.add(lblPassword);

		// =====================================================
		// 4. Email & Password Text Fields
		// =====================================================
		txtBoxEmail = new JTextField();
		txtBoxEmail.setBounds(250, 130, 290, 28);
		txtBoxEmail.setColumns(10);
		loginPane.add(txtBoxEmail);

		txtBoxPassword = new JPasswordField();
		txtBoxPassword.setBounds(250, 180, 290, 28);
		loginPane.add(txtBoxPassword);

		// =====================================================
		// 5. Buttons (Login, Register)
		// =====================================================

		// --- Login ---
		btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Baskerville Old Face", Font.PLAIN, 27));
		btnLogin.setBounds(360, 240, 180, 40);
		loginPane.add(btnLogin);

		// --- Register ---
		btnRegister = new JButton("Register");
		btnRegister.setFont(new Font("Baskerville Old Face", Font.PLAIN, 27));
		btnRegister.setBounds(150, 240, 180, 40);
		loginPane.add(btnRegister);
		
	    // Back to Intro
	    backToIntro = new JButton("Back");
	    backToIntro.setFont(new Font("Baskerville Old Face", Font.ITALIC, 14));
	    backToIntro.setBounds(20, 10, 80, 23);
	    loginPane.add(backToIntro);
	}


	public JPanel getPane() {
		return loginPane;
	}


	public JTextField getTxtBoxEmail() {
		return txtBoxEmail;
	}


	public JPasswordField getTxtBoxPassword() {
		return txtBoxPassword;
	}


	public JButton getBtnLogin() {
		return btnLogin;
	}


	public JButton getBtnRegister() {
		return btnRegister;
	}


	public JButton getBtnBackToIntro() {
		return backToIntro;
	}

}