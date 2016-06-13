package vs.malte;

import java.util.concurrent.TimeUnit;

import vs.gerriet.service.BankService;
import vs.jan.api.boardservice.BoardRESTApi;
import vs.jan.api.broker.BrokerAPI;
import vs.jan.api.decksservice.DecksAPI;
import vs.jan.api.userservice.UserServiceRESTApi;
import vs.malte.example.Example;
import vs.malte.services.GamesServiceAPI;

public class StartUpProject
{
    private static final boolean LOCAL = true;

    public static void main( String[] args )
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
    }
}
