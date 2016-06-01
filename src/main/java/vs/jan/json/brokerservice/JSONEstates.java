package vs.jan.json.brokerservice;

import java.util.ArrayList;
import java.util.List;

public class JSONEstates {
	
	private List<String> estates;

	
	public JSONEstates(){
		this(new ArrayList<>());
	}
	
	public JSONEstates(List<String> estates) {
		super();
		this.estates = estates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estates == null) ? 0 : estates.hashCode());
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
		JSONEstates other = (JSONEstates) obj;
		if (estates == null) {
			if (other.estates != null)
				return false;
		} else if (!estates.equals(other.estates))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JSONEstates [estates=" + estates + "]";
	}

	public List<String> getEstates() {
		return estates;
	}

	public void setEstates(List<String> estates) {
		this.estates = estates;
	}
	
	
	
	

}
