package entities;

public class Loot {
	private Inventory inventory = new Inventory();
	public void addItem(Item item) {
		inventory.addItem(item);
	}
	public Inventory getInventory() {
		return inventory;
	}
}
