package vs.jonas.client.model.table;

import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import vs.jonas.client.model.table.renderer.GameFieldNameCellRenderer;
import vs.jonas.client.model.table.renderer.GameFieldPlayersCellRenderer;
import vs.jonas.client.model.table.tablemodel.GameFieldTableModel;

public class GameFieldTable extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameFieldTable(DefaultTableModel model){
		super(model);
		setRowHeight(100);
		setColumnSelectionAllowed(true);
		setCellSelectionEnabled(true);
	}

	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		   Object value = super.getValueAt(row, column);
		   if(value != null) {
		      if(column == GameFieldTableModel.PLAYERS && value instanceof List) {
		           return new GameFieldPlayersCellRenderer();
		      } else if(column == GameFieldTableModel.NAME && value instanceof String){
		    	  GameFieldNameCellRenderer renderer = new GameFieldNameCellRenderer();
		    	  renderer.setHorizontalAlignment(JLabel.CENTER);
		    	  return renderer;
		      } else{
		    	  return getDefaultRenderer(value.getClass());
		      }
		   } else{
			   return super.getCellRenderer(row, column);
		   }
	}

	/**
	 * Wird zurzeit nicht verwendet, da keine JComboBox verwendet wird.
	 */
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		Object value = super.getValueAt(row, column);
		if(value != null) {
			if(value instanceof JComboBox) {
				return new DefaultCellEditor((JComboBox<?>)value);
			} else{
				return getDefaultEditor(value.getClass());
			}
		} else{
			return super.getCellEditor(row, column);
		}
	}
}
