package vs.jonas.client.model.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;

public class GameFieldNameCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		GameFieldTableModel model = (GameFieldTableModel) table.getModel();
		
		String fieldName = model.getPlace(row).getName();
//		System.out.println("## Rendering Gamefield: " + fieldName);
		
		if(fieldName.equals("Badstrasse") || fieldName.equals("Turmstrasse")){
			label.setBackground(Color.MAGENTA);
		} else if(fieldName.equals("Chaussestrasse") || fieldName.equals("Elisenstrasse")
				|| fieldName.equals("Poststrasse")){
			label.setBackground(Color.CYAN);
		} else if(fieldName.equals("Seestrasse") || fieldName.equals("Hafenstrasse")
				|| fieldName.equals("Neuestrasse")){
			label.setBackground(Color.PINK);
		} else if(fieldName.equals("Muenchnerstrasse") || fieldName.equals("Wienerstrasse")
				|| fieldName.equals("Berlinerstrasse")){
			label.setBackground(Color.ORANGE);
		} else if(fieldName.equals("Theaterstrasse") || fieldName.equals("Museumstrasse")
				|| fieldName.equals("Opernplatz")){
			label.setBackground(Color.RED);
		} else if(fieldName.equals("Lessingstrasse") || fieldName.equals("Schillerstrasse")
				|| fieldName.equals("Goethestrasse")){
			label.setBackground(Color.YELLOW);
		} else if(fieldName.equals("Rathausplatz") || fieldName.equals("Hauptstrasse")
				|| fieldName.equals("Bahnhofstrasse")){
			label.setBackground(Color.GREEN);
		} else if(fieldName.equals("Parkstrasse") || fieldName.equals("Schlossallee")){
			label.setBackground(Color.BLUE);
		} else if(fieldName.equals("Suedbahnhof") || fieldName.equals("Westbahnhof")
				|| fieldName.equals("Nordbahnhof") || fieldName.equals("Hauptbahnhof")){
			label.setBackground(Color.LIGHT_GRAY);
		} else label.setBackground(new Color(204, 255, 204));
		return label;
	}

}
