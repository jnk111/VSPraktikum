package vs.jonas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class PlayerOverviewTableModel extends DefaultTableModel {

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "Name", "Figur", "Kontostand", "Ready"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameInformation
	 * {id,name,numberOfPlayer}
	 */
	List<PlayerInformation> playerInformations;

	/**
	 * Initialisiert das TableModel
	 */
	public PlayerOverviewTableModel() {
		playerInformations = new ArrayList<>();
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
		if (playerInformations == null) {
			return 0;
		}
		return playerInformations.size();
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
		PlayerInformation player = playerInformations.get(row);
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
	public void loadData(List<PlayerInformation> data) {
		if (data != null) {
			playerInformations = new ArrayList<>(data);
			Collections.sort(playerInformations, new PlayerInformationComparator());
			System.out.println(playerInformations);
			fireTableDataChanged();
		}
	}

}
