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

import entities.Item;
import entities.Loot;
import entities.Player;
import tiles.Tile;

public class FileManager {

	
	  //*********************************\\
	 //**************Loaders**************\\
	//*************************************\\
	
	/**
	 * Loads the character from the file Char.rpgsave under the
	 * directory of the unique character ID
	 * @param characterName
	 * @return
	 */
	public static Object[] loadCharacter(String characterName) {
		Object[] objects = new Object[2];
		Player loadedCharacter = null;
		
		File character = new File("Saves/" + characterName);
		if(character.exists()) {
			File charData = new File("Saves/" + characterName + "/Char.rpgsave");
			try {
			String[] charInfo = readCharFile(charData);
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
			loadedCharacter.setName(characterName);
			initializeCharInventory(loadedCharacter,items);
			}catch(Exception e) {
				System.out.println("The Character save File is currpt");
			}
		}
		
		objects[1] = loadedCharacter;
		return objects;
	}
	
	/**
	 * Returns the lines of strings from the character loading file
	 * @param file
	 * @return
	 */
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
		reader.close();
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
		int matrixIndex = 0;
		BufferedImage lootIcon = FileManager.loadImage("TexturePack/Loot Icon.png");
		String[] fileLines = loadFileLines("Maps/" + mapName + "/Terrain");
		
		
		if(classIndex != null){
			loadedMatrix = new Tile[fileLines.length][];
			for(String string: fileLines) {
				String[] tokens = string.split(",");
				loadedMatrix[matrixIndex] = new Tile[tokens.length];
				for(int i = 0; i < tokens.length; i++) {
					int classCode = Integer.parseInt(tokens[i]);
					if(classCode != -1) {
						try {
							ClassIdentifier targetClass = classIndex[classCode];
							Tile object = (Tile)targetClass.getTile().newInstance();
							object.initializeImageSet(mapTileSet,targetClass.getImageSets());
							object.mapX = i;
							object.mapY = matrixIndex;
							loadedMatrix[matrixIndex][i] = (Tile)object;
							object.Initialize(classIndex[classCode].getParameteres());
							object.setLootIcon(lootIcon);
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
		}
		return null;
	}
	
	public static void loadMapItems(Tile[][] terrainMap, String mapName) {
		
		String[] fileLines = loadFileLines("Saves/" + mapName + "Items");
		if(fileLines != null) {
			for(String line : fileLines) {
				int x;
				int y;
				int itemType;
				String itemID;
				int itemQuantity;
				String[] tokens = line.split(":");
				
				//Loads the position of the Item
				String[] location = tokens[0].split(",");
				x = Integer.parseInt(location[0]);
				y = Integer.parseInt(location[1]);
				
				
				String[] itemInfo = tokens[1].split(",");
				//Loads the type of the Object
				itemType = Integer.parseInt(itemInfo[0]);
				
				//Loads the ID of the Object
				itemID = itemInfo[1];
				
				//Loads the Quantity of the Object
				itemQuantity = Integer.parseInt(itemInfo[2]);
				
				//Creates the Object
				Item item = new Item();
				item.setID(itemID);
				item.setQuantity(itemQuantity);
				item.setType(itemType);
				
				//Loads the item into the map
				terrainMap[y][x].addLoot(item);				
			}
		}
		
	}
	
	public static ClassIdentifier[] loadMapClassIDs(String location) {
		ClassIdentifier[] classInfo;
		String[] fileLines = loadFileLines(location);
		classInfo = new ClassIdentifier[fileLines.length];
		for(String workingString: fileLines) {
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

	public static int[][] loadMemory(String characterID, String mapName){
		int[][] memory = null;
		String fileName = "Saves/" + characterID + "/" + mapName + ".data";
		String[] lines = loadFileLines(fileName);
		memory = new int[lines.length][];
		for(int index = 0; index < lines.length; index++) {
			String[] numbers = lines[index].split(",");
			memory[index] = new int[numbers.length];
			for(int i = 0; i < numbers.length; i++) {
				memory[index][i] = Integer.parseInt(numbers[i]); 
			}
		}
		return memory;
	}
		
	public static String[] loadFileLines(String targetFile) {
		String[] tokens = null;
		BufferedReader fileReader;
		try {
			fileReader = new BufferedReader(new FileReader(targetFile));
			Queue<String> fileLines = new LinkedList<String>();
			String line;
			while((line = fileReader.readLine()) != null) {
				fileLines.add(line);
			}
			tokens = new String[fileLines.size()];
			int index = 0;
			while(!fileLines.isEmpty()) {
				tokens[index] = fileLines.poll();
				index++;
			}
			fileReader.close();
		}catch(Exception e) {
			System.out.println("FileManager: Load Map Items");
			System.out.println("The exception is: " + e);
		}
		return tokens;
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
		Queue<Tile> specialTiles = new LinkedList<Tile>();
		int[][] terrainData = new int[terrainMap.length][terrainMap[0].length];
		String itemSaveLocation = "Maps/" + mapName + "/Items";
		initializeFile(itemSaveLocation);
		int newClassIDS = 0;
		boolean append = false;
		for(int y = 0; y < terrainMap.length; y++) {
			for(int x = 0; x < terrainMap[y].length; x++) {
				Tile tile = terrainMap[y][x];
				Loot loot;
				if((loot = tile.getLoot()) != null) {
					Queue<Item> items = loot.getInventory().removeAllInventory();
					 saveMapItems(itemSaveLocation, tile.mapX, tile.mapY,items, append);
					 append = true;
				}else if(true){
					
				}
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
	
	public static boolean saveImage(BufferedImage image, String location) {
		try {
			ImageIO.write(image, "png", new File(location));
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveCharacterMemeory(String characterName, String mapName, int[][] characterMemory) {
		String targetFile = "Saves/" + characterName + "/" + mapName + ".data";
		saveToCSV(characterMemory, targetFile, false);
		return true;
	}
	
	public static boolean saveCharacter(Player character, String mapName) {
		BufferedWriter fileWriter;
		try {
			String targetFile = "Saves/" + character.getUniqueID() + "/Char.rpgsave";
			fileWriter = new BufferedWriter(new FileWriter(targetFile,false));

			//Writes the current map name of where the character is
			fileWriter.write(mapName);
			fileWriter.newLine();
			
			//Writes the current location of the character in the map;
			fileWriter.write("" + character.getxPos() + "," + character.getyPos());
			fileWriter.newLine();

			//Writes the health of the character
			fileWriter.write("" + character.getHealth());
			fileWriter.newLine();

			//Writes the characters inventory to a save file
			Queue<Item> items = character.getInventory().removeAllInventory();
			saveItems(fileWriter, items);
			
			fileWriter.close();
			return true;
		}catch(Exception e) {
			System.out.println("File Manager: Save Character");
			System.out.println("The excpetion was:" + e);
			return false;
		}
	}

	public static void saveItems(BufferedWriter fileWriter, Queue<Item> items) {
		try {
			for(Item item: items) {
				fileWriter.write("" + item.getType() +"," + item.getID() + "," + item.getType());
				fileWriter.newLine();
			}
		}catch(Exception e) {
			System.out.println("FileManager: Save Items");
			System.out.println("The Exception is: " + e);
		}
	}
	
	public static void saveMapItems(String location, int mapX, int mapY,Queue<Item> items, boolean append) {
		BufferedWriter fileWriter;
		try {
			fileWriter = new BufferedWriter(new FileWriter(location, append));
			for(Item item: items) {
				fileWriter.write(mapX + "," + mapY + ":" + item.getType() + "," + item.getID() + "," + item.getType());
				fileWriter.newLine();
			}
			fileWriter.close();
		}catch(Exception e) {
			System.out.println("FileManager: Save Map Items");
			System.out.println("The Exception is: " + e);
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

	
	public static void initializeFile(String fileName){
		try {
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName));
		fileWriter.close();
		}catch(IOException e) {
			System.out.println("FileManager: Initialize File");
			System.out.println("The Exception is: " + e);
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
	
	public static void addItemsToMap(Tile[][] terrainMap, String...strings) {
		for(String string:strings) {
			try {
				String[] itemInfo = string.split(":");
				if(itemInfo[1].equals("testItem")) {
					Item item = new Item();
					String[] itemStats = itemInfo[2].split(",");
					if(itemStats[0].toLowerCase().equals("key")) {
						item.setType(Item.KEY);
					}
					item.setID(itemStats[1]);
					item.setQuantity(Integer.parseInt(itemStats[2]));
					String[] itemLocation = itemInfo[0].split(",");
					terrainMap[Integer.parseInt(itemLocation[1])][Integer.parseInt(itemLocation[0])].addLoot(item);
					
				}
				
			}catch(Exception e) {
				System.out.println("Couldn't Load the Characters Item: " + e);
			}
		}
	}
	
	public static void generateCharacterMemoryForTileSet(String targetMap) {
		BufferedImage mapTileSet = loadImage("Maps/" + targetMap + "/TileSet.png");
		Graphics2D g = mapTileSet.createGraphics();
		g.setColor(new Color(0,0,0,256/2));
		g.fillRect(0, 0, mapTileSet.getWidth(), mapTileSet.getHeight());
		saveImage(mapTileSet, "Maps/" + targetMap + "/CharacterMemory.png");
	}
	
	  //*************************************\\
	 //****************Testers****************\\
	//*****************************************\\
	public static void test() {
		
	}
	

	public static void main(String[] args) {
		FileManager.generateCharacterMemoryForTileSet("Map2");
	}
}
