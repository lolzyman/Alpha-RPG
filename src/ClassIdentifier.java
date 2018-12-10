import java.lang.reflect.Constructor;

public class ClassIdentifier {
	private int classCode;
	private Constructor<?> tile;
	private String[] parameteres;
	public String[] getParameteres() {
		return parameteres;
	}
	public void setParameteres(String[] parameteres) {
		this.parameteres = parameteres;
	}
	public String[] getImageSets() {
		return ImageSets;
	}
	public void setImageSets(String[] imageSets) {
		ImageSets = imageSets;
	}
	private String[] ImageSets;
	public Constructor<?> getTile() {
		return tile;
	}
	public void setTile(Constructor<?> tile) {
		this.tile = tile;
		
	}
	public int getClassCode() {
		return classCode;
	}
	public void setClassCode(int classCode) {
		this.classCode = classCode;
	}
}
