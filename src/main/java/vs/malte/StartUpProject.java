package vs.malte;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import spark.Spark;
import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.broker.BrokerAPI;
import vs.jan.api.decksservice.DecksAPI;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.jan.services.yellowpages.RegisterService;
import vs.malte.example.Example;
import vs.malte.services.GamesServiceAPI;

public class StartUpProject
{
    private static final boolean LOCAL = true;

    public static void main( String[] args ) throws UnknownHostException
    {
        new GamesServiceAPI();
        
        if ( LOCAL )
        {
            new BrokerAPI();
            new DecksAPI();
            new BoardRESTApi();
            new UserServiceRESTApi();
            BankService.run();

            try
            {
                TimeUnit.SECONDS.sleep( 1 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }

             Example.runExample01( "http://localhost:4567" );
        }
        
        // Hier aufpassen -> sonst wird lokaler Service angemeldet (nur im VPN)
        if(!LOCAL) {
          Spark.awaitInitialization();
      		String ip = Inet4Address.getLocalHost().getHostAddress();
      		RegisterService.registerService("games", ip, true);
        }

    }
}
