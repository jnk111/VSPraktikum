package vs.jonas.client.model.table.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import vs.jonas.client.json.Place;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;

public class GameFieldPlayersCellRenderer implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {

		JTextArea textArea = new JTextArea();
		textArea.setBackground(new Color(204, 255, 204));
		GameFieldTableModel model = (GameFieldTableModel) table.getModel();
		Place place = model.getPlace(row);
		for(String s : place.getPlayers()){
			textArea.append(s+"\n");
		}
		return textArea;
	}
}
