import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Display1 extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8512897098475110795L;
	private int targetFPS = 60;
	private int keyArray = -1;
	private Game_Window game = new Game_Window(keyArray);
	//	private Enviornment game = new Enviornment(keyArray);
	private Menu_Window menu = new Menu_Window();

	public Display1() {
		super("Hello");
		this.setSize(568,597);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(new listener());
		this.addKeyListener(new boardListener());
		this.setLocation(0, 0);
		//		this.setLocationRelativeTo(null);
		Thread test = new Thread() {
			long time = System.currentTimeMillis();
			public void run() {
				int frames = 0;
				double currentFPS = 0;

				while(true) {
					// this area is going to be used to create the game

					if(getContentPane() == game) {
						game.updateGame(currentFPS, keyArray);
						//						System.out.println();
					}

					// This section of the code is designed to handle the FPS of the game
					// The way it works is that the loop runs the above section of code
					// It then pauses in the while loop below until the the target FPS is used up
					// If the processing for the game loop takes longer than the time allotted from the frame it just jumps
					// over the while loop and continues running. This code isn't designed to minimize CPU usage.
					long currentTime = System.currentTimeMillis();
					while(currentTime - time < 1000 / targetFPS) {
						currentTime = System.currentTimeMillis();
					}
					frames++;
					currentFPS = 1000 / (currentTime - time);
					time = currentTime;
				}
			}
		};
		switchToGame();
		test.start();
	}
	public void switchToGame() {
		this.setContentPane(game);
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		//Work around to always paint the game panel
		//		game.draw(g);
	}
	public Point getLocal() {
		return this.getLocationOnScreen();
	}
	public void switchScreens() {
		if(this.getContentPane() == game) {
			this.setContentPane(menu);
			repaint();
			return;
		}
		if(this.getContentPane() == menu) {
			this.setContentPane(game);
			return;
		}
	}
	public int getKeyArray(){
		return keyArray;
	}
	public void print(Object obj) {

	}
	private class listener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			//			System.out.println("This Program is a seperate thread");
			//			game.setMouseSampling(true);

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			//			switchScreens();
			//			game.setMouseSampling(false);

		}

	}
	private class boardListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			if(keyArray == -1) keyArray = arg0.getKeyCode();

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			if(keyArray == arg0.getKeyCode()) {
				keyArray = -1;
				game.updateMovement();
			}
//			System.out.println(arg0.getKeyCode());

		}

		@Override
		public void keyTyped(KeyEvent arg0) {

		}

	}
}