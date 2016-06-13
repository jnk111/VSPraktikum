package vs.jonas.client.model.table.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import vs.jonas.client.json.PlayerInformation;

public class PlayerStartGameScreenModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "Spieler", "Ready"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse
	 * {id,name,numberOfPlayer}
	 */
	List<PlayerInformation> players;

	/**
	 * Initialisiert das TableModel
	 */
	public PlayerStartGameScreenModel() {
		players = new ArrayList<>();
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
		if (players == null) {
			return 0;
		}
		return players.size();
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
		PlayerInformation player = players.get(row);
		Object ergebnis = null;

		switch (col) {
		case 0:	ergebnis = player.getPawn(); break;
		case 1: ergebnis = player.isReady()+""; 
		}
		return ergebnis;
	}

	/**
	 * L�dt die Tabelle mit neuen Daten.
	 * 
	 * @param data
	 *            Die neuen Daten.
	 */
	public void loadData(List<PlayerInformation> data) {
		if (data != null) {
			players = new ArrayList<>(data);
			fireTableDataChanged();
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}
