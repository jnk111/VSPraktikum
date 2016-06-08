package vs.jan.helper.brokerservice;

import java.net.HttpURLConnection;
import java.util.Map;

import vs.jan.exception.ResourceNotFoundException;
import vs.jan.helper.Helper;
import vs.jan.json.brokerservice.JSONAccount;
import vs.jan.json.brokerservice.JSONGameURI;
import vs.jan.model.ServiceList;
import vs.jan.model.Validable;
import vs.jan.model.brokerservice.Account;
import vs.jan.model.brokerservice.Broker;
import vs.jan.model.brokerservice.Place;
import vs.jan.model.exception.Error;
import vs.jan.tools.HttpService;

@SuppressWarnings("unused")
public class BrokerHelper extends Helper {

		
	public BrokerHelper(ServiceList serviceList){
		this.setServices(serviceList);
	}
	
	public Broker getBroker(Map<Broker, JSONGameURI> brokers, String gameid) {

		for (Broker b : brokers.keySet()) {
			if (b.getUri().contains(gameid)) {
				return b;
			}
		}

		throw new ResourceNotFoundException(Error.BROKER_NOT_FOUND.getMsg());
	}

	public Place getPlace(Broker b, String placeid) {

		for(Place p: b.getPlaces()){
			String id = getID(p.getPlaceUri());
			if(id.equals(placeid)){
				return p;
			}
		}

		throw new ResourceNotFoundException(Error.PLACE_NOT_FOUND.getMsg());
	}

	public JSONAccount getAccount(String accountUri) {
		String json = HttpService.get(accountUri, HttpURLConnection.HTTP_OK);
		return GSON.fromJson(json, JSONAccount.class);
	}

}
