package vs.jan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vs.jonas.services.model.Dice;

public class Throws {
	
	private Map<Pawn, List<Dice>> throwMap;
	
	public Throws(){
		throwMap = new HashMap<>();
	}
	
	
	public void addThrow(Pawn pawn, Dice roll){
		
		if(throwMap.containsKey(pawn)){
			throwMap.get(pawn).add(roll);
		}else{
			throwMap.put(pawn, new ArrayList<>(Arrays.asList(roll)));
		}
	}

	
	public List<Dice> getThrows(Pawn pawn){
		return throwMap.get(pawn);
	}

	public Map<Pawn, List<Dice>> getThrowMap() {
		return throwMap;
	}


	public void setThrowMap(Map<Pawn, List<Dice>> throwMap) {
		this.throwMap = throwMap;
	}
	
	
	

}
