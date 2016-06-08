package vs.jan.model.boardservice;

import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Convertable;

public enum Place implements Convertable<JSONPlace> {

	Los("Los"), Bad("Badstrasse", 40, PlaceColors.DARK_BLUE, 1000), Gemeinschaft1("Gemeinschaftsfeld"), Turm(
			"Turmstrasse", 80, PlaceColors.DARK_BLUE,
			1000), EinkStr("Einkommenssteuer"), Suedbhf("Suedbahnhof", 200), Chaussee("Chaussestrasse", 120,
					PlaceColors.TURQUOISE, 1000), Ereignis1("Ereignisfeld"), Elisen("Elisenstrasse", 120, PlaceColors.TURQUOISE,
							1000), Post("Poststrasse", 160, PlaceColors.TURQUOISE, 1000), InJail("Gefaengnis"), See("Seestrasse", 200,
									PlaceColors.VIOLET, 2000), EWerk("Elektrizitaetswerk", 150), Hafen("Hafenstrasse", 200,
											PlaceColors.VIOLET,
											2000), Neue("Neuestrasse", 240, PlaceColors.VIOLET, 2000), Westbhf("Westbahnhof", 200), Muenchner(
													"Muenchnerstrasse", 280, PlaceColors.ORANGE,
													2000), Gemeinschaft2("Gemeinschaftsfeld"), Wiener("Wienerstrasse", 280, PlaceColors.ORANGE,
															2000), Berliner("Berlinerstrasse", 320, PlaceColors.ORANGE,
																	2000), FreiParken("Frei Parken"), Theater("Theaterstrasse", 360, PlaceColors.RED,
																			3000), Ereignis2("Ereignisfeld"), Museum("Museumstrasse", 360, PlaceColors.RED,
																					3000), Opern("Opernplatz", 400, PlaceColors.RED, 3000), Nordbhf("Nordbahnhof",
																							200), Lessing("Lessingstrasse", 440, PlaceColors.YELLOW, 3000), Schiller(
																									"Schillerstrasse", 440, PlaceColors.YELLOW,
																									3000), Wasser("Wasserwerk", 150), Goethe("Goethestrasse", 480,
																											PlaceColors.YELLOW, 3000), GoJail("Gehe ins Gefaengnis"), Rathaus(
																													"Rathausplatz", 520, PlaceColors.GREEN,
																													4000), Haupt("Hauptstrasse", 520, PlaceColors.GREEN,
																															4000), Gemeinschaft3("Gemeinschaftsfeld"), Bhf(
																																	"Bahnhofstrasse", 560, PlaceColors.GREEN,
																																	4000), Hauptbhf("Hauptbahnhof", 200), Ereignis3(
																																			"Ereignisfeld"), Park("Parkstrasse", 700,
																																					PlaceColors.DARK_BLUE, 4000), ZusatzStr(
																																							"Zusatzsteuer"), Schloss("Schlossallee",
																																									1000, PlaceColors.DARK_BLUE, 4000);

	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board
	private int price;
	private int housesPrice;
	private PlaceColors color;
	
	private Place() {
		this(null);
	}

	private Place(String name) {

		this(name, 0, null);
	}

	private Place(String name, int price) {

		this(name, price, null);
	}

	private Place(String name, int price, PlaceColors color) {

		this(name, price, color, -1);
	}

	private Place(String name, int price, PlaceColors color, int housePrice) {

		this.name = name;
		this.price = price;
		this.housesPrice = housePrice;
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

	public int getHousesPrice() {
		return housesPrice;
	}

	public void setHousesPrice(int housesPrice) {
		this.housesPrice = housesPrice;
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
	
	public vs.jan.json.brokerservice.JSONPlace convertToBrokerPlace(){
		vs.jan.json.brokerservice.JSONPlace place = new vs.jan.json.brokerservice.JSONPlace();
		place.setPlace(this.getPlaceUri());
		place.setValue(this.getPrice());
		place.setHouses(this.getHousesPrice());
		return place;
	}

	public boolean isPlace() {
		return this.price > 0;
	}

}
