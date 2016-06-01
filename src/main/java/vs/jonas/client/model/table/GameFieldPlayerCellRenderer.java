package vs.jonas.client.model.table;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import vs.jonas.client.json.Place;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;

public class GameFieldPlayerCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {
		GameFieldTableModel model = (GameFieldTableModel) table.getModel();

		Place place = model.getPlace(row);
		List<String> players = place.getPlayers();
		JComboBox<String> playersBox = new JComboBox<>();
		DefaultListCellRenderer renderer = new DefaultListCellRenderer(); 
		renderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		playersBox.setRenderer(renderer);
//		
		for(String player : players){
			playersBox.addItem(player);
		}
		
		return playersBox;
	}

}
