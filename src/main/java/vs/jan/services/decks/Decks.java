package vs.jan.services.decks;

import java.util.Stack;

import vs.jan.model.Convertable;

public class Decks implements Convertable<JSONDecks>{

	private String uri;
	private String commUri;
	private String chanceUri;
	private Stack<ChanceCard> chanceCards;
	private Stack<CommCard> commCards;
	private Stack<ChanceCard> chanceCardBackup;
	private Stack<CommCard> commCardBackup;
	
	
	public Decks(String uri, String commUri, String chanceUri) {
		super();
		this.uri = uri;
		this.commUri = commUri;
		this.chanceUri = chanceUri;
		initCards();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chanceUri == null) ? 0 : chanceUri.hashCode());
		result = prime * result + ((commUri == null) ? 0 : commUri.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Decks other = (Decks) obj;
		if (chanceUri == null) {
			if (other.chanceUri != null)
				return false;
		} else if (!chanceUri.equals(other.chanceUri))
			return false;
		if (commUri == null) {
			if (other.commUri != null)
				return false;
		} else if (!commUri.equals(other.commUri))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	


	@Override
	public String toString() {
		return "Decks [uri=" + uri + ", commUri=" + commUri + ", chanceUri=" + chanceUri + ", chanceCards=" + chanceCards
				+ ", commCards=" + commCards + "]";
	}



	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public String getCommUri() {
		return commUri;
	}


	public void setCommUri(String commUri) {
		this.commUri = commUri;
	}


	public String getChanceUri() {
		return chanceUri;
	}


	public void setChanceUri(String chanceUri) {
		this.chanceUri = chanceUri;
	}


	@Override
	public JSONDecks convert() {
		
		JSONDecks decks = new JSONDecks();
		decks.setId(this.getUri());
		decks.setChance(this.getChanceUri());
		decks.setCommunity(this.getCommUri());
		return decks;
	}
	
	
	private void initCards() {
		this.chanceCards = new Stack<>();
		this.commCards = new Stack<>();
		this.commCardBackup = new Stack<>();
		this.chanceCardBackup = new Stack<>();
		
		for(ChanceCard c: ChanceCard.values()) {
			chanceCards.push(c);
		}
		
		for(CommCard c: CommCard.values()) {
			commCards.push(c);
		}
	}


	public CommCard getNextCommCard() {
		CommCard next = null;
		
		if(!this.commCards.isEmpty()) {
			 next = this.commCards.pop();
			 this.commCardBackup.push(next);
		} else {
			this.commCards = this.commCardBackup;
			this.commCardBackup = new Stack<>();
			return getNextCommCard();
		}
		return next;
	}
	
	public ChanceCard getNextChanceCard() {
		
		ChanceCard next = null;
		if(!this.chanceCards.isEmpty()) {
			 next = this.chanceCards.pop();
			 this.chanceCardBackup.push(next);
		} else {
			this.chanceCards = this.chanceCardBackup;
			this.chanceCardBackup = new Stack<>();
			return getNextChanceCard();
		}
		return next;
	}

}
