package entities;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import tiles.Tile;

public class Player extends Entity{

	// Character Sizes. Irrelevant for future implementations
	private final int XSIZE = 50, YSIZE = 50;

	// The position of the character in the game
	private int xPos, yPos;


	// The Image for the Character
	private BufferedImage me;

	// Constructor for the character that starts at the given map location
	public Player(int x, int y){
		// Calls a method to set the location
		setLocation(x, y);

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

	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}

	// Method to try and move the character to the north
	public boolean moveUp(Tile[][] terrainMap) {
		try {
			if(terrainMap[yPos - 1][xPos].walkAble) {
				yPos--;
				return true;
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		return false;
	}
	// Method to try and move the character to the south
	public boolean moveDown(Tile[][] terrainMap) {
		if(terrainMap[yPos + 1][xPos].walkAble) {
			yPos++;
			return true;
		}
		return false;
	}
	// Method to try and move the character to the east
	public boolean moveRight(Tile[][] terrainMap) {
		if(terrainMap[yPos][xPos + 1].walkAble) {
			xPos++;
			return true;
		}
		return false;
	}
	// Method to try and move the character to the west
	public boolean moveLeft(Tile[][] terrainMap) {
		if(terrainMap[yPos][xPos - 1].walkAble) {
			xPos--;
			return true;
		}
		return false;
	}
}
