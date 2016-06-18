package vs.jan.model.boardservice;

public enum PlaceColor {

	DARK_BLUE("Dunkelblau"), LIGHT_BLUE("Hellblau"), VIOLET("Violet"), ORANGE("Orange"), RED("Rot"), YELLOW(
			"Gelb"), GREEN("Gruen"), TURQUOISE("Türkis");

	private String farbe;
	
	private PlaceColor(String farbe){
		this.farbe = farbe;
	}

	public String getFarbe() {
		return farbe;
	}

	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}
	
	public String getColor() {
		return this.farbe;
	}
	
	public PlaceColor getColor(String name) {
		return null;
	}
	
	
}
