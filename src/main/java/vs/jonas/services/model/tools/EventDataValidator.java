package vs.jonas.services.model.tools;

import vs.jonas.services.json.EventData;

public class EventDataValidator implements Validator<EventData>{

	@Override
	// TODO Schoener waere es ansich, wenn hier eine Exception geworfen wird, damit man weiﬂ,
	// was genau in dem uebergebenen JSON schief gelaufen ist.
	public boolean isValidRessource(EventData ressource) {
		if(ressource.getGame() != null	&& ressource.getType() != null
				&& ressource.getName() != null && ressource.getReason() != null
				&& ressource.getRessource() != null && ressource.getPlayer() != null){
			return true;
		}
		return false;
	}

}
