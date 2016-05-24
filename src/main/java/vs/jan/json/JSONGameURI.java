package vs.jan.json;

public class JSONGameURI {

	private String game;

	public JSONGameURI(String gameUri) {

		this.game = gameUri;
	}

	public String getURI() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}

	/**
	 * Prueft ob die uebergebene GameUri gueltig ist Die <code>gameid</code> darf
	 * nicht null sein
	 * 
	 * @return <code>true</code> : gueltig, <code>false</code> unguelitg
	 */
	public boolean isValid() {
		return this.getURI() != null;
	}
}
