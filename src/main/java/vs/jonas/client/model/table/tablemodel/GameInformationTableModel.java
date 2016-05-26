package vs.jonas.client.model.table.tablemodel;

import vs.jonas.client.json.GameResponse;
import vs.jonas.client.model.comparator.GameNumberOfPlayerComparator;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
/**
 * Diese Klasse implementiert ein TableModel um Informationen ueber laufende Spiele anzuzeigen.
 * @author Jones
 */
public class GameInformationTableModel extends DefaultTableModel {

	/**
	 * Die Spalten-Namen
	 */
	private String[] columnNames = { "ID", "Name", "# of Players"};

	/**
	 * Die Daten, die angezeigt werden sollen: GameResponse {id,name,numberOfPlayer}
	 */
	List<GameResponse> gameResponses;

	/**
	 * Initialisiert das TableModel
	 */
	public GameInformationTableModel(){
		super();
		gameResponses = new ArrayList<GameResponse>();
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
	 * Liefert die Anzahl an Eintraegen
	 */
    @Override
	public int getRowCount() {
		if(gameResponses == null){
			return 0;
		}
		return gameResponses.size();
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
		GameResponse game = gameResponses.get(row);
		Object ergebnis = null;
		
		switch(col){
		case 0: ergebnis = game.getId(); break;
		case 1: ergebnis = game.getName(); break;
		case 2: ergebnis = game.getNumberOfPlayers();
		}
		
		return ergebnis;
	}

	public GameResponse getGameResponse(int row){
		return gameResponses.get(row);
	}

	/**
	 * Laedt die Tabelle mit neuen Daten.
	 * @param data Die neuen Daten.
	 */
	public void loadData(List<GameResponse> data){
		System.out.println("Load GameInformationTable ...");
		if(data != null){
			gameResponses = new ArrayList<>(data);
			Collections.sort(gameResponses,new GameNumberOfPlayerComparator());
			System.out.println(gameResponses);
			fireTableDataChanged();
		}
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}
