package vs.aufgabe2a.boardsservice.models.json;

import java.util.ArrayList;
import java.util.List;

public class JSONBoardList implements JSONObject {
	
	List<String> boards;
	
	public JSONBoardList(){
		boards = new ArrayList<>();
	}
	
	
	public void addBoardURI(String uri){
		boards.add(uri);
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
