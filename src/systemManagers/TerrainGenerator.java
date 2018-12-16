package systemManagers;
import java.awt.image.BufferedImage;
import java.io.File;

public class TerrainGenerator {
	public static boolean createDefaultMap(String mapName) {
		File newMapDir = new File("Maps/" + mapName);
		if(mapName.equals("Default")) {
			System.out.println("You cannot save as Default");
			return false;
		}
		if(newMapDir.exists()) {
			System.out.println("Save Already Exists");
			return false;
		}
		newMapDir.mkdir();
		int[][] terrainData = generateDefaultTerrainData(50,50);
		ClassIdentifier[] classIDs = FileManager.loadMapClassIDs("Maps/MasterClassIDs");
		BufferedImage DefaultTileSet = FileManager.loadImage("Maps/Default/TileSet.png");

		FileManager.saveImage(DefaultTileSet, "Maps/" + mapName + "/TileSet.png");
		FileManager.saveToData(classIDs,"Maps/" + mapName + "/ClassIDs", false);
		FileManager.saveToCSV(terrainData, "Maps/" + mapName + "/Terrain", false);
		return true;
		
	}
	public static int[][] generateDefaultTerrainData(int x, int y){
		int[][] newTerrainData = new int[y][x];
		for(int i = 0; i < y; i++) {
			for(int j = 0; j < x; j++) {
				newTerrainData[i][j] = 0;
			}
		}
		return newTerrainData;
	}
	public static void main(String[] args) {
		createDefaultMap("Map7");
	}
}
