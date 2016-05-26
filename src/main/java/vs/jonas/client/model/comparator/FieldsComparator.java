package vs.jonas.client.model.comparator;

import java.util.Comparator;

import vs.jonas.client.json.Field;

public class FieldsComparator implements Comparator<Field>{

	@Override
	public int compare(Field arg0, Field arg1) {
		// TODO Auto-generated method stub
		int field1 = 0;
		int field2 = 0;
		try{
			field1 = Integer.valueOf(arg0.getID());
			field2 = Integer.valueOf(arg1.getID());
		} catch(Exception ex){
			
		}
		return Integer.compare(field1, field2);
	}

}
