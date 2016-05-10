package vs.jonas;

public class Start {
	public static void main(String[] args) {
		RestopolyClient client = new RestopolyClient();
		new MenuController(client);
	}
}
