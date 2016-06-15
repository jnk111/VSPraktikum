package vs.jonas.client.model.table.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import vs.jonas.client.json.PlayerInformation;
import vs.jonas.client.model.comparator.PlayerInformationComparator;

@SuppressWarnings("serial")
public class PlayerOverviewTableModel extends DefaultTableModel {

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "Spieler", "Kontostand", "Turn"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse
	 * {id,name,numberOfPlayer}
	 */
	List<PlayerInformation> players;

	/**
	 * Initialisiert das TableModel
	 */
	public PlayerOverviewTableModel() {
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
		case 1:	ergebnis = player.getAccount(); break;
		case 2: ergebnis = player.hasTurnMutex();
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
			Collections.sort(players, new PlayerInformationComparator());
			fireTableDataChanged();
		}
	}
	
	public PlayerInformation getPlayerInformation(int row){
		return players.get(row);	
	}
	
	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}
