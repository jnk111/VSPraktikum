package vs.jan.json.boardservice;

public class JSONThrowsURI {

	private String rollUri;

	public JSONThrowsURI(String rollUri) {
		this.rollUri = rollUri;
	}

	public String getRollUri() {
		return rollUri;
	}

	public void setRollUri(String rollUri) {
		this.rollUri = rollUri;
	}

	@Override
	public int hashCode() {

		return this.getRollUri().hashCode() * 42;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj instanceof JSONThrowsURI) {
			JSONThrowsURI uri = (JSONThrowsURI) obj;
			return uri.getRollUri().equals(this.getRollUri());
		}

		return false;
	}

}
