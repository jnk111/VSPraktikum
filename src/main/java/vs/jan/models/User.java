package vs.jan.models;

/**
 * Repraesentiert einen User und ist auf Gueltigkeit pruefbar (RAML: required)
 * @author jan
 *
 */
public class User implements Validable{

		private String id;
		private String name;
		private String uri;
		
		public User(String id, String name, String uri){
			this.id = id;
			this.name = name;
			this.uri = uri;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}
		

		@Override
		public String toString() {
			
			return "ID: " + this.id + ", Name: " + this.name + ", URI: " + this.uri;
		}

		@Override
		public boolean isValid() {
			return this.getId().matches("/users/[a-z]+")
						&& this.getName() != null
							&& this.getUri() != null;
		}
		
		
		
		
}
