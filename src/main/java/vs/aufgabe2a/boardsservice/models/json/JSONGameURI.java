package vs.aufgabe2a.boardsservice.models.json;

public class JSONGameURI {
	
	private String game;
	
	public JSONGameURI(){
		
	}
	
	public JSONGameURI(String gameUri){
		
		this.game = gameUri;
	}

	public String getURI() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
