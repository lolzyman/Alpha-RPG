import java.lang.reflect.Constructor;

public class ClassIdentifier {
	private int classCode;
	private String className;
	private Constructor<?> tile;
	private String[] parameteres;
	private String[] ImageSets;
	
	public String[] getParameteres() {
		return parameteres;
	}
	public void setParameteres(String...parameteres) {
		this.parameteres = parameteres;
	}
	public String[] getImageSets() {
		return ImageSets;
	}
	public void setImageSets(String[] imageSets) {
		ImageSets = imageSets;
	}
	public void setImageSets(int[] imageSets) {
		ImageSets = new String[imageSets.length];
		for(int i = 0; i < imageSets.length; i++) {
			ImageSets[i] = imageSets[i] + "";
		}
	}
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
	public String getClassName() {
		return className;
	}
	public void setClassName(String input) {
		className = input;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ClassIdentifier) {
			ClassIdentifier that = (ClassIdentifier)obj;
			
			if(this.className.equals(that.className)) {
				String A = FileManager.compileArraytoString(parameteres);
				String B = FileManager.compileArraytoString(that.parameteres);
				if(A.equals(B)) {
					if(FileManager.compileArraytoString(this.ImageSets).equals(FileManager.compileArraytoString(that.ImageSets))) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
