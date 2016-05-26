package vs.jonas.client.json;

import java.util.List;

public class FieldList {

	List<Field> fields;
	
	public FieldList(List<Field> fields){
		this.fields = fields;
	}
	
	public List<Field> getFields(){
		return fields;
	}
}
