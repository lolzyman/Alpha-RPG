import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	
	private String targetMap;
	
	// This is a constructor that accepts a stack of keys. This is passed from the creating window.
	// The keyArray stack is used to process key inputs only once and is a work around for handling inputs at a specified rate
	// The work around is used because I don't know how to use the KeyboardListener as interrupts 
	public Game_Window(String mapName) {
		
		//Sets the targetMap to the mapName passed to the constructor
		targetMap = mapName;
		
		//Sets the JPanel to be an 11 by 11 tile size with 50 by 50 pixel tiles
		this.setSize(550,550);
		//Sets the JPanel as visible
		this.setVisible(true);
		// Loads the Terrain Map from a default condition. Will need to be modified to load previous saves.
		initTerrainMap();
		
		exploreVision();
		// Repaints the JPanel
		repaint();
		
		Item testKey = new Item();
		testKey.setType(Item.KEY);
		testKey.setID("Door1");
		terrainMap[2][1].getInventory().addItem(testKey);
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

		//Makes the character look around the map
		//exploreVision();
		
		// Draws the background based on the Tile Map
		drawTerrainMap(g2, screenX, screenY);
		
		// Draws the Character on the Map
		// This will need to be changed to draw all the entities that can roam the map
		me.paint(g2, screenX, screenY);
		
	}
	
	// initializes the terrain map from a default File. This will need to be modified to be loaded from a save file
	public void initTerrainMap() {
		// Calls to the FileManager Class to get a Tile[][] of the level Map1
		terrainMap = FileManager.loadTerrainmap(targetMap);
		if(terrainMap == null) {
			TerrainGenerator.createDefaultMap(targetMap);
			terrainMap = FileManager.loadTerrainmap(targetMap);
		}
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
	// Method that handles character interactions with a tile.	
	public void interactWithTile(int xOffset, int yOffset) {
		int x = me.getxPos() + xOffset;
		int y = me.getyPos() + yOffset;
		if(terrainMap[y][x] instanceof Portal) {
			Portal portal = (Portal)terrainMap[y][x];
			loadMap(portal.getTargetMap());
		}
		terrainMap[y][x].interact(me);
	}
	public void loadMap(String targetMap) {
		me.setLocation(1, 1);
		this.targetMap = targetMap;
		this.initTerrainMap();
		screenX = 6;
		screenY = 6;
		repaint();
	}
	
	public void exploreVision() {
 		int x = me.getxPos();
		int y = me.getyPos();
		
		terrainMap[y][x].setSeen();
		
		//Look left
		int distance = 0;
		do {
			distance++;
			terrainMap[y][x - distance].setSeen();
		}while(terrainMap[y][x - distance].getTransparent() && distance < 5);
		
		//Look down
		distance = 0;
		do {
			distance++;
			terrainMap[y + distance][x].setSeen();
		}while(terrainMap[y + distance][x].getTransparent() && distance < 5);

		//Look right
		distance = 0;
		do {
			distance++;
			terrainMap[y][x + distance].setSeen();
		}while(terrainMap[y][x + distance].getTransparent() && distance < 5);

		//Look up
		distance = 0;
		do {
			distance++;
			terrainMap[y - distance][x].setSeen();
		}while(terrainMap[y - distance][x].getTransparent() && distance < 5);
		
		//Look up and right
		distance = 0;
		do {
			distance++;
			terrainMap[y - distance][x + distance].setSeen();
		}while(terrainMap[y - distance][x + distance].getTransparent() && distance < 2);
		
		//Look up and left
		distance = 0;
		do {
			distance++;
			terrainMap[y - distance][x - distance].setSeen();
		}while(terrainMap[y - distance][x - distance].getTransparent() && distance < 2);

		//Look down and right
		distance = 0;
		do {
			distance++;
			terrainMap[y + distance][x + distance].setSeen();
		}while(terrainMap[y + distance][x + distance].getTransparent() && distance < 2);

		//Look down and left
		distance = 0;
		do {
			distance++;
			terrainMap[y + distance][x - distance].setSeen();
		}while(terrainMap[y + distance][x - distance].getTransparent() && distance < 2);

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
			if(!me.moveLeft(terrainMap)) {
				interactWithTile(-1,0);
			}
			moveScreen();
			break;
		case 38:
			if(!me.moveUp(terrainMap)){
				interactWithTile(0,-1);
			}
			moveScreen();
			break;
		case 39:
			if(!me.moveRight(terrainMap)) {
				interactWithTile(1,0);
			}
			moveScreen();
			break;
		case 40:
			if(!me.moveDown(terrainMap)) {
				interactWithTile(0,1);
			}
			moveScreen();
			break;
		case 83:
			FileManager.saveTerrainMap(terrainMap, targetMap);
			System.exit(0);
		case 32:
			interactWithTile(0,0);
		default:
			break;
		}
		// Updates the visuals for the game
		repaint();
	}
	
	// moves the screen to center on the character if possible
	public void moveScreen() {
		int x = me.getxPos();
		int y = me.getyPos();
		if(x < 6) {
			screenX = 6;
		}else if(x > terrainMap[y].length - 6) {
			screenX = terrainMap[y].length - 5;
		}else {
			screenX = x + 1;
		}
		if(y < 6) {
			screenY = 6;
		}else if(y > terrainMap.length - 6) {
			screenY = terrainMap.length - 5;
		}else {
			screenY = y + 1;
		}
		exploreVision();
	}
}
