package entities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
			break;
		}
	}
	public void removeItem(Item item) {
		if(hasKey(item.getID())) {
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
		bag.setVisible(true);
		JList list;
		JScrollPane scrollPane;
		String[] keyNames;
		Object[] keys = getKeys().toArray();
		keyNames = new String[keys.length];
		for(int i = 0; i < keys.length; i++) {
			keyNames[i] = ((Item)keys[i]).getID();
		}

		list = new JList<>(keyNames);
		list.setBackground(new Color(255,255,255,100));
		list.addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent event) {
						//test.removeItem((Item)keys[list.getSelectedIndex()]);
						System.out.println("valueChanged");
						//String[] keyNames;
						//Object[] keys = test.getKeys().toArray();
						//keyNames = new String[keys.length];
						//for(int i = 0; i < keys.length; i++) {
						//keyNames[i] = ((Item)keys[i]).getID();
						//}
						//list.setListData(keyNames);
						//list.repaint();
					}

				}
				);
		
		
		scrollPane = new JScrollPane(list);
//		scrollPane.setBackground(new Color(0,0,0,255));
		bag.addTab("keys", null, scrollPane, null);
		
		
		
		return bag;
	}
	public static void main(String[] args) {
	}
}
