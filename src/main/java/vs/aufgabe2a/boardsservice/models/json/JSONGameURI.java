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

	/**
	 * TODO: Pruefung implementieren ob uebergebenes JSONGameUri gueltig
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	public boolean isValid() {
		return true;
	}
	
	

}
