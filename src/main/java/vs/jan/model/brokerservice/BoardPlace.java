package vs.jan.model.brokerservice;

import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Convertable;

public enum BoardPlace implements Convertable<JSONPlace> {

	Los("Los"), Bad("Badstrasse", 40, PlaceColor.DARK_BLUE), Gemeinschaft1("Gemeinschaftsfeld"), Turm("Turmstrasse", 80,
			PlaceColor.DARK_BLUE), EinkStr("Einkommenssteuer", 1000), Suedbhf("Suedbahnhof", 200), Chaussee("Chaussestrasse", 120,
					PlaceColor.TURQUOISE), Ereignis1("Ereignisfeld"), Elisen("Elisenstrasse", 120, PlaceColor.TURQUOISE), Post(
							"Poststrasse", 160,
							PlaceColor.TURQUOISE), InJail("Gefaengnis"), See("Seestrasse", 200, PlaceColor.VIOLET), EWerk(
									"Elektrizitaetswerk", 150), Hafen("Hafenstrasse", 200, PlaceColor.VIOLET), Neue("Neuestrasse", 240,
											PlaceColor.VIOLET), Westbhf("Westbahnhof", 200), Muenchner("Muenchnerstrasse", 280,
													PlaceColor.ORANGE), Gemeinschaft2("Gemeinschaftsfeld"), Wiener("Wienerstrasse", 280,
															PlaceColor.ORANGE), Berliner("Berlinerstrasse", 320, PlaceColor.ORANGE), FreiParken(
																	"Frei Parken"), Theater("Theaterstrasse", 360, PlaceColor.RED), Ereignis2(
																			"Ereignisfeld"), Museum("Museumstrasse", 360, PlaceColor.RED), Opern(
																					"Opernplatz", 400, PlaceColor.RED), Nordbhf("Nordbahnhof", 200), Lessing(
																							"Lessingstrasse", 440, PlaceColor.YELLOW), Schiller("Schillerstrasse",
																									440, PlaceColor.YELLOW), Wasser("Wasserwerk", 150), Goethe(
																											"Goethestrasse", 480,
																											PlaceColor.YELLOW), GoJail("Gehe ins Gefaengnis"), Rathaus(
																													"Rathausplatz", 520, PlaceColor.GREEN), Haupt("Hauptstrasse",
																															520, PlaceColor.GREEN), Gemeinschaft3(
																																	"Gemeinschaftsfeld"), Bhf("Bahnhofstrasse", 560,
																																			PlaceColor.GREEN), Hauptbhf("Hauptbahnhof",
																																					200), Ereignis3("Ereignisfeld"), Park(
																																							"Parkstrasse", 700,
																																							PlaceColor.DARK_BLUE), ZusatzStr(
																																									"Zusatzsteuer", 1000), Schloss(
																																											"Schlossallee", 1000,
																																											PlaceColor.DARK_BLUE);

	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board
	private int price;
	private int houses;
	private PlaceColor color;

	private BoardPlace() {
		this(null);
	}

	private BoardPlace(String name) {

		this(name, 0);
	}

	private BoardPlace(String name, int price) {

		this(name, price, null);
	}

	private BoardPlace(String name, int price, PlaceColor color) {

		this.name = name;
		this.price = price;
		this.houses = 0;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrokerUri() {
		return brokerUri;
	}

	public void setBrokerUri(String brokerUri) {
		this.brokerUri = brokerUri;
	}

	public String getPlaceUri() {
		return placeUri;
	}

	public void setPlaceUri(String placeUri) {
		this.placeUri = placeUri;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getHouses() {
		return houses;
	}

	public void setHouses(int housesPrice) {
		this.houses = housesPrice;
	}

	public PlaceColor getColor() {
		return color;
	}

	public void setColor(PlaceColor color) {
		this.color = color;
	}

	@Override
	public JSONPlace convert() {

		return new JSONPlace(this.name, this.brokerUri);
	}

	public vs.jan.json.brokerservice.JSONPlace convertToBrokerPlace() {
		vs.jan.json.brokerservice.JSONPlace place = new vs.jan.json.brokerservice.JSONPlace();
		place.setPlace(this.getPlaceUri());
		place.setValue(this.getPrice());
		place.setHouses(this.getHouses());
		return place;
	}

	public boolean isPlace() {
		return this.price > 0 && this != EinkStr && this != ZusatzStr;
	}

	public boolean isJail() {

		return GoJail == this;
	}

	public boolean isChance() {

		return this == Ereignis1 || this == Ereignis2 || this == Ereignis3;
	}

	public boolean isCommunity() {

		return this == Gemeinschaft1 || this == Gemeinschaft2 || this == Gemeinschaft3;
	}

	public boolean isTax() {
		
		return this == EinkStr || this == ZusatzStr;
	}
}
