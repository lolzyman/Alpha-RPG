package systemManagers;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import entities.Player;
import entities.Item;
import tiles.Tile;

public class FileManager {

	
	  //*********************************\\
	 //**************Loaders**************\\
	//*************************************\\
	public static Object[] loadCharacter(String characterName) {
		Object[] objects = new Object[2];
		Player loadedCharacter = null;
		
		File character = new File("Saves/" + characterName + ".rpgsave");
		if(character.exists()) {
			try {
			String[] charInfo = readCharFile(character);
			objects[0] = charInfo[0];
			String[] location = charInfo[1].split(",");
			int x = Integer.parseInt(location[0]);
			int y = Integer.parseInt(location[1]);
			loadedCharacter = new Player(x,y);
			loadedCharacter.health = Integer.parseInt(charInfo[2]);
			String[] items = new String[charInfo.length-3];
			for(int i = 3; i < charInfo.length;i++) {
				items[i-3] = charInfo[i];
			}
			initializeCharInventory(loadedCharacter,items);
			}catch(Exception e) {
				System.out.println("The Character save File is currpt");
			}
		}
		
		objects[1] = loadedCharacter;
		return objects;
	}
	public static void initializeCharInventory(Player character,String...strings) {
		for(String string:strings) {
			try {
				Item item = new Item();
				String[] itemInfo = string.split(",");
				item.setType(Integer.parseInt(itemInfo[0]));
				item.setID(itemInfo[1]);
				item.setQuantity(Integer.parseInt(itemInfo[2]));
				character.getInventory().addItem(item);
			}catch(Exception e) {
				System.out.println("Couldn't Load the Characters Item: " + e);
			}
		}
	}
	public static String[] readCharFile(File file) {
		String[] characterInformation = null;
		try {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		Queue<String> charData = new LinkedList<String>();
		while((line = reader.readLine()) != null) {
			String temp;
			if(!line.equals("")){
				temp = line.substring(0, 1);
			}else {
				temp = "/";
			}
			if(!temp.equals("/")  && !temp.equals("#") && !temp.equals("%") && !temp.equals("*"))
			charData.add(removeWhiteSpace(line));
		}
		characterInformation = new String[charData.size()];
		int index = 0;
		while(!charData.isEmpty()) {
			characterInformation[index] = charData.poll();
			index++;
		}
		
		}catch(Exception e) {
			System.out.println("The Character Loading didn't work: " + e);
		}
		return characterInformation;
	}
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
	public static BufferedImage[] loadTilesFromIndex(BufferedImage image) {
		BufferedImage[] subImages = null;
		try {
			if(image != null) {
				int columns = (int)Math.floor(image.getWidth() / 50);
				int rows = (int)Math.floor(image.getHeight() / 50);
				subImages = new BufferedImage[columns*rows];
				for(int i = 0; i< columns; i++) {
					for(int j = 0; j < rows; j++) {
						subImages[j*columns + i] = image.getSubimage(i * 50, j * 50, 50, 50);
					}
				}
			}
		}catch(Exception e) {
			System.out.println("FileManager");
			System.out.println("The row indes was");
		}
		return subImages;
	}
	public static Tile[][] loadTerrainmap(String mapName){
		Tile[][] loadedMatrix;
		ClassIdentifier[] classIndex = FileManager.loadMapClassIDs("Maps/" + mapName + "/ClassIDs");
		BufferedImage mapTileSet = FileManager.loadImage("Maps/" + mapName + "/TileSet.png");
		BufferedImage[] tiles = FileManager.loadTilesFromIndex(mapTileSet);
		int matrixIndex = 0;
		Queue<String> rows = new LinkedList<String>();
		if(classIndex != null)
		try {
			String line;
			BufferedReader fileReader = new BufferedReader(new FileReader("Maps/" + mapName + "/Terrain"));
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
						try {
							/*
							Tile object = new Tile();
							object.setImage(tiles[classCode]);
							loadedMatrix[matrixIndex][i] = object;
							*///*
							ClassIdentifier targetClass = classIndex[classCode];
							Tile object = (Tile)targetClass.getTile().newInstance();
							object.initializeImageSet(mapTileSet,targetClass.getImageSets());
							object.mapX = i;
							object.mapY = matrixIndex;
							loadedMatrix[matrixIndex][i] = (Tile)object;
							object.Initialize(classIndex[classCode].getParameteres());
							//*/
						}catch(Exception e) {
							System.out.println("FileManager");
							System.out.println("Missing Class in Map Data.\n" + "Class code: " + classCode + "\nException: " + e);
						}
					}else {
						loadedMatrix[matrixIndex][i] = new Tile();
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
	public static ClassIdentifier[] loadMapClassIDs(String location) {
		ClassIdentifier[] classInfo;
		Queue<String> classes = new LinkedList<String>();
		String line;
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(location));
			while((line = fileReader.readLine()) != null) {
				String temp;
				if(!line.equals("")){
					temp = line.substring(0, 1);
				}else {
					temp = "/";
				}
				if(!temp.equals("/")  && !temp.equals("#") && !temp.equals("%") && !temp.equals("*"))
				classes.add(removeWhiteSpace(line));
			}
			fileReader.close();
		}catch (Exception e) {
			System.out.println("The system coudn't open the map's data file" + e);
		}
		classInfo = new ClassIdentifier[classes.size()];
		while(!classes.isEmpty()) {
			String workingString = classes.remove();
			String[] tokens = workingString.split(":");
			ClassIdentifier ident = new ClassIdentifier();
			
			//Find Class Code
			ident.setClassCode(Integer.parseInt(tokens[0]));
			
			
			//Gets the Class Name
			ident.setClassName(tokens[1]);
			
			Class<?> c = null;
			Constructor<?> cons = null;
			try {
				c = Class.forName(ident.getClassName());
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

	
	  //********************************\\
	 //**************Savers**************\\
	//************************************\\
	
	public static boolean saveToCSV(int[][] data, String FileName,boolean append) {
		BufferedWriter fileWriter;
		try {
			fileWriter = new BufferedWriter(new FileWriter(FileName, append));
			for(int y = 0; y < data.length; y++) {
				fileWriter.write(data[y][0] + "");
				for(int x = 1; x < data[y].length; x++) {
					fileWriter.write("," + data[y][x]);
				}
				fileWriter.newLine();
			}
			fileWriter.close();
			return true;
		}catch(Exception e) {
			System.out.println("Couldn't Save File");
			return false;
		}
	}
	
	public static void saveTerrainMap(Tile[][] terrainMap, String mapName) {
		ClassIdentifier[] currentClasses = loadMapClassIDs("Maps/" + mapName + "/ClassIDs");
		ClassIdentifier[] newClasses = new ClassIdentifier[currentClasses.length];
		int[][] terrainData = new int[terrainMap.length][terrainMap[0].length];
		int newClassIDS = 0;
		for(int y = 0; y < terrainMap.length; y++) {
			for(int x = 0; x < terrainMap[y].length; x++) {
				Tile tile = terrainMap[y][x];
				if(!tile.getClass().toString().equals(new Tile().getClass().toString())) {
					ClassIdentifier newID = createClassIdentifier(tile);
					boolean isNew = true;
					for(ClassIdentifier id:currentClasses) {
						if(id.equals(newID)) {
							newID.setClassCode(id.getClassCode());
							isNew = false;
						}

					}
					for(ClassIdentifier id:newClasses) {
						if(id != null) {
							if(id.equals(newID)) {
								newID.setClassCode(id.getClassCode());
								isNew = false;
							}	
						}
					}
					if(isNew) {
						newID.setClassCode(currentClasses.length + newClassIDS);
						newClasses[newClassIDS] = newID;
						newClassIDS++;
					}
					terrainData[y][x] = newID.getClassCode();
				}else {
					terrainData[y][x] = -1;
				}
				

			}
		}
		if(!saveToCSV(terrainData, "Maps/" + mapName + "/Terrain", false)) {
			System.out.println("Failed to save the terrainData");
		}
		if(!saveToData(newClasses, "Maps/" + mapName + "/ClassIDs", true)) {
			System.out.println("Failled to save the Class Identifiers");
		}
	}
	
	public static boolean saveToData(ClassIdentifier[] identities, String targetMap, boolean append) {
		try {
			BufferedWriter dataOut = new BufferedWriter(new FileWriter(targetMap, append));
			for(ClassIdentifier ident: identities) {
				if(ident != null) {
					dataOut.newLine();
					dataOut.write(ident.getClassCode() + ":");
					dataOut.write(ident.getClassName() + ":");
					dataOut.write(compileArraytoString(ident.getImageSets()) + ":");
					dataOut.write(compileArraytoString(ident.getParameteres()) + ":");
				}
			}
			dataOut.close();
			return true;
		}catch(Exception e) {
			System.out.println("The Class data file couldn't be updated because of :" + e);
			return false;
		}
	}
	
	public static boolean saveImage(BufferedImage image,String location) {
		try {
			ImageIO.write(image, "png", new File(location));
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	  //************************************\\
	 //**************Assistants**************\\
	//****************************************\\
	/**
	 * Removes all the tabs and spaces from a string
	 * @param input
	 * @return output
	 */
	private static String removeWhiteSpace(String input) {
		return compileArraytoString(compileArraytoString(input.split("\t")).split(" "));
	}
	/**
	 * Takes an array of Strings and stitches them together
	 * @param strings
	 * @return output
	 */
	public static String compileArraytoString(String...strings) {
		String output = "";
		for(String string: strings) {
			output += string;
			output += ",";
		}
		return output.substring(0, output.length()-1);
	}
	/**
	 * Generates a class Identifier from a Tile input
	 * @param obj
	 * @return
	 */
	public static ClassIdentifier createClassIdentifier(Tile obj) {
		ClassIdentifier ident = new ClassIdentifier();
		ident.setClassName(obj.getClass().toString().substring(6));
		ident.setImageSets(obj.imageTileIndices);
		ident.setParameteres(obj.generateParameters());
		return ident;
	}
	  //*************************************\\
	 //****************Testers****************\\
	//*****************************************\\
	public static void test() {
		BufferedImage tileSet = FileManager.loadImage("Maps/Map1/TileSet.png");
		Graphics2D g = tileSet.createGraphics();
		g.setColor(new Color(0,0,0,256/2));
		g.fillRect(0, 0, tileSet.getWidth(), tileSet.getHeight());
		File imageStream = new File("Maps/Map1/CharacterMemory.png");
		try {
			ImageIO.write(tileSet, "png", imageStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		test();
	}
}
