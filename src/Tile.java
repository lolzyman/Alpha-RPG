import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;


public class Tile {
	public boolean walkAble = true;
	public BufferedImage[] images;
	public int[] imageTileIndices;
	public int currentImage = 0;
	public int mapX;
	public int mapY;
	private boolean seen = false;
	private boolean transparent = false;
	private Inventory inventory = new Inventory();
	public Tile() {

	}
	public Inventory getInventory() {
		return inventory;
	}
	public boolean initializeImageSet(BufferedImage mapTileSet, String...strings) {
		int i = 0;
		boolean succesful = true;
		images = new BufferedImage[strings.length];
		imageTileIndices = new int[strings.length];
		for(String string: strings) {
			int index = Integer.parseInt(string);
			images[i] = FileManager.loadTileFromIndex(mapTileSet, index);
			imageTileIndices[i] = index;
			if(images[i] == null) {
				succesful = false;
				Graphics2D g2;
				BufferedImage newImage = new BufferedImage(50,50, BufferedImage.TYPE_INT_RGB);
				g2 = newImage.createGraphics();
				g2.setColor(Color.cyan);
				g2.fillRect(0, 0, 50, 50);
				images[i] = newImage;
			}
			i++;
		}
		return succesful;
	}
	public void Initialize(String...strings) {
		currentImage = Integer.parseInt(strings[0]);
		if(strings[1].equals("walkable")) {
			walkAble = true;
		} else {
			walkAble = false;
		}
		if(strings[2].equals("seen")) {
			seen = true;
		}else {
			seen = false;
		}
		if(strings[3].equals("transparent")) {
			transparent = true;
		}else {
			transparent = false;
		}
	}
	public void setSeen() {
		seen = true;
	}
	public boolean getSeen() {
		return seen;
	}
	public boolean getTransparent() {
		return transparent;
	}
	public void drawTile(Graphics2D g2, int x, int y) {
		if(seen) {
			try {
				g2.drawImage(images[currentImage], x * 50, y * 50, null);
			}catch(NullPointerException e) {
				BufferedImage[] newImages = new BufferedImage[currentImage+1];
				BufferedImage newImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = newImage.createGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(x * 50, y * 50, 50, 50);
				newImages[currentImage] = newImage;
				images = newImages;
				g2.drawImage(images[currentImage], x * 50, y * 50, null);
			}catch(Exception e) {
				System.out.println("Error in trying to paint a Tile. Go to the Class Tile.\nThe exception: " + e);
			}
		}else {
			g2.setColor(Color.BLACK);
			g2.fillRect(x * 50, y * 50, 50, 50);
		}
	}
	public String[] generateParameters() {
		String[] para = new String[4];
		para[0] = currentImage + "";
		if(walkAble) {
			para[1] = "walkable";
		}else {
			para[1] = "unwalkable";
		}
		if(getSeen()) {
			para[2] = "seen";
		}else {
			para[2] = "unseen";
		}
		if(getTransparent()) {
			para[3] = "transparent";
		}else {
			para[3] = "nottransparent";
		}
		return para;
	}
	public void interact(Entity being) {
		LinkedList<Item> keys = inventory.getKeys();
		while(!keys.isEmpty()) {
			being.getInventory().addItem(keys.pollFirst());
		}
	}
}