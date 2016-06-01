package vs.jonas.client.model.table.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
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
	private String[] columnNames = {"Name","Players"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse
	 * {id,name,numberOfPlayer}
	 */
	List<Place> fields;

	public static int NAME = 0;
	public static int PLAYERS = 1;
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
		case 0:	ergebnis = field.getName();	break;
		case 1: ergebnis = field.getPlayers();
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
		if (data != null) {
			fields = new ArrayList<>(data);
			Collections.sort(fields, new FieldsComparator());
			fireTableDataChanged();
		}
	}
	
	public Place getPlace(int row){
		return fields.get(row);
	}
	
	public JComboBox<String> getPlayersAsComboBox(int row){
		return (JComboBox<String>) getValueAt(row, PLAYERS);
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}
