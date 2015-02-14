package net.aetherteam.aether.launcher.gui.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

public class GuiFakePanoramaPanel extends JPanel {
	
	Image img;
	
    public GuiFakePanoramaPanel()
    {
		try {
			img = javax.imageio.ImageIO.read(GuiFakePanoramaPanel.class.getResource("/assets/init/fakePanorama.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, -22, null);
	}
}