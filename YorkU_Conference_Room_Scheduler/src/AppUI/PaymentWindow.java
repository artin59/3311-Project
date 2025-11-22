package AppUI;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PaymentWindow {
	
	// Payment pane
	private JPanel paymentPane;

	private JTextField amountTextBox;

	private JComboBox<String> paymentMethodComboBox;
	
	private JTextField nameOnCardTextBox;
	private JTextField cardNumberTextBox;
	private JTextField expiryTextBox;
	private JTextField cvvTextBox;
	private JTextField billingRefTextBox;

	private JButton btnConfirmPayment;
	private JButton btnCancelPayment;
	
	public PaymentWindow(double amountToPay) {
		buildWindow(amountToPay);

	}
	
	public void buildWindow(double amountToPay) {

	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    paymentPane = new JPanel();
	    paymentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    paymentPane.setLayout(null);

	    // =====================================================
	    // 2. Title
	    // =====================================================

	    JLabel paymentTitleLabel = new JLabel("Payment & Confirmation");
	    paymentTitleLabel.setFont(new Font("Arial", Font.BOLD, 26));
	    paymentTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    paymentTitleLabel.setBounds(120, 20, 450, 35);
	    paymentPane.add(paymentTitleLabel);

	    // =====================================================
	    // 3. Amount due (from parameter)
	    // =====================================================

	    JLabel amountLabel = new JLabel("Amount Due:");
	    amountLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    amountLabel.setBounds(160, 80, 120, 25);
	    paymentPane.add(amountLabel);

	    amountTextBox = new JTextField();
	    amountTextBox.setEditable(false);
	    amountTextBox.setFont(new Font("Tahoma", Font.BOLD, 16));
	    amountTextBox.setBounds(290, 82, 200, 25);
	    amountTextBox.setHorizontalAlignment(SwingConstants.RIGHT);
	    amountTextBox.setText(String.format("$%.2f", amountToPay));
	    paymentPane.add(amountTextBox);

	    // =====================================================
	    // 4. Payment method
	    // =====================================================

	    JLabel methodLabel = new JLabel("Payment Method:");
	    methodLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    methodLabel.setBounds(160, 125, 140, 25);
	    paymentPane.add(methodLabel);

	    paymentMethodComboBox = new JComboBox<>();
	    paymentMethodComboBox.setBounds(320, 127, 210, 25);
	    paymentMethodComboBox.addItem("Select a method...");
	    paymentMethodComboBox.addItem("Credit Card");
	    paymentMethodComboBox.addItem("Debit Card");
	    paymentMethodComboBox.addItem("Institutional Billing");
	    paymentPane.add(paymentMethodComboBox);

	    // =====================================================
	    // 5. Standard payment info fields
	    // =====================================================

	    // Name on card / payer
	    JLabel nameOnCardLabel = new JLabel("Name on Card:");
	    nameOnCardLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    nameOnCardLabel.setBounds(160, 170, 130, 25);
	    paymentPane.add(nameOnCardLabel);

	    nameOnCardTextBox = new JTextField();
	    nameOnCardTextBox.setBounds(320, 172, 210, 22);
	    paymentPane.add(nameOnCardTextBox);

	    // Card number
	    JLabel cardNumberLabel = new JLabel("Card Number:");
	    cardNumberLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    cardNumberLabel.setBounds(160, 205, 130, 25);
	    paymentPane.add(cardNumberLabel);

	    cardNumberTextBox = new JTextField();
	    cardNumberTextBox.setBounds(320, 207, 210, 22);
	    paymentPane.add(cardNumberTextBox);

	    // Expiry
	    JLabel expiryLabel = new JLabel("Expiry (MM/YY):");
	    expiryLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    expiryLabel.setBounds(160, 240, 150, 25);
	    paymentPane.add(expiryLabel);

	    expiryTextBox = new JTextField();
	    expiryTextBox.setBounds(320, 242, 70, 22);
	    paymentPane.add(expiryTextBox);

	    // CVV
	    JLabel cvvLabel = new JLabel("CVV:");
	    cvvLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    cvvLabel.setBounds(410, 240, 50, 25);
	    paymentPane.add(cvvLabel);

	    cvvTextBox = new JTextField();
	    cvvTextBox.setBounds(450, 242, 80, 22);
	    paymentPane.add(cvvTextBox);

	    // Billing reference (Institutional Billing)
	    JLabel billingRefLabel = new JLabel("Billing Reference:");
	    billingRefLabel.setFont(new Font("Bell MT", Font.PLAIN, 18));
	    billingRefLabel.setBounds(160, 275, 150, 25);
	    paymentPane.add(billingRefLabel);

	    billingRefTextBox = new JTextField();
	    billingRefTextBox.setBounds(320, 277, 210, 22);
	    paymentPane.add(billingRefTextBox);

	    // =====================================================
	    // 6. Buttons
	    // =====================================================

	    btnConfirmPayment = new JButton("Confirm Payment");
	    btnConfirmPayment.setFont(new Font("Baskerville Old Face", Font.PLAIN, 20));
	    btnConfirmPayment.setBounds(160, 320, 200, 35);
	    paymentPane.add(btnConfirmPayment);

	    btnCancelPayment = new JButton("Cancel");
	    btnCancelPayment.setFont(new Font("Baskerville Old Face", Font.ITALIC, 18));
	    btnCancelPayment.setBounds(380, 320, 150, 35);
	    paymentPane.add(btnCancelPayment);

	    // =====================================================
	    // 7. Wire method-based field enabling
	    // =====================================================

	    paymentMethodComboBox.addActionListener(e -> {
	    	updatePaymentFields();
	    	System.out.println("updating payment listeners");
	    	
	    });
	    updatePaymentFields(); // initial state
	}
	
	
	
	
	
	private void updatePaymentFields() {
	    if (paymentMethodComboBox == null) return;

	    String method = (String) paymentMethodComboBox.getSelectedItem();
	    if (method == null) method = "";
	    
	    Color onColor = Color.WHITE;
	    Color offColor = Color.LIGHT_GRAY;
	    
	    switch (method) {
	        case "Credit Card":
	        case "Debit Card":
	            // Enable card fields
	            nameOnCardTextBox.setEnabled(true);
	            cardNumberTextBox.setEnabled(true);
	            expiryTextBox.setEnabled(true);
	            cvvTextBox.setEnabled(true);
	            nameOnCardTextBox.setBackground(onColor);
	            cardNumberTextBox.setBackground(onColor);
	            expiryTextBox.setBackground(onColor);
	            cvvTextBox.setBackground(onColor);

	            // Disable billing reference
	            billingRefTextBox.setEnabled(false);
	            billingRefTextBox.setText("");
	            billingRefTextBox.setBackground(offColor);

	            break;

	        case "Institutional Billing":
	            // Disable card fields
	            nameOnCardTextBox.setEnabled(false);
	            cardNumberTextBox.setEnabled(false);
	            expiryTextBox.setEnabled(false);
	            cvvTextBox.setEnabled(false);

	            nameOnCardTextBox.setText("");
	            cardNumberTextBox.setText("");
	            expiryTextBox.setText("");
	            cvvTextBox.setText("");
	            nameOnCardTextBox.setBackground(offColor);
	            cardNumberTextBox.setBackground(offColor);
	            expiryTextBox.setBackground(offColor);
	            cvvTextBox.setBackground(offColor);

	            // Enable billing reference
	            billingRefTextBox.setEnabled(true);
	            billingRefTextBox.setBackground(onColor);
	            break;

	        default: // "Select a method..." or anything else
	            // Disable everything
	            nameOnCardTextBox.setEnabled(false);
	            cardNumberTextBox.setEnabled(false);
	            expiryTextBox.setEnabled(false);
	            cvvTextBox.setEnabled(false);
	            billingRefTextBox.setEnabled(false);

	            nameOnCardTextBox.setText("");
	            cardNumberTextBox.setText("");
	            expiryTextBox.setText("");
	            cvvTextBox.setText("");
	            billingRefTextBox.setText("");
	            
	            nameOnCardTextBox.setBackground(onColor);
	            cardNumberTextBox.setBackground(onColor);
	            expiryTextBox.setBackground(onColor);
	            cvvTextBox.setBackground(onColor);
	            billingRefTextBox.setBackground(onColor);
	            break;
	    }
	}

	public JPanel getPane() {
		return paymentPane;
	}

	public JTextField getAmountTextBox() {
		return amountTextBox;
	}

	public JComboBox<String> getPaymentMethodComboBox() {
		return paymentMethodComboBox;
	}

	public JTextField getNameOnCardTextBox() {
		return nameOnCardTextBox;
	}

	public JTextField getCardNumberTextBox() {
		return cardNumberTextBox;
	}

	public JTextField getExpiryTextBox() {
		return expiryTextBox;
	}

	public JTextField getCvvTextBox() {
		return cvvTextBox;
	}

	public JTextField getBillingRefTextBox() {
		return billingRefTextBox;
	}

	public JButton getBtnConfirmPayment() {
		return btnConfirmPayment;
	}

	public JButton getBtnCancelPayment() {
		return btnCancelPayment;
	}
}