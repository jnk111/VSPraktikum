package vs.jonas.client.json;

public class Account {

	String player; // uri
	int saldo;
	
	public Account(String player, int saldo){
		this.player = player;
		this.saldo = saldo;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getSaldo() {
		return saldo;
	}

	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}
	
	
}
