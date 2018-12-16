package entities;

public class Item {
	public static final int KEY = 0, WEAPON = 1, CONSUMABLE = 2, MATERIAL = 3;
	private int type;
	private String id;
	
	public String getID() {
		return id;
	}
	public int getType() {
		return type;
	}
	public void setID(String id) {
		this.id = id;
	}
	public void setType(int type) {
		this.type = type;
	}
}
