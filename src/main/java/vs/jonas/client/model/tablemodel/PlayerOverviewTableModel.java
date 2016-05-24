package vs.jonas.client.model.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import vs.jonas.client.model.Player;
import vs.jonas.client.model.comparator.PlayerInformationComparator;

@SuppressWarnings("serial")
public class PlayerOverviewTableModel extends DefaultTableModel {

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "Name", "Figur", "Kontostand", "Ready"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse
	 * {id,name,numberOfPlayer}
	 */
	List<Player> players;

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
		Player player = players.get(row);
		Object ergebnis = null;

		switch (col) {
		case 0:
			ergebnis = player.getName();
			break;
		case 1:
			ergebnis = player.getPawn();
			break;
		case 2:
			ergebnis = player.getAccount();
			break;
		case 3: 
			ergebnis = player.isReady();
		}
		return ergebnis;
	}

	/**
	 * L�dt die Tabelle mit neuen Daten.
	 * 
	 * @param data
	 *            Die neuen Daten.
	 */
	public void loadData(List<Player> data) {
		System.out.println("Load PlayerInformationTable ...");
		if (data != null) {
			players = new ArrayList<>(data);
			Collections.sort(players, new PlayerInformationComparator());
			System.out.println(players);
			fireTableDataChanged();
		}
	}

}
