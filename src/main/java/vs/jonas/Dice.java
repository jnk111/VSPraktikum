package vs.jonas;

public class Dice {

	private int number;
	private String player;
	private String game;

	private Dice(String player, String game) {
		roll();
		this.player = player;
		this.game = game;
	}
	
	public static Dice create(String player, String game) {
		return new Dice(player, game);
	}

	public void roll(){
		number = (int) ((Math.random() * 6) + 1);
	}

	public int getNumber() {
		return number;
	}
	
	public String getPlayer(){
		return player;
	}
	
	public String getGame(){
		return game;
	}

}
