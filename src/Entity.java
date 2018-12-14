
public class Entity {
	private Inventory inventory = new Inventory();
	public int health;
	public int attack;
	public int range;
	public Entity() {
		
	}
	public Inventory getInventory() {
		return inventory;
	}
}
