package vs.aufgabe2a.boardsservice.models.json;

import java.util.ArrayList;
import java.util.List;

public class JSONPawnList {

		List<String> pawns;
		
		public JSONPawnList(){
			pawns = new ArrayList<>();
		}
		
		public void addPawnURI(String uri){
			
			pawns.add(uri);
		}
}
