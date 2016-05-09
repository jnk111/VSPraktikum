package vs.aufgabe1b.models;

import vs.aufgabe1b.interfaces.Validator;

public class EventValidator implements Validator<Event>{

	@Override
	// TODO Schöner wäre es ansich, wenn hier eine Exception geworfen wird, damit man weiß,
	// was genau in dem übergebenen JSON schief gelaufen ist.
	public boolean isValidRessource(Event ressource) {
		if((ressource.getId() != null && ressource.getId().matches("events/[a-z]+"))
				&& ressource.getGame() != null && ressource.getType() != null
				&& ressource.getName() != null && ressource.getReason() != null){
			return true;
		}
		return false;
	}

}
