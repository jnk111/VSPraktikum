package vs.jan.model.boardservice;

import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Convertable;

public enum Place implements Convertable<JSONPlace> {

	Los("Los"), Bad("Badstrasse", 40, PlaceColors.DARK_BLUE), Gemeinschaft1("Gemeinschaftsfeld"), Turm("Turmstrasse", 80,
			PlaceColors.DARK_BLUE), EinkStr("Einkommenssteuer"), Suedbhf("Suedbahnhof", 200), Chaussee("Chaussestrasse", 120,
					PlaceColors.TURQUOISE), Ereignis1("Ereignisfeld"), Elisen("Elisenstrasse", 120, PlaceColors.TURQUOISE), Post(
							"Poststrasse", 160,
							PlaceColors.TURQUOISE), InJail("Gefaengnis"), See("Seestrasse", 200, PlaceColors.VIOLET), EWerk(
									"Elektrizitaetswerk", 150), Hafen("Hafenstrasse", 200, PlaceColors.VIOLET), Neue("Neuestrasse", 240,
											PlaceColors.VIOLET), Westbhf("Westbahnhof", 200), Muenchner("Muenchnerstrasse", 280,
													PlaceColors.ORANGE), Gemeinschaft2("Gemeinschaftsfeld"), Wiener("Wienerstrasse", 280,
															PlaceColors.ORANGE), Berliner("Berlinerstrasse", 320, PlaceColors.ORANGE), FreiParken(
																	"Frei Parken"), Theater("Theaterstrasse", 360, PlaceColors.RED), Ereignis2(
																			"Ereignisfeld"), Museum("Museumstrasse", 360, PlaceColors.RED), Opern(
																					"Opernplatz", 400, PlaceColors.RED), Nordbhf("Nordbahnhof", 200), Lessing(
																							"Lessingstrasse", 440, PlaceColors.YELLOW), Schiller("Schillerstrasse",
																									440, PlaceColors.YELLOW), Wasser("Wasserwerk", 150), Goethe(
																											"Goethestrasse", 480,
																											PlaceColors.YELLOW), GoJail("Gehe ins Gefaengnis"), Rathaus(
																													"Rathausplatz", 520, PlaceColors.GREEN), Haupt("Hauptstrasse",
																															520, PlaceColors.GREEN), Gemeinschaft3(
																																	"Gemeinschaftsfeld"), Bhf("Bahnhofstrasse", 560,
																																			PlaceColors.GREEN), Hauptbhf("Hauptbahnhof",
																																					200), Ereignis3("Ereignisfeld"), Park(
																																							"Parkstrasse", 700,
																																							PlaceColors.DARK_BLUE), ZusatzStr(
																																									"Zusatzsteuer"), Schloss(
																																											"Schlossallee", 1000,
																																											PlaceColors.DARK_BLUE);

	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board
	private int price;
	private int houses;
	private PlaceColors color;

	private Place() {
		this(null);
	}

	private Place(String name) {

		this(name, 0);
	}

	private Place(String name, int price) {

		this(name, price, null);
	}

	private Place(String name, int price, PlaceColors color) {

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

	public PlaceColors getColor() {
		return color;
	}

	public void setColor(PlaceColors color) {
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
		return this.price > 0 && this != EinkStr;
	}

	public boolean isJail() {

		return GoJail == this;
	}

	public boolean isChance() {

		return this == Place.Ereignis1 || this == Place.Ereignis2 || this == Place.Ereignis3;
	}

	public boolean isCommunity() {

		return this == Place.Gemeinschaft1 || this == Place.Gemeinschaft2 || this == Place.Gemeinschaft3;
	}
}
