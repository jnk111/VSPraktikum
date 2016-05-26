package vs.jan.json;

import com.google.gson.Gson;

import vs.jan.model.Convertable;
import vs.jan.model.Service;

public class JSONService implements Convertable<Service>{

	/**
	 * EXAMPLE: ========================================== "_uri":
	 * "/services/164", "description": "Event service of group 7", "name": "bla",
	 * "service": "events", "status": "dead", "uri":
	 * "http://141.22.65.58:4567/events"
	 * ==========================================
	 */

	private String _uri;
	private String description;
	private String name;
	private String service;
	private String status;
	private String uri;

	public JSONService() {

		this(null, null, null, null, null, null);
	}

	public JSONService(String _uri, String description, String name, String service, String status, String uri) {

		this._uri = _uri;
		this.description = description;
		this.name = name;
		this.service = service;
		this.status = status;
		this.uri = uri;
	}

	public String get_uri() {
		return _uri;
	}

	public void set_uri(String _uri) {
		this._uri = _uri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String toString() {
		return new Gson().toJson(this);
	}

	@Override
	public Service convert() {
		String relUri = this.get_uri();
		String description = this.getDescription();
		String name = this.getName();
		String service = this.getService();
		String status  = this.getStatus();
		String fullUri = this.getUri();
		return new Service(relUri, description, name, service, status, fullUri);
	}

}
