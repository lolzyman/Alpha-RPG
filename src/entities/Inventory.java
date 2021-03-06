package entities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Inventory {
	private LinkedList<Item> keys;
	private LinkedList<Item> materials;
	public boolean hasKey(Item item) {
		for(Item entry: keys) {
			if(entry.getID().equals(item.getID())) {
				return true;
			}
		}
		return false;
	}
	public Item getKey(Item item) {
		for(Item entry : keys) {
			if(entry.equals(item)) {
				return entry;
			}
		}
		return null;
	}
	public boolean hasKeys() {
		if(keys != null && keys.isEmpty()) {
			return false;
		}
		return true;
	}
	public boolean hasWeapon() {
		return false;
	}
	public boolean hasConsumable() {
		return false;
	}
	public Item[] listToArray(LinkedList<Item> list) {
		Item[] items = null;
		if(list != null) {
			Object[] objects = list.toArray();
			items = new Item[objects.length];
			for(int i = 0; i < objects.length;i++) {
				items[i] = (Item)objects[i];
			}
		}
		return items;
	}
	public Item[][] removeAllInventoy() {
		Item[][] items = new Item[2][];
		items[0] = listToArray(keys);
		items[1] = listToArray(materials);
		keys = null;
		materials = null;
		return items;
	}
	public Queue<Item> removeAllInventory(){
		Queue<Item> linkedList = new LinkedList<Item>();
		if(keys != null) {
			for(Item item : keys) {
				linkedList.add(item);
			}
		}
		if(materials != null) {
			for(Item item:materials) {
				linkedList.add(item);
			}
		}
		return linkedList;
	}
	public void merge(Inventory that) {
		Item[][] allItems = that.removeAllInventoy();
		for(Item[] itemType: allItems) {
			if(itemType != null) {
				for(Item item: itemType) {
					this.addItem(item);
				}
			}
		}
	}
	public void addItem(Item item) {
		switch(item.getType()) {
		case Item.KEY:
			if(keys == null) {
				keys = new LinkedList<Item>();
			}
			if(hasKey(item)) {
				
			}
			keys.add(item);
			break;
		case Item.MATERIAL:
			if(materials == null) {
				materials = new LinkedList<Item>();
			}
			materials.add(item);
			break;
		}
	}
	public void removeItem(Item item) {
		if(hasKey(item)) {
			keys.remove(item);
		}
	}
	public LinkedList<Item> getKeys(){
		return keys;
	}
	
	public JPanel generateTestPanel() {
		JPanel testPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setColor(Color.RED);
				g2.fillRect(50, 50, 50, 50);
			}
		};
		
		return testPanel;
	}
	public JTabbedPane getInventoryScreen() {
		
		JTabbedPane bag = new JTabbedPane(JTabbedPane.TOP);
		String[] keyNames;
		Object[] keys = getKeys().toArray();
		keyNames = new String[keys.length];
		for(int i = 0; i < keys.length; i++) {
			keyNames[i] = ((Item)keys[i]).getID();
		}
//		
//		String[] materialNames;
//		Object[] materials = getKeys().toArray();
//		keyNames = new String[keys.length];
//		for(int i = 0; i < keys.length; i++) {
//			keyNames[i] = ((Item)keys[i]).getID();
//		}

		JList list = new JList<>(keyNames);		
		JScrollPane keyScrollPane = new JScrollPane(list);
//		JScrollPane testScrollPane = new JScrollPane(list);

		
		bag.addTab("keys", null, keyScrollPane, null);
//		bag.addTab("Test", null, testScrollPane, null);
		return bag;
	}
//	public static void main(String[] args) {}
}
