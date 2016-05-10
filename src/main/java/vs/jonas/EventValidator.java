package vs.jonas;

public class EventValidator implements Validator<Event>{

	@Override
	// TODO Sch�ner w�re es ansich, wenn hier eine Exception geworfen wird, damit man wei�,
	// was genau in dem �bergebenen JSON schief gelaufen ist.
	public boolean isValidRessource(Event ressource) {
		if((ressource.getId() != null && ressource.getId().matches("events/[a-z]+"))
				&& ressource.getGame() != null && ressource.getType() != null
				&& ressource.getName() != null && ressource.getReason() != null){
			return true;
		}
		return false;
	}

}
