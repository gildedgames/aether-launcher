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
		statusLabel.setBounds(0, 130, 404, 27);
		mainPanel.add(statusLabel);

		frame.setBounds(100, 100, 420, 260);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setLocation(dim.width / 2 - this.frame.getSize().width / 2,
				dim.height / 2 - this.frame.getSize().height / 2);
		this.frame.setTitle("Aether II Launcher Updater");
	}

	public void setStatus(String text) {
		statusLabel.setText(text);
	}
}