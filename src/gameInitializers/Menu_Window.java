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
	private JTextField textField;
	private JLabel PathMessage;
	private JButton btnLoadMap;
	private boolean ready = false;
	private Stack<Object> listenAbles = new Stack<Object>();
	public Menu_Window() {
		this.setSize(500,500);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
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
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		textField = new JTextField();
		add(textField, "8, 8, fill, default");
		textField.setColumns(10);
		
		PathMessage = new JLabel("Please select Map Name");
		add(PathMessage, "8, 10, left, default");
		btnLoadMap = new JButton("Load Map");
		btnLoadMap.setName("Load Button");
		listenAbles.add(btnLoadMap);
		add(btnLoadMap, "8, 14");
	}
	public boolean start() {
		return ready;
	}
	public String getTargetMap() {
		return textField.getText();
	}
	public Stack<Object> getListenAbles(){
		return listenAbles;
	}
	private class Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(btnLoadMap)){
				ready = true;
			}
		}
		
	}
}