import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Game_Window extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Character me = new Character(1,1);
	private Tile[][] terrainMap;
	private int screenX = 6, screenY = 6;
	private WheatFarm[] wheatFarms = new WheatFarm[2];
	private int wheatFarmIndex = 0;
	private BufferedImage background = null;
	public Game_Window(int keyArray) {
		this.setSize(10000,10000);
		this.setVisible(true);
		initTerrainMap();
		background = FileLoader.loadImage("Resources/Working Map 1.png");
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		//Draws Background
		g2.setColor(Color.GREEN);
		g2.fillRect(0, 0, 550, 550);

		//Draws the Terrain
		if(background != null) {
			drawBackground(g2);
		}else {
			drawTerrainMap(g2, screenX, screenY);
		}
		for(WheatFarm farm: wheatFarms) {
			if(farm != null) {
				farm.drawFarm(g2, screenX, screenY);
			}
		}
		
		me.paint(g2, screenX, screenY);
		
		//Draws a shop menu
		if(me.getTile() instanceof Shop) {
			g2.setColor(new Color(0,0,0,256/2 - 1));
			g2.fillRect(0, 0, 500, 500);
		}
		
	}
	public void drawBackground(Graphics2D g2) {
		g2.drawImage(background, (6 - screenX)*50, (6 - screenY) * 50,null);
	}
	public void initTerrainMap() {
		Tile[][] beta = FileLoader.loadTerrainmap("Resources/Working Map 1.csv");
		int[][] map = {	{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
						{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		Tile[][] terrain = new Tile[map.length][];
		for(int i = 0; i < map.length; i++) {
			terrain[i] = new Tile[map[i].length];
			for(int j = 0; j < map[i].length; j++) {
				switch(map[i][j]) {
				case 0:
					terrain[i][j] = new Grass();
					break;
				case 1:
					terrain[i][j] = new Wall();
					break;
				case 2:
					terrain[i][j] = new Gold();
					break;
				case 3:
					terrain[i][j] = new WheatFarm(i, j);
					break;
				case 4:
					terrain[i][j] = new Shop();
					break;
				}
			}
		}
		terrainMap = terrain;
	}

	public void drawTerrainMap(Graphics2D g2, int x, int y) {
		for(int i = 0; i < 11; i++) {
			for(int j = 0; j < 11; j++) {
				if(terrainMap[y + j - 6][x + i - 6] != null) {
					switch(terrainMap[y + j - 6][x + i - 6].type) {
					case 0:
						//This is an empty tile
						break;
					case 1:
						// This is the tile for the default wall tile
						g2.setColor(Color.black);
						g2.fillRect(i*50, j*50, 50, 50);
						break;
					case 2:
						// This is the tile for Gold
						g2.setColor(Color.YELLOW);
						g2.fillRect(i*50, j*50, 50, 50);
						break;
					case 3:
						// This is the Default tile for a wheatFarm
						g2.setColor(Color.DARK_GRAY);
						g2.fillRect(i*50, j*50, 50, 50);
						break;
					case 4:
						g2.setColor(Color.red);
						g2.fillRect(i*50, j*50, 50, 50);
						break;
					}
				}
			}
		}
	}
	public void interactWithTile() {
		int x = me.xPos;
		int y = me.yPos;

		switch(terrainMap[y][x].type) {
		case 2:
			terrainMap[y][x].type = 0;
			me.gold++;
			System.out.println("The player has " + me.gold + " gold!");
			break;
		case 3:
			WheatFarm farm = (WheatFarm)terrainMap[y][x];
			me.wheat +=  farm.wheat;
			farm.wheat = 0;
			System.out.println("The player has " + me.wheat + " wheat!");
			break;
		}
	}
	public void updateGame(double currentFPS, int keyarray) {
		//Handles Key Events

		switch(keyarray) {
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
		case 66:
			buildWheatFarm();
		default:
			break;
		}
		repaint();

	}
	public void loadLevel() {
		
	}
	public void updateMovement() {
		me.clearMove();
		moveScreen();
		int updated = 0;
		for(WheatFarm farm: wheatFarms) {
			if(farm != null) {
				farm.updateFarm(1);
				updated++;
			}
		}
	}
	public void buildWheatFarm() {
		int x = me.xPos;
		int y = me.yPos;
		if(terrainMap[y][x] instanceof Grass) {
			terrainMap[y][x] = addWheatFarm(wheatFarmIndex, x, y);
			wheatFarmIndex++;
		}
	}
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
	
	public WheatFarm addWheatFarm(int index, int x, int y) {
		if(wheatFarms.length == index) {
			WheatFarm[] newArray = new WheatFarm[(index)*2];
			for(int i = 0; i < index; i++) {
				newArray[i] = wheatFarms[i];
			}
			WheatFarm newFarm = new WheatFarm(x, y);
			newArray[index] = newFarm;
			wheatFarms = newArray;
			return newFarm;
		}
		WheatFarm newFarm = new WheatFarm(x, y);
		wheatFarms[index] = newFarm;
		return newFarm;
	}

	private class Character {
		private final int XSIZE = 50, YSIZE = 50;
		private int xPos, yPos;
		private boolean moved = false;
		private int gold = 0;
		private int wheat = 0;
	
	
	
		public Character(int x, int y){
			setLocation(x, y);
		}
		public int[] getLocation() {
			int[] local = {xPos, yPos};
			return local;
		}
		public void paint(Graphics2D g2, int x, int y) {
			g2.setColor(Color.GRAY);
			g2.fillOval((xPos - x + 6)*50, (yPos - y + 6)*50, XSIZE, YSIZE);
	
		}
		public Tile getTile() {
			return terrainMap[yPos][xPos];
		}
		public void setLocation(int x, int y) {
			xPos = x;
			yPos = y;
		}
		public void clearMove() {
			moved = false;
		}
		public void moveUp() {
			if(!moved) {
				if(terrainMap[yPos - 1][xPos].walkAble) {
					yPos--;
					interactWithTile();
					moved = true;
				}
			}
	
		}
		public void moveDown() {
			if(!moved) {
				if(terrainMap[yPos + 1][xPos].walkAble) {
					yPos++;
					interactWithTile();
					moved = true;
				}
			}
		}
		public void moveRight() {
			if(!moved) {
				if(terrainMap[yPos][xPos + 1].walkAble) {
					xPos++;
					interactWithTile();
					moved = true;
				}
			}
		}
		public void moveLeft() {
			if(!moved) {
				if(terrainMap[yPos][xPos - 1].walkAble) {
					xPos--;
					interactWithTile();
					moved = true;
				}
			}
		}
	}

	private class Item{
		
	}

	private class Tile {
		public int type = 0;
		public boolean walkAble = false;
		public Tile() {
	
		}
	}

	private class Inventory {
		private int gold = 0;
		
		private int wheat = 100;
		private int wheatSellPrice = 10;
		private int wheatBuyPrice = 5;
		
		private int forestSeed = 100;
		private int forestSeedSellPrice = 50;
		private int forestSeedBuyPrice = 50;
		
		private int wood = 100;
		private int woodSellPrice = 40;
		private int woodBuyPrice = 20;
		
		public Inventory() {
			
		}
		public boolean sellWheat(int quantity) {
			if(wheat >= quantity) {
				wheat -= quantity;
				gold += quantity * wheatSellPrice;
				return true;
			}
			return false;
		}
		public int getWheatSellPrice() {
			return wheatSellPrice;
		}
		public int getWheatBuyPrice() {
			return wheatBuyPrice;
		}
		public boolean buyWheat(int quantity) {
			if(gold >= quantity * wheatBuyPrice) {
				gold -= quantity * wheatBuyPrice;
				wheat += quantity;
				return true;
			}
			return false;
			
		}
		public void buyWheat() {
			
		}
		public void sellForestSeed() {
			
		}
		public void buyForestSeed() {
			
		}
	}

	private class Grass extends Tile {
		public Grass() {
			this.type = 0;
			this.walkAble = true;
		}
	}

	private class Gold extends Tile{
		private int value = 0;
		public Gold() {
			this.type = 2;
			this.walkAble = true;
		}
	}
	private class Wall extends Tile{
		private int value = 0;
		public Wall() {
			this.type = 1;
			this.walkAble = false;
		}
	}
	private class WheatFarm extends Tile{
		private int wheat = 0;
		private int maxWheat = 50;
		private int xPos;
		private int yPos;

		public WheatFarm(int x, int y) {
			this.type = 3;
			this.walkAble = true;
			xPos = x;
			yPos = y;
		}
		public void updateFarm(int i) {
			for(;i > 0;i--) {
				if(wheat < maxWheat)wheat++;
			}
		}
		public void drawFarm(Graphics2D g2, int x, int y) {
			g2.setColor(new Color(255,255,0,255/maxWheat*wheat));
			g2.fillRect((xPos - x + 6)*50, (yPos - y + 6)*50, 50, 50);
		}
	}
	private class Shop extends Tile{
		private Inventory shopInventory = new Inventory();
		
		public Shop() {
			this.walkAble = true;
			this.type = 4;
			shopInventory.gold = 500;
		}
	}
	private class Forest extends Tile{

	}
	private static class FileLoader {

		
		public static BufferedImage loadImage(String location) {
			try {
				return ImageIO.read(new File(location));
			} catch (IOException e) {
				return null;
			}
		}
		public static Tile[][] loadTerrainmap(String location){
			Tile[][] loadedMatrix;
			Stack<String> rows= new Stack();
			try {
				String line;
				BufferedReader fileReader = new BufferedReader(new FileReader(location));
				while((line = fileReader.readLine()) != null) {
					rows.add(line);
					System.out.println("The size of Stack is: " + rows.size());
				}
				loadedMatrix = new Tile[rows.size()][];
				while(!rows.isEmpty()) {
					String[] tokens = rows.pop().split(",");
					int[] row = new int[tokens.length];
				}
			} catch (Exception e) {
				return null;
			}
			return null;
		}
	}

}
