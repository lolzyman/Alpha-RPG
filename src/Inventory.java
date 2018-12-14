import java.util.LinkedList;

public class Inventory {
	private LinkedList<Item> keys = new LinkedList<Item>();
	
	public boolean hasKey(String id) {
		for(Item item: keys) {
			if(item.getID().equals(id)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasWeapon() {
		return false;
	}
	public boolean hasConsumable() {
		return false;
	}
	public void addItem(Item item) {
		switch(item.getType()) {
		case Item.KEY:
			keys.add(item);
			System.out.println("I got a key!");
			break;
		}
	}
	public LinkedList<Item> getKeys(){
		return keys;
	}
}
