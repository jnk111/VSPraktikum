package vs.jan.model.boardservice;

import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Convertable;

public enum Place implements Convertable<JSONPlace> {

	Los("Los"), Bad("Badstrasse", 60), Gemeinschaft1("Gemeinschaftsfeld"), Turm("Turmstrasse",
			60), EinkStr("Einkommenssteuer"), Suedbhf("Suedbahnhof", 200), Chaussee("Chaussestrasse", 100), Ereignis1(
					"Ereignisfeld"), Elisen("Elisenstrasse", 100), Post("Poststrasse", 120), InJail("Gefaengnis"), See(
							"Seestrasse", 140), EWerk("Elektrizitaetswerk", 150), Hafen("Hafenstrasse", 140), Neue("Neuestrasse",
									160), Westbhf("Westbahnhof", 200), Muenchner("Muenchnerstrasse", 180), Gemeinschaft2(
											"Gemeinschaftsfeld"), Wiener("Wienerstrasse", 180), Berliner("Berlinerstrasse", 200), FreiParken(
													"Frei Parken"), Theater("Theaterstrasse", 220), Ereignis2("Ereignisfeld"), Museum(
															"Museumstrasse", 220), Opern("Opernplatz", 240), Nordbhf("Nordbahnhof", 200), Lessing(
																	"Lessingstrasse", 260), Schiller("Schillerstrasse", 260), Wasser("Wasserwerk",
																			150), Goethe("Goethestrasse", 280), GoJail("Gehe ins Gefaengnis"), Rathaus(
																					"Rathausplatz", 300), Haupt("Hauptstrasse", 300), Gemeinschaft3(
																							"Gemeinschaftsfeld"), Bhf("Bahnhofstrasse", 320), Hauptbhf("Hauptbahnhof",
																									200), Ereignis3("Ereignisfeld"), Park("Parkstrasse",
																											350), ZusatzStr("Zusatzsteuer"), Schloss("Schlossallee", 400);

	private final static int HOUSE_PRICE = 50;

	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board
	private int price;
	private int housesPrice;

	private Place() {
		this(null);
	}

	private Place(String name) {

		this(name, 0);
	}

	private Place(String name, int price) {

		this(name, price, HOUSE_PRICE);
	}

	private Place(String name, int price, int housePrice) {

		this.name = name;
		this.price = price;
		this.housesPrice = housePrice;
		
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

	public int getHousesPrice() {
		return housesPrice;
	}

	public void setHousesPrice(int housesPrice) {
		this.housesPrice = housesPrice;
	}

	public int getHOUSE_PRICE() {
		return HOUSE_PRICE;
	}

	@Override
	public JSONPlace convert() {

		return new JSONPlace(this.name, this.brokerUri);
	}

	public boolean isPlace() {
		return this.price > 0;
	}

}
