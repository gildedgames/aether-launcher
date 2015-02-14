package net.aetherteam.aether.launcher.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GuiFirstTimeInit {

	private JFrame frame;

	private GuiFancyLabel statusLabel;

	public void start() {
		initialize();
	}

	public void setVisible(boolean bool) {
		this.frame.setVisible(bool);
	}

	public void quit() {
		this.setVisible(false);
		this.frame.dispose();
	}

	public GuiFirstTimeInit() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				GuiFirstTimeInit.class.getResource("/assets/icon_64.png")));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel mainPanel = new GuiFakePanoramaPanel();
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);

		statusLabel = new GuiFancyLabel();
		statusLabel.setText("Preparing...");
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setFont(new Font("Athelas", Font.PLAIN, 14));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setBounds(0, 193, 404, 27);
		mainPanel.add(statusLabel);

		GuiFancyLabel htmlLabel = new GuiFancyLabel();
		htmlLabel.setText("<html><center>Setting up the Aether II launcher...</center></html>");
		htmlLabel.setForeground(Color.WHITE);
		htmlLabel.setHorizontalAlignment(SwingConstants.CENTER);
		htmlLabel.setFont(new Font("Athelas", Font.BOLD, 22));
		htmlLabel.setBounds(0, 87, 402, 35);
		mainPanel.add(htmlLabel);
		frame.setBounds(100, 100, 420, 260);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setLocation(dim.width / 2 - this.frame.getSize().width / 2,
				dim.height / 2 - this.frame.getSize().height / 2);
	}

	public void setStatus(String text) {
		statusLabel.setText(text);
	}
}