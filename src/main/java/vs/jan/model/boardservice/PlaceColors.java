package vs.jan.model.boardservice;

public enum PlaceColors {

	DARK_BLUE("Dunkelblau"), LIGHT_BLUE("Hellblau"), VIOLET("Violet"), ORANGE("Orange"), RED("Rot"), YELLOW(
			"Gelb"), GREEN("Gruen"), TURQUOISE("Türkis");

	private String farbe;
	
	private PlaceColors(String farbe){
		this.farbe = farbe;
	}

	public String getFarbe() {
		return farbe;
	}

	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}
	
	public PlaceColors getColor() {
		return null;
	}
	
	
}
