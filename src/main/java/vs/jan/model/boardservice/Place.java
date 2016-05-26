package vs.jan.model.boardservice;

import vs.jan.json.boardservice.JSONPlace;
import vs.jan.model.Convertable;

public enum Place implements Convertable<JSONPlace> {

	Los("Los"), Bad("Badstrasse"), Gemeinschaft1("Gemeinschaftsfeld"), Turm("Turmstrasse"), EinkStr(
			"Einkommenssteuer"), Suedbhf("Suedbahnhof"), Chaussee("Chaussestrasse"), Ereignis1("Ereignisfeld"), Elisen(
					"Elisenstrasse"), Post("Poststrasse"), InJail("Gefaengnis"), See("Seestrasse"), EWerk(
							"Elektrizitaetswerk"), Hafen("Hafenstrasse"), Neue("Neuestrasse"), Westbhf("Westbahnhof"), Muenchner(
									"Muenchnerstrasse"), Gemeinschaft2("Gemeinschaftsfeld"), Wiener("Wienerstrasse"), Berliner(
											"Berlinerstrasse"), FreiParken("Frei Parken"), Theater("Theaterstrasse"), Ereignis2(
													"Ereignisfeld"), Museum("Museumstrasse"), Opern("Opernplatz"), Nordbhf(
															"Nordbahnhof"), Lessing("Lessingstrasse"), Schiller("Schillerstrasse"), Wasser(
																	"Wasserwerk"), Goethe("Goethestrasse"), GoJail("Gehe ins Gefaengnis"), Rathaus(
																			"Rathausplatz"), Haupt("Hauptstrasse"), Gemeinschaft3("Gemeinschaftsfeld"), Bhf(
																					"Bahnhofstrasse"), Hauptbhf("Hauptbahnhof"), Ereignis3("Ereignisfeld"), Park(
																							"Parkstrasse"), ZusatzStr("Zusatzsteuer"), Schloss("Schlossallee");

	private String name;
	private String brokerUri;
	private String placeUri; // Uri to the place on the board

	private Place() {

	}

	private Place(String name) {

		this.name = name;
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

	@Override
	public JSONPlace convert() {

		return new JSONPlace(this.name, this.brokerUri);
	}

	@Override
	public String toString() {

		return "Name: " + this.name + ", URI: " + this.placeUri + ", Broker: " + this.brokerUri;
	}
}
