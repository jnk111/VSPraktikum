package vs.jan.model.brokerservice;

import vs.jan.model.boardservice.Player;

public class Account {

	private Player player;
	private int saldo;
	private String accUri;
	
	public Account(Player player, int saldo, String accUri) {
		
		this.player = player;
		this.saldo = saldo;
		this.accUri = accUri;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accUri == null) ? 0 : accUri.hashCode());
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
		Account other = (Account) obj;
		if (accUri == null) {
			if (other.accUri != null)
				return false;
		} else if (!accUri.equals(other.accUri))
			return false;
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
		return "Account [player=" + player + ", saldo=" + saldo + ", accUri=" + accUri + "]";
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getSaldo() {
		return saldo;
	}

	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}

	public String getAccUri() {
		return accUri;
	}

	public void setAccUri(String accUri) {
		this.accUri = accUri;
	}
}
