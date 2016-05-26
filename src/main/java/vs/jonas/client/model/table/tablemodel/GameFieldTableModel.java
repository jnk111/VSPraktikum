package vs.jonas.client.model.table.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import vs.jonas.client.json.Place;
import vs.jonas.client.model.comparator.FieldsComparator;

public class GameFieldTableModel extends DefaultTableModel{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "ID", "Name", "Owner", "Value", 
			"Rent", "Cost", "Houses", "Hypocredit", "Players"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse
	 * {id,name,numberOfPlayer}
	 */
	List<Place> fields;

	/**
	 * Initialisiert das TableModel
	 */
	public GameFieldTableModel() {
		fields = new ArrayList<>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/**
	 * Liefert die Anzahl an Spalten
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Liefert die Anzahl an Eintr�gen
	 */
	@Override
	public int getRowCount() {
		if (fields == null) {
			return 0;
		}
		return fields.size();
	}

	/**
	 * Liefert den Namen der Spalte
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Liefert den Wert einer Zelle.
	 * 
	 * @param row
	 *            Die Reihe in der sich die Zelle befindet.
	 * @param col
	 *            DIe Spalte in der sich die Zelle befindet.
	 */
	public Object getValueAt(int row, int col) {
		Place field = fields.get(row);
		Object ergebnis = null;
		
		switch (col) {
		case 0: ergebnis = field.getID(); break;
		case 1:	ergebnis = field.getName();	break;
		case 2:	ergebnis = field.getOwner(); break;
		case 3: ergebnis = field.getValue(); break;
		case 4: ergebnis = field.getRent(); break;
		case 5: ergebnis = field.getCost(); break;
		case 6: ergebnis = field.getHouses(); break;
		case 7: ergebnis = field.getHypocredit(); break;
		case 8: ergebnis = field.getPlayersAsComboBox();
		}
		return ergebnis;
	}

	/**
	 * L�dt die Tabelle mit neuen Daten.
	 * 
	 * @param data
	 *            Die neuen Daten.
	 */
	public void loadData(List<Place> data) {
		System.out.println("Load PlayerInformationTable ...");
		if (data != null) {
			fields = new ArrayList<>(data);
			Collections.sort(fields, new FieldsComparator());
			System.out.println(fields);
			fireTableDataChanged();
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}
