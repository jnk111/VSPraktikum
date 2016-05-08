package vs.aufgabe2a.boardsservice.models.json;

import java.util.ArrayList;
import java.util.List;

public class JSONBoardList{
	
	List<String> boards;
	
	public JSONBoardList(){
		boards = new ArrayList<>();
	}
	
	
	public void addBoardURI(String uri){
		boards.add(uri);
	}
}
