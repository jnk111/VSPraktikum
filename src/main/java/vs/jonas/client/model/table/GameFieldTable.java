package vs.jonas.client.model.table;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class GameFieldTable extends JTable{

	public GameFieldTable(DefaultTableModel model){
		super(model);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
	   Object value = super.getValueAt(row, column);
	   if(value != null) {
	      if(value instanceof JComboBox) {
	           return new DefaultCellEditor((JComboBox)value);
	      }
	            return getDefaultEditor(value.getClass());
	   }
	   return super.getCellEditor(row, column);
	}
}
