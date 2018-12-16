package gameInitializers;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class Menu_Window extends JPanel{
	private static final long serialVersionUID = 1L;
	private JTextField mapName;
	private JLabel PathMessage;
	private JButton loadMapButton;
	private boolean ready = false;
	private Stack<Object> listenAbles = new Stack<Object>();
	private JTextField characterName;
	private JLabel loadCharacterInfo;
	private JButton loadCharacterButton;
	public Menu_Window() {
		this.setSize(500,500);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		mapName = new JTextField();
		add(mapName, "4, 8, fill, default");
		mapName.setColumns(10);
		
		PathMessage = new JLabel("Please select Map Name");
		add(PathMessage, "4, 10, left, default");
		
		loadMapButton = new JButton("Load Map");
		loadMapButton.setName("Load Button");
		listenAbles.add(loadMapButton);
		add(loadMapButton, "4, 14");
		
		characterName = new JTextField();
		add(characterName, "4, 18, fill, default");
		characterName.setColumns(10);
		
		loadCharacterInfo = new JLabel("Please Enter A Character Name");
		add(loadCharacterInfo, "4, 20");
		
		loadCharacterButton = new JButton("New button");
		loadCharacterButton.setName("Character Load Button");
		listenAbles.add(loadCharacterButton);
		add(loadCharacterButton, "4, 24");
	}
	public boolean start() {
		return ready;
	}
	public String getTargetMap() {
		return mapName.getText();
	}
	public String getTargetCharacter() {
		return characterName.getText();
	}
	public Stack<Object> getListenAbles(){
		return listenAbles;
	}
}