package vs.jonas.client.model.tablemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import vs.jonas.client.model.GameInformation;
import vs.jonas.client.model.comparator.GameInformationComparator;

@SuppressWarnings("serial")
/**
 * Diese Klasse implementiert ein TableModel um Informationen �ber laufende Spiele anzuzeigen.
 * @author Jones
 */
public class GameInformationTableModel extends DefaultTableModel {

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "ID", "Name", "# of Players"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameInformation {id,name,numberOfPlayer}
	 */
	List<GameInformation> gameInformations;

	/**
	 * Initialisiert das TableModel
	 */
	public GameInformationTableModel(){
		super();
		gameInformations = new ArrayList<GameInformation>();
	}
	
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
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
		if(gameInformations == null){
			return 0;
		}
		return gameInformations.size();
	}

	/**
	 * Liefert den Namen der Spalte
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Liefert den Wert einer Zelle.
	 * @param row Die Reihe in der sich die Zelle befindet.
	 * @param col DIe Spalte in der sich die Zelle befindet.
	 */
	public Object getValueAt(int row, int col) {
		GameInformation game = gameInformations.get(row);
		Object ergebnis = null;
		
		switch(col){
		case 0: ergebnis = game.getId(); break;
		case 1: ergebnis = game.getName(); break;
		case 2: ergebnis = game.getNumberOfPlayers();
		}
		
		return ergebnis;
	}
	
	/**
	 * L�dt die Tabelle mit neuen Daten.
	 * @param data Die neuen Daten.
	 */
	public void loadData(List<GameInformation> data){
		if(data != null){
			gameInformations = new ArrayList<>(data);
			Collections.sort(gameInformations,new GameInformationComparator());
			System.out.println(gameInformations);
			fireTableDataChanged();
		}
	}

}
