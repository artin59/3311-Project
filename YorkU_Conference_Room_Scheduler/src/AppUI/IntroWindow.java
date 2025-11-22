package AppUI;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class IntroWindow {
	
	private JPanel introPane;

	private JButton btnIntroCheckIn;
	private JButton btnIntroReserve;
	private JLabel introTitleLabel;
	private JLabel introSubtitleLabel;
	
	public IntroWindow() {
		buildWindow();
	}

	
	public void buildWindow() {
	    // =====================================================
	    // 1. Base panel setup
	    // =====================================================
	    introPane = new JPanel();
	    introPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	    introPane.setLayout(null);

	    // =====================================================
	    // 2. Title & subtitle
	    // =====================================================

	    introTitleLabel = new JLabel("Conference Room System");
	    introTitleLabel.setFont(new Font("Arial", Font.BOLD, 32));
	    introTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    introTitleLabel.setBounds(79, 75, 529, 40);
	    introPane.add(introTitleLabel);

	    introSubtitleLabel = new JLabel("Select an option to continue");
	    introSubtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
	    introSubtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    introSubtitleLabel.setBounds(79, 125, 529, 30);
	    introPane.add(introSubtitleLabel);

	    // =====================================================
	    // 3. Big action buttons
	    // =====================================================

	    // Check-In button
	    btnIntroCheckIn = new JButton("Check In");
	    btnIntroCheckIn.setFont(new Font("Baskerville Old Face", Font.PLAIN, 24));
	    btnIntroCheckIn.setBounds(79, 170, 225, 80);
	    introPane.add(btnIntroCheckIn);

	    // Reserve a Room button
	    btnIntroReserve = new JButton("Reserve a Room");
	    btnIntroReserve.setFont(new Font("Baskerville Old Face", Font.PLAIN, 24));
	    btnIntroReserve.setBounds(383, 170, 225, 80);
	    introPane.add(btnIntroReserve);
	}


	public JPanel getPane() {
		return introPane;
	}


	public JButton getBtnIntroCheckIn() {
		return btnIntroCheckIn;
	}


	public JButton getBtnIntroReserve() {
		return btnIntroReserve;
	}
}
