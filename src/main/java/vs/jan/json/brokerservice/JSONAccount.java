package vs.jan.json.brokerservice;

public class JSONAccount {
	
	private String player;
	private int saldo;
	
	public JSONAccount(){
		
	}
	
	
	public JSONAccount(String player){
		this(player, 0);
	}
	
	public JSONAccount(JSONAccount acc) {
		this(acc.getPlayer(), acc.getSaldo());
	}
	
	public JSONAccount(String player, int saldo) {
		super();
		this.player = player;
		this.saldo = saldo;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + saldo;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JSONAccount other = (JSONAccount) obj;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (saldo != other.saldo)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONAccount [player=" + player + ", saldo=" + saldo + "]";
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
