import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class Tile {
	public boolean walkAble = true;
	public BufferedImage[] images;
	public int currentImage = 0;
	public int mapX;
	public int mapY;
	public Tile() {

	}
	public boolean addImageSet(BufferedImage mapTileSet, String...strings) {
		int i = 0;
		boolean succesful = true;
		images = new BufferedImage[strings.length];
		for(String string: strings) {
			String[] tokens = string.split(",");
			images[i] =  FileManager.loadTileFromIndex(mapTileSet, Integer.parseInt(tokens[0]));
			
			if(images[i] == null) {
				succesful = false;
				Graphics2D g2;
				BufferedImage newImage = new BufferedImage(50,50, BufferedImage.TYPE_INT_RGB);
				g2 = newImage.createGraphics();
				g2.setColor(Color.cyan);
				g2.drawRect(0, 0, 50, 50);
				images[i] = newImage;
			}
			i++;
		}
		return succesful;
	}
	public void Initialize(String...strings) {
		currentImage = Integer.parseInt(strings[0]);
		int intWalk = Integer.parseInt(strings[1]);
		if(intWalk == 0) {
			walkAble = false;
		} else {
			walkAble = true;
		}
	}
	public void drawTile(Graphics2D g2, int x, int y) {
		g2.drawImage(images[currentImage], x * 50, y * 50, null);
	}
}