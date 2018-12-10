import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

public class Game_Window extends JPanel{

	/**
	 * 
	 */
	// This is the serialID for the user. It normally isn't useful but I have needed it before and it removes warning
	private static final long serialVersionUID = 1L;
	
	// This is the Character that has the ability to walk around the map
	private Character me = new Character(1,1);
	
	// This is the terrainMap which has the background tiles.
	private Tile[][] terrainMap;
	
	// This is the screen offset which allows the screen to follow the character around the map and only have to draw the 121 tiles
	private int screenX = 6, screenY = 6;
	
	// This is a obsolete Image for the map tile set. This will be handled in the FileManager when loading the map
	private BufferedImage mapTileSet;
	
	// This is a constructor that accepts a stack of keys. This is passed from the creating window.
	// The keyArray stack is used to process key inputs only once and is a work around for handling inputs at a specified rate
	// The work around is used because I don't know how to use the KeyboardListener as interrupts 
	public Game_Window(Stack<?> keyArray) {
		//Sets the JPanel to be an 11 by 11 tile size with 50 by 50 pixel tiles
		this.setSize(550,550);
		//Sets the JPanel as visible
		this.setVisible(true);
		// Loads the Terrain Map from a default condition. Will need to be modified to load previous saves.
		initTerrainMap();
		// Repaints the JPanel
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		// Calls the parent paint to prevent errors
		super.paint(g);
		// Converts the normal Graphics to Graphics2D which I know how to work with best
		Graphics2D g2 = (Graphics2D)g;
		// Draws Background
		g2.setColor(Color.GREEN);
		// Draws a Green background so I know when tiles are not being drawn
		g2.fillRect(0, 0, 550, 550);

		// Draws the background based on the Tile Map
		drawTerrainMap(g2, screenX, screenY);
		
		// Draws the Character on the Map
		// This will need to be changed to draw all the entities that can roam the map
		me.paint(g2, screenX, screenY);
		
	}
	
	// initializes the terrain map from a default File. This will need to be modified to be loaded from a save file
	public void initTerrainMap() {
		// Calls to the FileManager Class to get a Tile[][] of the level Map1
		terrainMap = FileManager.loadTerrainmap("Map1");
	}
	
	// Method to drawing the background based on the Terrain Map
	public void drawTerrainMap(Graphics2D g2, int x, int y) {
		// For loop to step through 11 Columns
		for(int i = 0; i < 11; i++) {
			// For loop to step through 11 Rows
			for(int j = 0; j < 11; j++) {
				// Checks to make sure the Tile isn't null. This will happen if areas of the map are not interactable and the creater
				// Didn't bother to create a Tile class for it
				if(terrainMap[y + j - 6][x + i - 6] != null) {
					// Calls a Method from Tile that draws the tile based on screen location
					terrainMap[y + j - 6][x + i - 6].drawTile(g2, i, j);
				}
			}
		}
	}
	// Method that handles character interactions with a tile. Currently not used
	public void interactWithTile() {
		@SuppressWarnings("unused")
		int x = me.xPos;
		@SuppressWarnings("unused")
		int y = me.yPos;
	}
	// Method that the thread handles game updates. Takes the same keyArray as the constructor. Runs a switch statement for handling
	// key actions
	public void updateGame(double currentFPS, Stack<Integer> keyArray) {
		//Handles Key Events
		
		// key is the value of the top keyCode on the stack
		// If the stack is empty the value is set to -1 and the statement handles accordingly
		int key;
		if(keyArray.isEmpty()) {
			key = -1;
		}else {
			key = (int)keyArray.pop();
		}
		
		//keyCodes 37, 38, 39, 40 are the arrow keys. The Switch statement tells the character to move left, right, up or down.
		switch(key) {
		case 37:
			me.moveLeft();
			moveScreen();
			break;
		case 38:
			me.moveUp();
			moveScreen();
			break;
		case 39:
			me.moveRight();
			moveScreen();
			break;
		case 40:
			me.moveDown();
			moveScreen();
			break;
		default:
			break;
		}
		// Updates the visuals for the game
		repaint();
	}
	
	// moves the screen to center on the character if possible
	public void moveScreen() {
		int x = me.xPos;
		int y = me.yPos;
		if(x < 6) {
			screenX = 6;
		}else if(x > terrainMap[y].length - 6) {
			screenX = terrainMap[y].length - 5;
		}else {
			screenX = me.xPos + 1;
		}
		if(y < 6) {
			screenY = 6;
		}else if(y > terrainMap.length - 6) {
			screenY = terrainMap.length - 5;
		}else {
			screenY = me.yPos + 1;
		}
	}
	
	// Embedded Class for the Character In the Game
	private class Character {
		// Character Sizes. Irrelavent for future implementations
		private final int XSIZE = 50, YSIZE = 50;
		
		// The position of the character in the game
		private int xPos, yPos;
		
		// The Image for the Character
		private BufferedImage me;

		// Constructor for the character that starts at the given map location
		public Character(int x, int y){
			// Calls a method to set the location
			setLocation(x, y);
			
			// Tries to load an image for the character 
			me = FileManager.loadTileFromIndex(mapTileSet, 3);
		}
		// Paint method for the Character
		// Not to be confused with the paint method associated with JPanels and JFrames
		public void paint(Graphics2D g2, int x, int y) {
			
			// If the Character has an image, draw it at the location, if it doesn't draw a gray sphere
			if(me != null) {
				g2.drawImage(me, (xPos - x + 6)*50, (yPos - y + 6)*50, null);
			}else{
				g2.setColor(Color.GRAY);
				g2.fillOval((xPos - x + 6)*50, (yPos - y + 6)*50, XSIZE, YSIZE);
			}
		}

		// Method to set the location of the Character on the Map
		public void setLocation(int x, int y) {
			xPos = x;
			yPos = y;
		}
		// Method to try and move the character to the north
		public void moveUp() {
			if(terrainMap[yPos - 1][xPos].walkAble) {
				yPos--;
				interactWithTile();
			}
		}
		// Method to try and move the character to the south
		public void moveDown() {
			if(terrainMap[yPos + 1][xPos].walkAble) {
				yPos++;
				interactWithTile();
			}
		}
		// Method to try and move the character to the east
		public void moveRight() {
			if(terrainMap[yPos][xPos + 1].walkAble) {
				xPos++;
				interactWithTile();
			}
		}
		// Method to try and move the character to the west
		public void moveLeft() {
			if(terrainMap[yPos][xPos - 1].walkAble) {
				xPos--;
				interactWithTile();
			}
		}
	}
}
