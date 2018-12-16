package entities;

public class Item {
	public static final int KEY = 0, WEAPON = 1, CONSUMABLE = 2, MATERIAL = 3;
	private int type;
	private String id;
	private int quantity;
	
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
	public void setQuantity(int newQuantity) {
		quantity = newQuantity;
	}
	public int getQuantity() {
		return quantity;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Item) {
			Item that = (Item)obj;
			if(this.type != that.type) {
				return false;
			}
			if(!this.id.equals(that.id)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
