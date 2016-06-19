package vs.jonas.client.utils;

public class JailReasonGenerator {

	private final static String[] reasons = {"hat den Keks aus der Dose geklaut und wurde deswegen ins Gefaengnis geschickt."
			,"hatte etwas mit der Tochter vom Polizeipraesidenten und wurde deswegen ins Gefaengnis geschickt."
			,"war zur falschen Zeit am falschen Ort und wurde deswegen ins Gefaengnis geschickt."
			,"wollte seine Mitspieler beklauen und wurde deswegen ins Gefaengnis geschickt. "
			,"wurde zu seiner eigenen Sicherheit ins Gefaengnis geschickt. Wir wollen dir nur helfen."
			,"hatte es nicht anders verdient und wurde ins Gefaengnis geschickt."};
	
	public static String getRandomReason() {
		int number = (int) ((Math.random() * 6) + 1);
		return reasons[number];
	}

}
