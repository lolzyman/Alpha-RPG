import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;

public class FileManager {

	public static BufferedImage loadImage(String location) {
		try {
			return ImageIO.read(new File(location));
		} catch (IOException e) {
			return null;
		}
	}
	public static BufferedImage loadTileFromIndex(BufferedImage image, int index) {
		BufferedImage subImage;
		if(image != null) {
			int columns = (int)Math.floor(image.getWidth() / 50);
			int imageColumn = index % columns;
			int imageRow = (index - imageColumn) / columns;
			subImage = image.getSubimage(imageColumn * 50, imageRow * 50, 50, 50);
			return subImage;
		}else {
			return null;
		}
	}
	public static Tile[][] loadTerrainmap(String mapName){
		Tile[][] loadedMatrix;
		ClassIdentifier[] classIndex = FileManager.loadMapData("Maps/" + mapName + "/Data");
		BufferedImage mapTileSet = FileManager.loadImage("Maps/" + mapName + "/TileSet.png");
		int matrixIndex = 0;
		Queue<String> rows = new LinkedList<String>();
		if(classIndex != null)
		try {
			String line;
			BufferedReader fileReader = new BufferedReader(new FileReader("Maps/" + mapName + "/Terrain.csv"));
			while((line = fileReader.readLine()) != null) {
				rows.add(line);
			}
			fileReader.close();
			loadedMatrix = new Tile[rows.size()][];
			
			while(!rows.isEmpty()) {
				String[] tokens = rows.remove().split(",");
				loadedMatrix[matrixIndex] = new Tile[tokens.length];
				for(int i = 0; i < tokens.length; i++) {
					int classCode = Integer.parseInt(tokens[i]);
					if(classCode != -1) {
						ClassIdentifier targetClass = classIndex[classCode];
						Tile object = (Tile)targetClass.getTile().newInstance();
						object.addImageSet(mapTileSet,targetClass.getImageSets());
						object.mapX = i;
						object.mapY = matrixIndex;
						loadedMatrix[matrixIndex][i] = (Tile)object;
						object.Initialize(classIndex[classCode].getParameteres());
					}
				}
				matrixIndex++;
			}
			return loadedMatrix;
		} catch (Exception e) {
			System.out.print("Loader Didn't work: " + e);
			return null;
		}
		return null;
	}
	public static ClassIdentifier[] loadMapData(String location) {
		ClassIdentifier[] classInfo;
		Queue<String> classes = new LinkedList<String>();
		String line;
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(location));
			while((line = fileReader.readLine()) != null) {
				classes.add(line);
			}
			fileReader.close();
		}catch (Exception e) {
			System.out.println("The system coudn't open the map's data file" + e);
		}
		classInfo = new ClassIdentifier[classes.size()];
		while(!classes.isEmpty()) {
			String[] tokens = classes.remove().split(":");
			ClassIdentifier ident = new ClassIdentifier();
			
			//Find Class Code
			ident.setClassCode(Integer.parseInt(tokens[0]));
			
			//Load the Class Images
			
			Class<?> c = null;
			Constructor<?> cons = null;
			try {
				c = Class.forName(tokens[1]);
				cons = c.getConstructor();
			} catch (ClassNotFoundException e) {
				System.out.println("The map's data file classes are not created or mispelled:" + e);
			} catch (NoSuchMethodException e ) {
				System.out.println("The map's data file classes was not construced properly and the constructor couldn't be found:" + e);
			}
			ident.setTile(cons);
			
			String[] imageSets = tokens[2].split(",");
			ident.setImageSets(imageSets);
			
			String[] parameters = tokens[3].split(",");
			ident.setParameteres(parameters);

			classInfo[ident.getClassCode()] = ident;
		}
		return classInfo;
	}
}
