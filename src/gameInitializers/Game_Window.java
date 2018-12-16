package gameInitializers;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import entities.Character;
import entities.Item;
import systemManagers.FileManager;
import systemManagers.TerrainGenerator;
import tiles.Portal;
import tiles.Tile;

public class Game_Window extends JPanel{

	/**
	 * 
	 */
	// This is the serialID for the user. It normally isn't useful but I have needed it before and it removes warning
	private static final long serialVersionUID = 1L;

	// This is the Character that has the ability to walk around the map
	private Character me = new Character(4,4);

	// This is the terrainMap which has the background tiles.
	private Tile[][] terrainMap;
	
	private int[][] characterMemory;
	
	private BufferedImage[] memoryImages;

	// This is the screen offset which allows the screen to follow the character around the map and only have to draw the 121 tiles
	private int screenX = 6, screenY = 6;

	private String targetMap;
	
	private JTabbedPane playerInventory;
	
	private JPanel testCard;
	
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

		characterMemory = new int[terrainMap.length][terrainMap[0].length];
		//Arrays.setAll(characterMemory, null);
		for(int y = 0; y < characterMemory.length; y++) {
			for(int x = 0; x < characterMemory[x].length; x++) {
				characterMemory[y][x] = -1;
			}
		}
		
		memoryImages = FileManager.loadTilesFromIndex(FileManager.loadImage("Maps/Map1/CharacterMemory.png"));
		
		// Repaints the JPanel
		repaint();

