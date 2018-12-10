import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class Menu_Window extends JPanel{
	private static final long serialVersionUID = 1L;
	public Menu_Window() {
		this.setSize(500,500);
	}
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.RED);
		g2.fillRect(0, 0, 500, 500);
	}
}