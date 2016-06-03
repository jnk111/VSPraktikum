package vs.jan.services.run.restopoly;

import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.broker.BrokerAPI;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.malte.services.GamesServiceAPI;

public class RestopolyRun {

	public static void main(String[] args) {
		
		BankService.run();
		new UserServiceRESTApi();
		new GamesServiceAPI();
		new BoardRESTApi();
		new BrokerAPI();

	}

}
