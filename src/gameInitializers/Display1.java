package gameInitializers;
import java.awt.CardLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import entities.Player;
import systemManagers.FileManager;


public class Display1 extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8512897098475110795L;
	private int targetFPS = 60;
	private Stack<Integer> keyArray = new Stack<Integer>();
	private Game_Window game;
	private Menu_Window menu = new Menu_Window();
	private JPanel cards = new JPanel();
	private JPanel gamePanel;
	private JPanel menuPanel = (JPanel)menu;

	public Display1() {
		super("Hello");
		this.setSize(568,597);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addMouseListener(new listener());
		this.addKeyListener(new boardListener());
		this.setFocusable(true);
		this.setLocation(0, 0);
		cards.setLayout(new CardLayout());
		cards.add(menu, "Menu");
		this.setContentPane(menu);
//		this.setContentPane(cards);
//		((CardLayout)cards.getLayout()).show(this.getContentPane(), "Menu");
		setupMenuListeners();
	}
	public void switchToGame() {
		this.remove(menu);
		this.setContentPane(game);
		this.requestFocus();

		repaint();
	}
	public Point getLocal() {
		return this.getLocationOnScreen();
	}
	public void initiateGameLogic() {
		Thread test = new Thread() {
			long time = System.currentTimeMillis();
			public void run() {
				double currentFPS = 0;

				while(true) {
					// this area is going to be used to create the game
					if(getContentPane() == game) {
						game.updateGame(currentFPS, keyArray);
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
					currentFPS = 1000 / (currentTime - time);
					time = currentTime;
				}
			}
		};
		test.start();
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
	public void updateManager() {
		if(menu.start()) {
			game = new Game_Window(menu.getTargetMap());
			gamePanel = (JPanel)game;
			cards.add(gamePanel, "Game");
			initiateGameLogic();
			switchToGame();
		}
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
			if(game == null) {
				updateManager();
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

	}
	public void setupMenuListeners() {
		Stack<Object> listenAbles = menu.getListenAbles();
		for(Object object: listenAbles) {
			 if(object instanceof JButton) {
				 JButton button = (JButton) object;
				 button.addActionListener(new menuListener());
			 }
		}
	}
	public void returnToGame() {
		if(this.getContentPane() != game) {
			this.setContentPane(game);
		}
	}
	private class boardListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent arg0) {
			keyArray.add(arg0.getKeyCode());
			switch(arg0.getKeyCode()) {
			case 37:
				break;
			case 38:
				break;
			case 39:
				break;
			case 40:
				break;
			case 32:
				// Space Key
				break;
			case 27:
				returnToGame();
			default:
				System.out.println(arg0.getKeyCode());
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			keyArray.remove(new Integer(arg0.getKeyCode()));
		}

		@Override
		public void keyTyped(KeyEvent arg0) {

		}

	}
	private class menuListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if(((JButton)e.getSource()).getName() == "Load Button") {
					if(new File("Maps/" + menu.getTargetMap()).exists()) {
						game = new Game_Window(menu.getTargetMap());
					}else {
						
					}
					initiateGameLogic();
					switchToGame();
				}
				if(((JButton)e.getSource()).getName() == "Character Load Button") {
					if(new File("Saves/" + menu.getTargetCharacter() + ".rpgsave").exists()) {
						Object[] loadInfo = FileManager.loadCharacter(menu.getTargetCharacter());
						if(loadInfo[0] != null && loadInfo[0] != null) {
							game = new Game_Window((String)loadInfo[0], (Player)loadInfo[1]);
						}
					}else {
						
					}
					initiateGameLogic();
					switchToGame();
				}
			}catch(Exception exp) {
				System.out.println("Display1");
				System.out.println("The Exception is: " + exp);
			}
		}
	}
}