		Item testKey = new Item();
		testKey.setType(Item.KEY);
		testKey.setID("Door1");
		terrainMap[2][1].getInventory().addItem(testKey);
	}
	@Override
	public void paint(Graphics g) {
		//Calls the parent paint to prevent errors
		super.paint(g);
		// Converts the normal Graphics to Graphics2D which I know how to work with best
		Graphics2D g2 = (Graphics2D)g;
		// Draws Background
		g2.setColor(Color.GREEN);
		// Draws a Green background so I know when tiles are not being drawn
		g2.fillRect(0, 0, 550, 550);
		
		paintMemory(g2, screenX, screenY);

		exploreVision(g2);
		// Draws the Character on the Map
		// This will need to be changed to draw all the entities that can roam the map
		me.paint(g2, screenX, screenY);

	}
	
	public void paintMemory(Graphics2D g2, int screenX, int screenY) {
		// For loop to step through 11 Columns
		for(int i = 0; i < 11; i++) {
			// For loop to step through 11 Rows
			for(int j = 0; j < 11; j++) {
				// Checks to make sure the Tile isn't null. This will happen if areas of the map are not interactable and the creater
				// Didn't bother to create a Tile class for it
				if(characterMemory[screenY + j - 6][screenX + i - 6] != -1) {
					g2.drawImage(memoryImages[characterMemory[screenY + j - 6][screenX + i - 6]], i * 50, j * 50, null);
					
				}else {
					g2.setColor(Color.BLACK);
					g2.fillRect(i * 50, j * 50,50,50);
				}
			}
		}
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

	// Method that handles character interactions with a tile.	
	public void interactWithTile(int xOffset, int yOffset) {
		int x = me.getxPos() + xOffset;
		int y = me.getyPos() + yOffset;
		if(terrainMap[y][x] instanceof Portal) {
			Portal portal = (Portal)terrainMap[y][x];
			loadMap(portal.getTargetMap());
			return;
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

	public void exploreVision(Graphics2D g2) {
		int x = me.getxPos();
		int y = me.getyPos();

		//terrainMap[y][x].setSeen();
		int distance = 5;
		int left = distance, right = distance, up = distance, down = distance;
		if(x < distance + 1) {
			left = x; 
		}
		if(x > terrainMap[0].length-6) {
			right = terrainMap[0].length - x - 1;
		}
		if(y < distance + 1) {
			up = y; 
		}
		if(y > terrainMap.length-6) {
			down = terrainMap.length - y - 1;
		}
		int xCenter = left;
		int yCenter = up;

		int yRange = 1 + up + down;
		int xRange = 1 + left + right;
		boolean[][] seen = new boolean[yRange][xRange];
		Tile[][] pocket = new Tile[yRange][xRange];

		// walkAble is used for Path finding. This shouldn't be used for the character.
		// You are going to have too much fun with this
		int[][] walkAble = new int[yRange][xRange];

		PriorityQueue<int[]> visionQueue = new PriorityQueue(new visionComparator());
		for(int yIndex = -up; yIndex < down + 1; yIndex++) {
			for(int xIndex = -left; xIndex < right + 1; xIndex++) {
				int[] queueEntry = {yIndex,xIndex, (int)(Math.pow(yIndex,2) + Math.pow(xIndex,2))};
				visionQueue.add(queueEntry);
			}
		}

		while(!visionQueue.isEmpty()) {
			int[] value = visionQueue.poll();
			int xIndex = value[1];
			int yIndex = value[0];
			//			System.out.println(Math.pow(((double)distance + 0.5),2));
			//			System.out.println(value[2]);
			if(value[2] < Math.pow(((double)distance + 0.5),2)) {
				try {
					pocket[yIndex + up][xIndex + left] = terrainMap[y + yIndex][x + xIndex];
					Tile tile = pocket[yIndex + up][xIndex + left];
					if(xIndex == 0 && yIndex == 0) {
//						tile.setSeen();
						characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
						seen[yIndex + up][xIndex + left] = true;
					}else if(xIndex == 0) {
						if(yIndex > 0) {
							Tile tile2 = pocket[yIndex + up - 1][xIndex + left];
							if(seen[yIndex + up - 1][xIndex + left] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}else {
							Tile tile2 = pocket[yIndex + up + 1][xIndex + left];
							if(seen[yIndex + up + 1][xIndex + left] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}
					}else if(yIndex == 0) {
						if(xIndex > 0) {
							Tile tile2 = pocket[yIndex + up][xIndex + left - 1];
							if(seen[yIndex + up][xIndex + left - 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}else {
							Tile tile2 = pocket[yIndex + up][xIndex + left + 1];
							if(seen[yIndex + up][xIndex + left + 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}
					}else if(yIndex == xIndex) {
						if(xIndex > 0) {
							Tile tile2 = pocket[yIndex + up - 1][xIndex + left - 1];
							if(seen[yIndex + up - 1][xIndex + left - 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}else {
							Tile tile2 = pocket[yIndex + up + 1][xIndex + left + 1];
							if(seen[yIndex + up + 1][xIndex + left + 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}
					}else if(yIndex == -xIndex) {
						if(xIndex > 0) {
							Tile tile2 = pocket[yIndex + up + 1][xIndex + left - 1];
							if(seen[yIndex + up + 1][xIndex + left - 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}else {
							Tile tile2 = pocket[yIndex + up - 1][xIndex + left + 1];
							if(seen[yIndex + up - 1][xIndex + left + 1] && tile2.getTransparent()) {
//								tile.setSeen();
								seen[yIndex + up][xIndex + left] = true;
								characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
							}
						}
					}else if(yIndex > 0 && xIndex > 0) {
						//Check Quadrant II
						Tile tile2 = pocket[yIndex + up - 1][xIndex + left - 1];
						Tile tile3;
						boolean seen3;
						boolean seen4;
						if(xIndex > yIndex) {
							tile3 = pocket[yIndex + up][xIndex + left - 1];
							seen3 = seen[yIndex + up][xIndex + left - 1];
							seen4 = seen[yIndex + up - 1][xIndex + left];
							
						}else {
							tile3 = pocket[yIndex + up - 1][xIndex + left];
							seen3 = seen[yIndex + up - 1][xIndex + left];
							seen4 = seen[yIndex + up][xIndex + left - 1];
						}
						if(seen[yIndex + up - 1][xIndex + left - 1] && tile2.getTransparent()) {
							seen[yIndex + up][xIndex + left] = true;
							characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
						}
					}else if(yIndex < 0 && xIndex > 0) {
						//Check Quadrant I
						Tile tile2 = pocket[yIndex + up + 1][xIndex + left - 1];
						Tile tile3;
						boolean seen3;
						boolean seen4;
						if(xIndex < -yIndex) {
							tile3 = pocket[yIndex + up][xIndex + left - 1];
							seen3 = seen[yIndex + up][xIndex + left - 1];
							seen4 = seen[yIndex + up + 1][xIndex + left];
							
						}else {
							tile3 = pocket[yIndex + up + 1][xIndex + left];
							seen3 = seen[yIndex + up + 1][xIndex + left];
							seen4 = seen[yIndex + up][xIndex + left - 1];
						}
						if(seen[yIndex + up + 1][xIndex + left - 1] && tile2.getTransparent()) {
							seen[yIndex + up][xIndex + left] = true;
							characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
						}
					}else if(yIndex > 0 && xIndex < 0) {
						//Check Quadrant III
						Tile tile2 = pocket[yIndex + up - 1][xIndex + left + 1];
						Tile tile3;
						boolean seen3;
						boolean seen4;
						if(-xIndex > yIndex) {
							tile3 = pocket[yIndex + up][xIndex + left + 1];
							seen3 = seen[yIndex + up][xIndex + left + 1];
							seen4 = seen[yIndex + up - 1][xIndex + left];
							
						}else {
							tile3 = pocket[yIndex + up - 1][xIndex + left];
							seen3 = seen[yIndex + up - 1][xIndex + left];
							seen4 = seen[yIndex + up][xIndex + left + 1];
						}
						if(seen[yIndex + up - 1][xIndex + left + 1] && tile2.getTransparent()) {
							seen[yIndex + up][xIndex + left] = true;
							characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
						}
						
						
					}else if(yIndex < 0 && xIndex < 0) {
						//Check Quadrant IV
						/*
						
						*/
						Tile tile2 = pocket[yIndex + up + 1][xIndex + left + 1];
						Tile tile3;
						boolean seen3;
						boolean seen4;
						if(-xIndex < -yIndex) {
							tile3 = pocket[yIndex + up][xIndex + left + 1];
							seen3 = seen[yIndex + up][xIndex + left + 1];
							seen4 = seen[yIndex + up + 1][xIndex + left];
							
						}else {
							tile3 = pocket[yIndex + up + 1][xIndex + left];
							seen3 = seen[yIndex + up + 1][xIndex + left];
							seen4 = seen[yIndex + up][xIndex + left + 1];
						}
						if(seen[yIndex + up + 1][xIndex + left + 1] && tile2.getTransparent()) {
							seen[yIndex + up][xIndex + left] = true;
							characterMemory[y + yIndex][x + xIndex] = tile.drawTile(g2, screenX, screenY, 6, 6);
						}
					}
				}catch(Exception e) {
					//System.out.println("Game Window Line 354");
					//System.out.format(" xPos: %s\n yPos: %s\n xIndex: %s\n yIndex: %s\n", x, y,xIndex,yIndex);
				}
			}
		}
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
			break;
		case 73:
			if(playerInventory == null) {
				
				playerInventory = me.getInventory().getInventoryScreen();
				JFrame display1 = (JFrame)this.getParent().getParent().getParent();
				display1.remove(this);
				display1.setContentPane(playerInventory);	
				display1.repaint();
				display1.validate();
				
			}
			break;
		case 27:
			System.out.println("This still gets passed");
			playerInventory = null;
			break;
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
//		exploreVision();
	}

	private class visionComparator implements Comparator<int[]>{
		@Override
		public int compare(int[] o1, int[] o2) {
			return o1[2] - o2[2];
		}

	}
}
